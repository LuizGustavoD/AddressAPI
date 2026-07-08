# Relatório de Testes de Integração e Segurança (End-to-End)

Este documento registra as evidências de execução dos testes funcionais e de segurança na arquitetura de microsserviços. Os testes validam o fluxo completo desde o Nginx (WAF), passando pelo API Gateway, até a persistência nos microsserviços internos.

---

## 1. Resumo Executivo dos Testes

| ID | Caso de Teste | Endpoint Testado | Protocolo | Status | Resultado Esperado | Resultado Obtido |
|:---|:---|:---|:---:|:---:|:---|:---|
| **FT-01** | Cadastro de Usuário | `POST /api/auth/register` | HTTP | **PASSOU** | Retornar HTTP 201 e persistir usuário inativo. | Retornado HTTP 201. |
| **FT-02** | Confirmação de Cadastro | `GET /api/auth/confirm` | HTTP | **PASSOU** | Ativar conta do usuário via link/token do Mailpit. | Retornado HTTP 200. |
| **FT-03** | Login e Emissão de Token | `POST /api/auth/login` | HTTP | **PASSOU** | Retornar HTTP 200 com JWT access/refresh token válidos. | Retornado HTTP 200 com tokens. |
| **FT-04** | Criação de Pessoa Física | `POST /api/persons/physical` | HTTP | **PASSOU** | Autenticar JWT, buscar chaves JWKS e salvar pessoa + endereço. | Retornado HTTP 201 com dados persistidos. |
| **FT-05** | Consulta de Pessoa Física | `GET /api/persons/physical/{id}`| HTTP | **PASSOU** | Buscar e retornar dados da pessoa cadastrada. | Retornado HTTP 200 com a carga útil correta. |
| **FT-06** | Exceção Customizada: Conflito (Duplicidade) | `POST /api/persons/physical` | HTTP | **PASSOU** | Retornar HTTP 409 caso tente cadastrar CPF duplicado. | Retornado HTTP 409 com mensagem customizada. |
| **FT-07** | Exceção Customizada: Não Encontrado | `GET /api/persons/physical/{id}` | HTTP | **PASSOU** | Retornar HTTP 404 para ID inexistente no banco. | Retornado HTTP 404 com mensagem customizada. |
| **FT-08** | Exceção Customizada: Validação de Dados | `POST /api/persons/physical` | HTTP | **PASSOU** | Retornar HTTP 400 informando falha de validação em campos. | Retornado HTTP 400 com detalhes do campo nulo. |
| **ST-01** | Bloqueio de Payload Malicioso (WAF) | `POST /api/auth/login` | HTTP | **PASSOU** | ModSecurity deve bloquear IP numérico e cabeçalhos em falta. | Retornado HTTP 403 Forbidden pelo WAF. |
| **ST-02** | Isolamento de Rede de Backend | Portas internas (`8080`, `8082`) | TCP | **PASSOU** | Conexões diretas do host local devem ser recusadas. | Conexão recusada em todas as portas internas. |
| **ST-03** | WAF: Bloqueio de SQL Injection | `GET /api/persons/physical/{id}` | HTTP | **PASSOU** | Detectar injeção de SQL em parâmetros e bloquear a chamada. | Retornado HTTP 403 (SQLi detectado via libinjection). |
| **ST-04** | WAF: Bloqueio de Cross-Site Scripting (XSS) | `GET /api/persons/physical/{id}` | HTTP | **PASSOU** | Detectar tags `<script>` e payloads XSS e bloquear chamada. | Retornado HTTP 403 (XSS Script Tag Vector detectado). |
| **ST-05** | WAF: Bloqueio de Path Traversal (LFI) | `GET /api/persons/physical/{id}` | HTTP | **PASSOU** | Detectar padrões `/../` e tentativa de acessar arquivos do SO. | Retornado HTTP 403 (Path Traversal /etc/passwd detectado). |

---

## 2. Evidências de Testes Funcionais (End-to-End)

### FT-01: Cadastro de Usuário (Register)
**Comando executado:**
```bash
curl.exe -iv -d '{"username":"luiztest","email":"luiztest@example.com","password":"Password123!"}' \
  -H "Content-Type: application/json" \
  -X POST http://localhost/api/auth/register
```

**Resultado Obtido (HTTP 201 Created):**
```json
{
  "status": 201,
  "success": true,
  "data": null,
  "message": "User registered successfully. Please confirm your email.",
  "timestamp": "2026-07-08T18:32:15.123Z"
}
```

---

### FT-02: Confirmação de E-mail (Activation)
**Comando executado:**
```bash
curl.exe -iv "http://localhost/api/auth/confirm?token=67b7e289-49ee-47da-8181-420215ff368a"
```
*(Token extraído dos logs de mensagens capturados pelo Mailpit na porta 1025).*

**Resultado Obtido (HTTP 200 OK):**
```json
{
  "status": 200,
  "success": true,
  "data": null,
  "message": "Email confirmed successfully",
  "timestamp": "2026-07-08T18:33:04.456Z"
}
```

---

### FT-03: Login e Obtenção do Token JWT
**Comando executado:**
```bash
curl.exe -iv -d '{"username":"luiztest","password":"Password123!"}' \
  -H "Content-Type: application/json" \
  -X POST http://localhost/api/auth/login
```

**Resultado Obtido (HTTP 200 OK):**
```json
{
  "status": 200,
  "success": true,
  "data": {
    "accessToken": "eyJraWQiOiJhdXRoLWtleS1pZCIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJiNzQ2NDE2Mi1jMGE5LTRjYWQtYTlmMC1iMTMwNDQ4NWY0MTYiLCJhdWQiOlsiYWRkcmVzcy1zZXJ2aWNlIiwibWFpbC1zZXJ2aWNlIl0sInRva2VuX3VzZSI6ImFjY2VzcyIsImlzcyI6ImF1dGgtc2VydmljZSIsImV4cCI6MTc4MzUzOTU4NCwiaWF0IjoxNzgzNTM4Njg0LCJqdGkiOiI4NTRkZWU3ZC05NzhjLTRlMTQtYWYyMi1jNGY0NDZiNDQwMGEiLCJlbWFpbCI6Imx1aXp0ZXN0QGV4YW1wbGUuY29tIiwidXNlcm5hbWUiOiJsdWl6dGVzdCJ9.cKqi3WOK...",
    "refreshToken": "eyJraWQiOiJhdXRoLWtleS1pZCIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJiNzQ2...",
    "expiresIn": 900
  },
  "message": "Login successful",
  "timestamp": "2026-07-08T19:24:44.636Z"
}
```

---

### FT-04: Criação de Pessoa Física (Com Endereço e Contato)
**Comando executado:**
```bash
curl.exe -iv -d '{
  "name": "Luiz Silva",
  "cpf": "12345678901",
  "addresses": [
    {
      "street": "Rua das Flores",
      "number": 100,
      "city": "Sao Paulo",
      "cep": "01000-000",
      "state": "SP"
    }
  ],
  "contact": {
    "userId": "b7464162-c0a9-4cad-a9f0-b1304485f416",
    "name": "Luiz Contact",
    "email": "luizcontact@example.com",
    "phone": "11999999999"
  }
}' \
-H "Content-Type: application/json" \
-H "Authorization: Bearer <FT-03_ACCESS_TOKEN>" \
-X POST http://localhost/api/persons/physical
```

**Resultado Obtido (HTTP 201 Created):**
```json
{
  "status": 201,
  "success": true,
  "data": {
    "id": "464d3c7d-bc23-436e-a9aa-edadadeb1d8a",
    "name": "Luiz Silva",
    "addresses": [
      {
        "id": "948c86b7-31ee-4ef1-8ff4-47109c02bc6a",
        "street": "Rua das Flores",
        "number": 100,
        "city": "Sao Paulo",
        "cep": "01000-000",
        "state": "SP"
      }
    ],
    "contact": {
      "id": "70610760-23f2-498b-905d-45aa1eff88f1",
      "userId": "b7464162-c0a9-4cad-a9f0-b1304485f416",
      "name": "Luiz Contact",
      "email": "luizcontact@example.com",
      "phone": "11999999999"
    },
    "cpf": "12345678901"
  },
  "message": "Physical person created successfully",
  "timestamp": "2026-07-08T19:25:37.452Z"
}
```

---

### FT-05: Consulta de Pessoa Física
**Comando executado:**
```bash
curl.exe -iv -H "Authorization: Bearer <FT-03_ACCESS_TOKEN>" \
  http://localhost/api/persons/physical/464d3c7d-bc23-436e-a9aa-edadadeb1d8a
```

**Resultado Obtido (HTTP 200 OK):**
```json
{
  "status": 200,
  "success": true,
  "data": {
    "id": "464d3c7d-bc23-436e-a9aa-edadadeb1d8a",
    "name": "Luiz Silva",
    "addresses": [
      {
        "id": "948c86b7-31ee-4ef1-8ff4-47109c02bc6a",
        "street": "Rua das Flores",
        "number": 100,
        "city": "Sao Paulo",
        "cep": "01000-000",
        "state": "SP"
      }
    ],
    "contact": {
      "id": "70610760-23f2-498b-905d-45aa1eff88f1",
      "userId": "b7464162-c0a9-4cad-a9f0-b1304485f416",
      "name": "Luiz Contact",
      "email": "luizcontact@example.com",
      "phone": "11999999999"
    },
    "cpf": "12345678901"
  },
  "message": "Physical person retrieved successfully",
  "timestamp": "2026-07-08T19:25:47.736Z"
}
```

---

### FT-06: Exceção Customizada: Conflito (Duplicidade)
**Comando executado:**
*(Tentativa de cadastrar a mesma pessoa física com CPF já registrado no FT-04)*
```bash
curl.exe -s -d @scratch/create_person.json -H "Content-Type: application/json" \
  -H "Authorization: Bearer <FT-03_ACCESS_TOKEN>" \
  -X POST http://localhost/api/persons/physical
```

**Resultado Obtido (HTTP 409 Conflict):**
```json
{
  "status": 409,
  "success": false,
  "message": "CPF already exists: 12345678901",
  "path": "/api/persons/physical",
  "timestamp": "2026-07-08T19:41:18.942Z"
}
```

---

### FT-07: Exceção Customizada: Não Encontrado
**Comando executado:**
*(Consulta de ID inexistente com formato UUID válido)*
```bash
curl.exe -s -H "Authorization: Bearer <FT-03_ACCESS_TOKEN>" \
  http://localhost/api/persons/physical/00000000-0000-0000-0000-000000000000
```

**Resultado Obtido (HTTP 404 Not Found):**
```json
{
  "status": 404,
  "success": false,
  "message": "Physical person not found: 00000000-0000-0000-0000-000000000000",
  "path": "/api/persons/physical/00000000-0000-0000-0000-000000000000",
  "timestamp": "2026-07-08T19:41:28.160Z"
}
```

---

### FT-08: Exceção Customizada: Validação de Dados
**Comando executado:**
*(Envio de payload com restrição de validação violada, ex: name em branco)*
```bash
curl.exe -s -d @scratch/validation_error.json -H "Content-Type: application/json" \
  -H "Authorization: Bearer <FT-03_ACCESS_TOKEN>" \
  -X POST http://localhost/api/persons/physical
```

**Resultado Obtido (HTTP 400 Bad Request):**
```json
{
  "status": 400,
  "success": false,
  "message": "name: must not be blank",
  "path": "/api/persons/physical",
  "timestamp": "2026-07-08T19:41:56.889Z"
}
```

---

## 3. Evidências de Testes de Segurança (ModSecurity WAF & Isolamento)

### ST-01: Bloqueio de Requisição não-conforme (ModSecurity CRS)
**Comando executado:**
```bash
curl.exe -iv -X POST http://127.0.0.1/api/auth/login
```
*(Bate diretamente usando o IP numérico no cabeçalho Host e sem enviar corpo).*

**Resultado Obtido (HTTP 403 Forbidden):**
```html
HTTP/1.1 403 Forbidden
Server: nginx
Date: Wed, 08 Jul 2026 19:16:05 GMT
Content-Type: text/html
Content-Length: 146
Connection: keep-alive

<html>
<head><title>403 Forbidden</title></head>
<body>
<center><h1>403 Forbidden</h1></center>
<hr><center>nginx</center>
</body>
</html>
```

---

### ST-02: Isolamento de Rede de Backend (Bypass Prevention)
**Comandos executados da máquina host:**
```bash
# Tentativa de acesso direto à porta do gateway
curl.exe http://localhost:8080/api/auth/login

# Tentativa de acesso direto à porta do resource-service
curl.exe http://localhost:8082/api/persons/physical
```

**Resultado Obtido:**
```text
curl: (7) Failed to connect to localhost port 8080 after 2045 ms: Connection refused
curl: (7) Failed to connect to localhost port 8082 after 2038 ms: Connection refused
```
*(Confirmado que nenhuma porta interna está exposta diretamente para fora da rede privada do Docker, obrigando a passagem pelo Nginx WAF).*

---

### ST-03: WAF: Bloqueio de SQL Injection
**Comando executado:**
*(Requisição contendo payload clássico de SQL Injection em parâmetros de query)*
```bash
curl.exe -iv -H "Authorization: Bearer <FT-03_ACCESS_TOKEN>" \
  "http://localhost/api/persons/physical/464d3c7d-bc23-436e-a9aa-edadadeb1d8a?id=1%20OR%201=1"
```

**Resultado Obtido (HTTP 403 Forbidden):**
```html
HTTP/1.1 403 Forbidden
Server: nginx
Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS

<html>
<head><title>403 Forbidden</title></head>
<body>
<center><h1>403 Forbidden</h1></center>
<hr><center>nginx</center>
</body>
</html>
```

**Registro de Auditoria do ModSecurity (Log do Container Nginx WAF):**
```text
ModSecurity: Access denied with code 403 (phase 2).
Matched "Operator `Ge' with parameter `5' against variable `TX:ANOMALY_SCORE' (Value: `5') [file "/etc/modsecurity.d/owasp-crs/rules/REQUEST-949-BLOCKING-EVALUATION.conf"] [line "81"] [id "949110"]
[msg "Inbound Anomaly Score Exceeded (Total Score: 5)"]
[message "SQL Injection Attack Detected via libinjection"] [ruleId "942100"] [data "Matched Data: 1&1 found within ARGS:id: 1 OR 1=1"]
```

---

### ST-04: WAF: Bloqueio de Cross-Site Scripting (XSS)
**Comando executado:**
*(Requisição enviando tags HTML/Script via parâmetro de busca)*
```bash
curl.exe -iv -H "Authorization: Bearer <FT-03_ACCESS_TOKEN>" \
  "http://localhost/api/persons/physical/464d3c7d-bc23-436e-a9aa-edadadeb1d8a?name=%3Cscript%3Ealert(%27XSS%27)%3C/script%3E"
```

**Resultado Obtido (HTTP 403 Forbidden):**
```html
HTTP/1.1 403 Forbidden
Server: nginx

<html>
<head><title>403 Forbidden</title></head>
<body>
<center><h1>403 Forbidden</h1></center>
<hr><center>nginx</center>
</body>
</html>
```

**Registro de Auditoria do ModSecurity (Log do Container Nginx WAF):**
```text
ModSecurity: Access denied with code 403 (phase 2).
Matched "Operator `Ge' with parameter `5' against variable `TX:ANOMALY_SCORE' (Value: `15') [file "/etc/modsecurity.d/owasp-crs/rules/REQUEST-949-BLOCKING-EVALUATION.conf"] [line "81"] [id "949110"]
[msg "Inbound Anomaly Score Exceeded (Total Score: 15)"]
[message "XSS Attack Detected via libinjection"] [ruleId "941100"] [data "Matched Data: XSS data found within ARGS:name: <script>alert('XSS')</script>"]
[message "XSS Filter - Category 1: Script Tag Vector"] [ruleId "941110"] [data "Matched Data: <script> found"]
```

---

### ST-05: WAF: Bloqueio de Path Traversal (LFI)
**Comando executado:**
*(Requisição contendo caminhos relativos de diretórios para ler arquivos sensíveis do SO)*
```bash
curl.exe -iv -H "Authorization: Bearer <FT-03_ACCESS_TOKEN>" \
  "http://localhost/api/persons/physical/464d3c7d-bc23-436e-a9aa-edadadeb1d8a?file=../../../../etc/passwd"
```

**Resultado Obtido (HTTP 403 Forbidden):**
```html
HTTP/1.1 403 Forbidden
Server: nginx

<html>
<head><title>403 Forbidden</title></head>
<body>
<center><h1>403 Forbidden</h1></center>
<hr><center>nginx</center>
</body>
</html>
```

**Registro de Auditoria do ModSecurity (Log do Container Nginx WAF):**
```text
ModSecurity: Access denied with code 403 (phase 2).
Matched "Operator `Ge' with parameter `5' against variable `TX:ANOMALY_SCORE' (Value: `30') [file "/etc/modsecurity.d/owasp-crs/rules/REQUEST-949-BLOCKING-EVALUATION.conf"] [line "81"] [id "949110"]
[msg "Inbound Anomaly Score Exceeded (Total Score: 30)"]
[message "Path Traversal Attack (/../)"] [ruleId "930100"] [data "Matched Data: /../ found within ARGS:file"]
[message "OS File Access Attempt"] [ruleId "930120"] [data "Matched Data: etc/passwd found"]
[message "Remote Command Execution: Unix Shell Code Found"] [ruleId "932160"] [data "Matched Data: etc/passwd found"]
```
