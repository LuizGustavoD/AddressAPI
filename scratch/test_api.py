import urllib.request
import urllib.error
import json
import time
import uuid
import re

import os
BASE_URL = os.environ.get("BASE_URL", "http://localhost")
MAILPIT_URL = os.environ.get("MAILPIT_URL", "http://localhost:8025/api/v1/messages")
REPORT_PATH = "test_report.md"

report_lines = []

def log_to_report(text):
    report_lines.append(text)
    print(text)

def log_test_header(title):
    log_to_report(f"\n## {title}\n")

def log_http_transaction(method, url, request_headers, request_body, status, response_headers, response_body):
    log_to_report("### HTTP Transaction")
    log_to_report(f"**Request:** `{method} {url}`")
    if request_headers:
        log_to_report("**Request Headers:**")
        log_to_report("```json")
        log_to_report(json.dumps(request_headers, indent=2))
        log_to_report("```")
    if request_body:
        log_to_report("**Request Body:**")
        log_to_report("```json")
        log_to_report(json.dumps(request_body, indent=2) if isinstance(request_body, dict) else str(request_body))
        log_to_report("```")
    log_to_report(f"**Response Status:** `{status}`")
    if response_headers:
        log_to_report("**Response Headers (Partial):**")
        log_to_report("```json")
        # Keep only key headers
        key_headers = {k: v for k, v in response_headers.items() if k.lower() in ['content-type', 'server', 'x-api-key', 'connection']}
        log_to_report(json.dumps(key_headers, indent=2))
        log_to_report("```")
    if response_body:
        log_to_report("**Response Body:**")
        log_to_report("```json")
        try:
            parsed = json.loads(response_body)
            log_to_report(json.dumps(parsed, indent=2))
        except Exception:
            log_to_report(str(response_body))
        log_to_report("```")
    log_to_report("\n---\n")

def make_request(method, url, headers=None, body=None):
    if headers is None:
        headers = {}
    
    data = None
    if body is not None:
        if isinstance(body, dict) or isinstance(body, list):
            data = json.dumps(body).encode('utf-8')
            if 'Content-Type' not in headers:
                headers['Content-Type'] = 'application/json'
        else:
            data = str(body).encode('utf-8')

    req = urllib.request.Request(url, data=data, headers=headers, method=method)
    
    status_code = None
    response_body = ""
    resp_headers = {}
    
    try:
        with urllib.request.urlopen(req, timeout=60) as response:
            status_code = response.status
            response_body = response.read().decode('utf-8')
            resp_headers = dict(response.info())
    except urllib.error.HTTPError as e:
        status_code = e.code
        response_body = e.read().decode('utf-8')
        resp_headers = dict(e.info())
    except urllib.error.URLError as e:
        status_code = "URL ERROR"
        response_body = str(e.reason)
    
    log_http_transaction(method, url, headers, body, status_code, resp_headers, response_body)
    return status_code, response_body

def fetch_mailpit_token(email):
    log_to_report(f"Polly Mailpit for confirmation email sent to {email}...")
    for _ in range(5):
        time.sleep(2)
        try:
            req = urllib.request.Request(MAILPIT_URL, method="GET")
            with urllib.request.urlopen(req, timeout=5) as response:
                data = json.loads(response.read().decode('utf-8'))
                messages = data.get("messages", [])
                for msg in messages:
                    # check if the subject matches and the body contains the email
                    msg_id = msg.get("ID")
                    # fetch detail to inspect email body
                    detail_req = urllib.request.Request(f"{MAILPIT_URL.replace('/messages', '/message')}/{msg_id}", method="GET")
                    with urllib.request.urlopen(detail_req, timeout=5) as detail_resp:
                        detail = json.loads(detail_resp.read().decode('utf-8'))
                        body_text = detail.get("Text", "") + detail.get("HTML", "")
                        if email in body_text:
                            # Extract token
                            match = re.search(r"token=([A-Za-z0-9-_=\.]+)", body_text)
                            if match:
                                token = match.group(1)
                                log_to_report(f"Successfully retrieved confirmation token from Mailpit: {token[:20]}...")
                                return token
        except Exception as e:
            log_to_report(f"Error checking Mailpit: {str(e)}")
    raise RuntimeError(f"Could not retrieve verification email for {email} from Mailpit.")

def run_tests():
    log_to_report("# Test Execution Report - API & WAF Verification\n")
    log_to_report(f"**Date:** {time.strftime('%Y-%m-%d %H:%M:%S')}")
    log_to_report("**Host:** `http://localhost` (Nginx ModSecurity WAF Gateway)\n")
    
    test_user_email = f"test_{uuid.uuid4().hex[:6]}@example.com"
    test_user_password = "Password123!"
    test_user_name = f"test_user_{uuid.uuid4().hex[:4]}"
    
    # ----------------------------------------------------
    # 1. USER REGISTRATION
    # ----------------------------------------------------
    log_test_header("1. User Registration Flow")
    reg_body = {
        "username": test_user_name,
        "email": test_user_email,
        "password": test_user_password,
        "confirmPassword": test_user_password
    }
    status, body = make_request("POST", f"{BASE_URL}/auth/api/auth/register", body=reg_body)
    assert status == 201, "Registration failed"
    
    # ----------------------------------------------------
    # 2. EMAIL CONFIRMATION (ACTIVATION)
    # ----------------------------------------------------
    log_test_header("2. Email Confirmation via Mailpit")
    activation_token = fetch_mailpit_token(test_user_email)
    
    # Trigger confirm endpoint
    status, body = make_request("GET", f"{BASE_URL}/auth/api/auth/confirm?token={activation_token}")
    assert status == 200, "Confirmation failed"
    
    # ----------------------------------------------------
    # 3. USER LOGIN
    # ----------------------------------------------------
    log_test_header("3. User Login")
    login_body = {
        "email": test_user_email,
        "password": test_user_password
    }
    status, body = make_request("POST", f"{BASE_URL}/auth/api/auth/login", body=login_body)
    assert status == 200, "Login failed"
    
    login_response = json.loads(body)
    access_token = login_response["data"]["accessToken"]
    refresh_token = login_response["data"]["refreshToken"]
    
    auth_headers = {
        "Authorization": f"Bearer {access_token}"
    }
    
    # ----------------------------------------------------
    # 4. PROFILE RETRIEVAL (GET /api/v1/me)
    # ----------------------------------------------------
    log_test_header("4. Access User Profile")
    status, body = make_request("GET", f"{BASE_URL}/address/api/v1/me", headers=auth_headers)
    assert status == 200, "Could not fetch user profile"
    
    profile_response = json.loads(body)
    user_id = profile_response["data"]["userId"]
    
    # ----------------------------------------------------
    # 5. CRUD PHYSICAL PERSON (/api/persons/physical)
    # ----------------------------------------------------
    log_test_header("5. CRUD - Physical Person")
    
    person_contact = {
        "userId": user_id,
        "name": "Physical Person Contact",
        "email": f"phys_contact_{uuid.uuid4().hex[:4]}@example.com",
        "phone": "5511999999999"
    }
    
    person_address = {
        "street": "Avenida Paulista",
        "number": 1000,
        "city": "São Paulo",
        "cep": "01310-100",
        "state": "SP"
    }
    
    physical_person_body = {
        "name": "John Doe Physical",
        "cpf": f"00000000{uuid.uuid4().hex[:3]}",  # generate unique CPF string
        "addresses": [person_address],
        "contact": person_contact
    }
    
    # CREATE (POST)
    status, body = make_request("POST", f"{BASE_URL}/address/api/persons/physical", headers=auth_headers, body=physical_person_body)
    assert status == 200, "Physical person creation failed"
    
    created_person = json.loads(body)["data"]
    person_id = created_person["id"]
    
    # READ (GET)
    status, body = make_request("GET", f"{BASE_URL}/address/api/persons/physical/{person_id}", headers=auth_headers)
    assert status == 200, "Physical person retrieval failed"
    
    # UPDATE (PUT)
    updated_person_body = physical_person_body.copy()
    updated_person_body["name"] = "John Doe Physical Updated"
    status, body = make_request("PUT", f"{BASE_URL}/address/api/persons/physical/{person_id}", headers=auth_headers, body=updated_person_body)
    assert status == 200, "Physical person update failed"
    
    # DELETE
    status, body = make_request("DELETE", f"{BASE_URL}/address/api/persons/physical/{person_id}", headers=auth_headers)
    assert status == 200, "Physical person deletion failed"
    
    # READ AFTER DELETE (EXPECT 404 or Error)
    log_to_report("#### Verify Deletion (Expected to fail/404)")
    status, body = make_request("GET", f"{BASE_URL}/address/api/persons/physical/{person_id}", headers=auth_headers)
    
    # ----------------------------------------------------
    # 6. CRUD JURIDIC PERSON (/api/persons/juridic)
    # ----------------------------------------------------
    log_test_header("6. CRUD - Juridic Person")
    
    juridic_person_body = {
        "name": "Acme Corp Juridic",
        "cnpj": f"0000000000{uuid.uuid4().hex[:4]}", # unique CNPJ string
        "addresses": [person_address],
        "contact": person_contact
    }
    
    # CREATE (POST)
    status, body = make_request("POST", f"{BASE_URL}/address/api/persons/juridic", headers=auth_headers, body=juridic_person_body)
    assert status == 200, "Juridic person creation failed"
    
    created_juridic = json.loads(body)["data"]
    juridic_id = created_juridic["id"]
    
    # READ (GET)
    status, body = make_request("GET", f"{BASE_URL}/address/api/persons/juridic/{juridic_id}", headers=auth_headers)
    assert status == 200, "Juridic person retrieval failed"
    
    # UPDATE (PUT)
    updated_juridic_body = juridic_person_body.copy()
    updated_juridic_body["name"] = "Acme Corp Juridic Updated"
    status, body = make_request("PUT", f"{BASE_URL}/address/api/persons/juridic/{juridic_id}", headers=auth_headers, body=updated_juridic_body)
    assert status == 200, "Juridic person update failed"
    
    # DELETE
    status, body = make_request("DELETE", f"{BASE_URL}/address/api/persons/juridic/{juridic_id}", headers=auth_headers)
    assert status == 200, "Juridic person deletion failed"
    
    # ----------------------------------------------------
    # 7. CRUD CONTACTS (/api/contacts)
    # ----------------------------------------------------
    log_test_header("7. CRUD - Contacts")
    
    contact_body = {
        "userId": user_id,
        "name": "Jane Contact",
        "email": f"jane_{uuid.uuid4().hex[:4]}@example.com",
        "phone": "5511888888888"
    }
    
    # CREATE (POST)
    status, body = make_request("POST", f"{BASE_URL}/address/api/contacts", headers=auth_headers, body=contact_body)
    assert status == 201, "Contact creation failed"
    
    created_contact = json.loads(body)["data"]
    contact_id = created_contact["id"]
    
    # READ (GET)
    status, body = make_request("GET", f"{BASE_URL}/address/api/contacts/{contact_id}", headers=auth_headers)
    assert status == 200, "Contact retrieval failed"
    
    # UPDATE (PUT)
    updated_contact_body = contact_body.copy()
    updated_contact_body["name"] = "Jane Contact Updated"
    status, body = make_request("PUT", f"{BASE_URL}/address/api/contacts/{contact_id}", headers=auth_headers, body=updated_contact_body)
    assert status == 200, "Contact update failed"
    
    # DELETE
    status, body = make_request("DELETE", f"{BASE_URL}/address/api/contacts/{contact_id}", headers=auth_headers)
    assert status == 200, "Contact deletion failed"
    
    # ----------------------------------------------------
    # 8. CRUD ADDRESSES (/api/addresses)
    # ----------------------------------------------------
    log_test_header("8. CRUD - Addresses")
    
    # Create a dummy physical person to link the address
    dummy_person_body = {
        "name": "Dummy Person for Address Test",
        "cpf": f"99999999{uuid.uuid4().hex[:3]}",
        "addresses": [person_address],
        "contact": person_contact
    }
    status, body = make_request("POST", f"{BASE_URL}/address/api/persons/physical", headers=auth_headers, body=dummy_person_body)
    assert status == 200
    dummy_person_id = json.loads(body)["data"]["id"]
    
    address_body = {
        "street": "Avenida Rebouças",
        "number": 500,
        "city": "São Paulo",
        "cep": "05401-000",
        "state": "SP"
    }
    
    # CREATE (POST)
    status, body = make_request("POST", f"{BASE_URL}/address/api/addresses/person/{dummy_person_id}", headers=auth_headers, body=address_body)
    assert status == 201, "Address creation failed"
    
    created_address = json.loads(body)["data"]
    address_id = created_address["id"]
    
    # READ (GET)
    status, body = make_request("GET", f"{BASE_URL}/address/api/addresses/{address_id}", headers=auth_headers)
    assert status == 200, "Address retrieval failed"
    
    # UPDATE (PUT)
    updated_address_body = address_body.copy()
    updated_address_body["street"] = "Avenida Rebouças Updated"
    status, body = make_request("PUT", f"{BASE_URL}/address/api/addresses/{address_id}/person/{dummy_person_id}", headers=auth_headers, body=updated_address_body)
    assert status == 200, "Address update failed"
    
    # DELETE
    status, body = make_request("DELETE", f"{BASE_URL}/address/api/addresses/{address_id}", headers=auth_headers)
    assert status == 200, "Address deletion failed"
    
    # ----------------------------------------------------
    # 9. EXCEPTION & STANDARDIZED ERROR PAYLOADS
    # ----------------------------------------------------
    log_test_header("9. Exception Handling & Standard Error Responses")
    
    # Invalid Login (Bad credentials)
    log_to_report("#### Invalid Login Credentials")
    bad_login = {"email": test_user_email, "password": "WrongPassword!"}
    status, body = make_request("POST", f"{BASE_URL}/auth/api/auth/login", body=bad_login)
    
    # Validation Error (Missing fields)
    log_to_report("#### Validation Failure (Empty name on register)")
    bad_register = {"email": "bad@example.com", "password": "Pass", "username": "", "confirmPassword": ""}
    status, body = make_request("POST", f"{BASE_URL}/auth/api/auth/register", body=bad_register)
    
    # Resource Not Found
    log_to_report("#### Resource Not Found (GET invalid contact ID)")
    status, body = make_request("GET", f"{BASE_URL}/address/api/contacts/{uuid.uuid4()}", headers=auth_headers)
    
    # ----------------------------------------------------
    # 10. MODSECURITY WAF SECURITY TEST
    # ----------------------------------------------------
    log_test_header("10. Web Application Firewall (ModSecurity WAF) Security Tests")
    
    # WAF rule 1: Enforce disallowed HTTP method
    log_to_report("#### WAF - Block Disallowed HTTP Method (PATCH on login)")
    status, body = make_request("PATCH", f"{BASE_URL}/auth/api/auth/login")
    
    # WAF rule 2: Block malformed authorization header format
    log_to_report("#### WAF - Block Malformed Authorization Header (Basic Auth on protected route)")
    waf_bad_auth = {"Authorization": "Basic bXl1c2VyOm15cGFzcw=="}
    status, body = make_request("GET", f"{BASE_URL}/address/api/v1/me", headers=waf_bad_auth)
    
    # WAF rule 3: Block SQL Injection attempt in authentication parameters
    log_to_report("#### WAF - Block SQL Injection Attack on login body")
    sql_inj_body = {
        "email": "admin' OR 1=1 --",
        "password": "password"
    }
    status, body = make_request("POST", f"{BASE_URL}/auth/api/auth/login", body=sql_inj_body)

    # Write report
    with open(REPORT_PATH, "w", encoding="utf-8") as f:
        f.write("\n".join(report_lines))
    print(f"Report written successfully to {REPORT_PATH}")

if __name__ == "__main__":
    run_tests()
