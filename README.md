# Guia de Arquitetura de Rede e Segurança

Este documento descreve detalhadamente a arquitetura de rede, segurança e o fluxo de requisições do projeto após as correções para conformidade arquitetural.

---

## 1. Fluxo do Ciclo de Vida da Requisição

Todas as requisições externas seguem estritamente o fluxo abaixo, passando pelo Nginx (WAF), API Gateway e depois pelos microsserviços internos.

```
Requisição (Cliente) 
       │
       ▼
┌──────────────┐
│  Nginx WAF   │ (ModSecurity CRS) ──► Rejeita se houver anomalias/ataques
└──────┬───────┘
       │ (Apenas requisições legítimas)
       ▼
┌──────────────┐
│ API Gateway  │ (Spring Cloud Gateway WebMVC) ──► Roteamento e balanceamento
└──────┬───────┘
       │
       ├─────────────────────────┬─────────────────────────┐
       ▼                         ▼                         ▼
┌──────────────┐          ┌──────────────┐          ┌──────────────┐
│ auth-server  │          │resource-serv │          │ mail-service │ (Internal-only)
└──────────────┘          └──────────────┘          └──────────────┘
```

1. **Cliente** inicia uma requisição direcionada à porta `80` (HTTP) exposta no host.
2. A requisição bate no container **Nginx WAF (ModSecurity)**.
   - O ModSecurity analisa os cabeçalhos, corpo e parâmetros da requisição contra o Core Rule Set (CRS) e as regras customizadas.
   - Se for detectado comportamento malicioso (como SQL Injection ou ausência de `Content-Length` em requisições POST), a requisição é **bloqueada imediatamente (HTTP 403)**.
3. Se a requisição for legítima, o Nginx atua como proxy reverso e encaminha para o **API Gateway** na porta interna `8080` (resolvida pelo nome de host `api-gateway`).
4. O **API Gateway** recebe a requisição e avalia os predicados de caminho:
   - Se começar com `/api/auth/**`, encaminha para o `auth-server:8080`.
   - Se começar com `/api/**` (como `/api/addresses`), encaminha para o `resource-service:8082`.
5. O microsserviço de destino processa a requisição e retorna a resposta por todo o caminho reverso.

---

## 2. Modelo de Isolamento de Rede (Docker Networks)

Para garantir que o API Gateway e os microsserviços **não possam ser acessados diretamente**, ignorando o WAF, a comunicação é segmentada em duas redes virtuais no Docker Compose:

### Rede `public-net` (Pública)
- **Objetivo:** Canal de entrada do tráfego web controlado.
- **Membros:** 
  - `nginx_waf` (expõe a porta `80:8080` para o host).
  - `api-gateway` (não expõe portas para o host).

### Rede `private-net` (Privada)
- **Objetivo:** Comunicação de backend e isolamento de banco de dados.
- **Membros:**
  - `api-gateway` (funciona como ponte entre as duas redes).
  - `auth-server` (sem portas expostas ao host).
  - `resource-service` (sem portas expostas ao host).
  - `mail-service` (sem portas expostas ao host).
  - `auth_db` (banco de dados MySQL).
  - `mailpit` (ferramenta de e-mail de desenvolvimento).
  - `db_visualizer` (phpMyAdmin).

> [!NOTE]
> Nenhum container de microsserviço ou o próprio gateway expõe portas diretamente para a máquina host em ambiente de execução. Desta forma, é fisicamente impossível para um atacante ou cliente na rede externa burlar o Nginx e contatar diretamente os serviços internos.

---

## 3. Integração e Regras do ModSecurity (WAF)

O Nginx utiliza a imagem oficial `owasp/modsecurity-crs:nginx` configurada em modo bloqueante (`paranoia_level=1`). Os arquivos de regras customizadas foram mapeados no diretório oficial de regras do CRS (`/opt/owasp-crs/rules/`), onde são carregados dinamicamente na inicialização do serviço.

As regras customizadas de segurança incluem:

### Servidor de Autenticação (`auth_server.conf`)
- **Regras 10001 & 10002:** Forçam o uso do método `POST` para chamadas aos endpoints de login (`/api/auth/login`) e cadastro (`/api/auth/register`). Chamadas usando GET ou outros métodos são sumariamente bloqueadas com HTTP 405.
- **Regra 10003:** Inspeção profunda de corpo de requisição na rota de login para bloquear padrões de injeção de SQL (`SQL Injection`) óbvios, retornando HTTP 400.

### Servidor de Recursos (`resurce_server.conf`)
- **Regra 20001:** Restringe os métodos HTTP válidos para as rotas `/api/persons`, `/api/contacts` e `/api/addresses` a apenas `GET`, `POST`, `PUT` e `DELETE`.
- **Regra 20002:** Exige que a requisição forneça um cabeçalho `Authorization` válido, iniciando obrigatoriamente com o prefixo `Bearer ` (case-insensitive), prevenindo requisições não formatadas para endpoints sensíveis.

---

## 4. Vulnerabilidades Mitigadas

A nova arquitetura mitiga diretamente as seguintes falhas de segurança comuns:

1. **Bypass de Controle de Segurança (Edge Bypass):** Ao remover as seções `ports` do `auth-server`, `resource-service`, `mail-service` e do gateway do `compose.yaml`, removeu-se a capacidade de interagir com eles diretamente pelas portas `8080`, `8081`, `8082` e `8090` de fora da rede Docker.
2. **Exploração de Nomes de Host Inválidos (Tomcat Underscore Issue):** A alteração do nome do serviço de `api_gateway` (com underline) para `api-gateway` (com hífen) corrige a conformidade com a RFC 1123, eliminando o erro de *Bad Request (HTTP 400)* gerado pela validação estrita de cabeçalho `Host` do Tomcat.
3. **Erros de Sintaxe de Proxy:** Correção da diretiva `proxy_pass` do Nginx, que estava colocada incorretamente fora do bloco `location /`, gerando falhas fatais na inicialização do servidor web.
4. **Vazamento de Requisições Maliciosas:** Mapeamento correto do ModSecurity para processar a cadeia completa de regras do CRS em vez de apenas um arquivo de regras isolado de XSS.

---

## 5. Como Executar e Validar

### Inicialização do Ambiente
Certifique-se de estar na raiz do projeto e execute:
```powershell
# Limpa containers e redes anteriores
docker compose down --remove-orphans

# Reconstrói as imagens modificadas (Gateway e WAF)
docker compose build

# Inicia todos os serviços em segundo plano
docker compose up -d
```

### Comandos de Validação de Segurança

#### 1. Validar Roteamento de Requisição Legítima (Passa pelo WAF e Gateway)
Enviar uma requisição HTTP POST formatada corretamente para o endpoint de login. Ela deve alcançar o microsserviço de autenticação e falhar apenas pelas credenciais ausentes (HTTP 400 customizado do backend):
```powershell
curl.exe -iv -d "{}" -H "Content-Type: application/json" -X POST http://localhost/api/auth/login
```
*Saída Esperada:* HTTP 400 com JSON contendo `"message":"username: Username cannot be blank"`.

#### 2. Validar Bloqueio de Segurança por Protocolo (WAF Inbound Anomaly)
Tentar fazer uma chamada POST usando o endereço IP numérico (`127.0.0.1`) e sem corpo/tamanho definido. O ModSecurity CRS irá disparar as regras de bloqueio de IP numérico e ausência de Content-Length:
```powershell
curl.exe -iv -X POST http://127.0.0.1/api/auth/login
```
*Saída Esperada:* HTTP 403 Forbidden (com cabeçalho `Server: nginx`).

#### 3. Validar Isolamento de Portas de Backend
Tentar acessar diretamente o Gateway ou os microsserviços pelas portas internas através do host local:
```powershell
# Tentar contato direto com o Gateway
curl.exe http://localhost:8080/api/auth/login

# Tentar contato direto com o Servidor de Autenticação
curl.exe http://localhost:8080/actuator
```
*Saída Esperada:* Falha na conexão/Conexão recusada (provando que as portas estão completamente bloqueadas externamente).

---

## 6. Log de Testes End-to-End (Fluxo Completo)

Para validar a integridade funcional de ponta a ponta e garantir que a autenticação, validação de tokens JWT, sincronização de chaves JWKS e inserções no banco de dados estão operando perfeitamente através da rede de isolamento e do WAF, o seguinte fluxo completo foi executado e verificado com sucesso:

### Passo 1: Cadastro de Usuário (Register)
Cadastra um novo usuário no sistema através do WAF e do Gateway.
```bash
curl.exe -iv -d '{"username":"luiztest","email":"luiztest@example.com","password":"Password123!"}' -H "Content-Type: application/json" -X POST http://localhost/api/auth/register
```
*Resposta Esperada (201 Created):*
```json
{"status":201,"success":true,"message":"User registered successfully. Please confirm your email.","timestamp":"..."}
```

### Passo 2: Confirmação de E-mail (Activation)
Um token de ativação é enviado para o Mailpit (`http://localhost:8025`). Ao ler o log de e-mail, confirmamos a conta chamando o endpoint de ativação:
```bash
curl.exe -iv "http://localhost/api/auth/confirm?token=<TOKEN_EXTRAIDO_DO_MAILPIT>"
```
*Resposta Esperada (200 OK):*
```json
{"status":200,"success":true,"message":"Email confirmed successfully","timestamp":"..."}
```

### Passo 3: Login e Obtenção do Token JWT
Realiza o login com o usuário ativado para obter um token JWT válido:
```bash
curl.exe -iv -d '{"username":"luiztest","password":"Password123!"}' -H "Content-Type: application/json" -X POST http://localhost/api/auth/login
```
*Resposta Esperada (200 OK):*
```json
{
  "status":200,
  "success":true,
  "data":{
    "accessToken":"eyJraWQiOiJhdXRo...",
    "refreshToken":"eyJraWQiOiJhdXRo...",
    "expiresIn":900
  },
  "message":"Login successful"
}
```

### Passo 4: Criação de Pessoa Física (Resource Service)
Insere uma nova pessoa física com endereço e contato aninhados utilizando o token de acesso obtido (validação de assinatura via JWKS e persistência em banco de dados ativa):
```bash
curl.exe -iv -d '{"name": "Luiz Silva", "cpf": "12345678901", "addresses": [{"street": "Rua das Flores", "number": 100, "city": "Sao Paulo", "cep": "01000-000", "state": "SP"}], "contact": {"userId": "b7464162-c0a9-4cad-a9f0-b1304485f416", "name": "Luiz Contact", "email": "luizcontact@example.com", "phone": "11999999999"}}' -H "Content-Type: application/json" -H "Authorization: Bearer <SEU_ACCESS_TOKEN>" -X POST http://localhost/api/persons/physical
```
*Resposta Esperada (201 Created):*
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
  "message": "Physical person created successfully"
}
```

### Passo 5: Recuperação de Dados Cadastrados
Recupera a pessoa física pelo ID gerado:
```bash
curl.exe -iv -H "Authorization: Bearer <SEU_ACCESS_TOKEN>" http://localhost/api/persons/physical/464d3c7d-bc23-436e-a9aa-edadadeb1d8a
```
*Resposta Esperada (200 OK):*
```json
{
  "status": 200,
  "success": true,
  "data": {
    "id": "464d3c7d-bc23-436e-a9aa-edadadeb1d8a",
    "name": "Luiz Silva",
    "addresses": [...],
    "contact": {...},
    "cpf": "12345678901"
  },
  "message": "Physical person retrieved successfully"
}
```

