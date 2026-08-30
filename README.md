# 🏦 INTEGRATE_SERVICES_JAVA_API — Ecossistema Fintech Integrado

[![Java 17](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot 3.4.3](https://img.shields.io/badge/Spring%20Boot-3.4.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Data JPA](https://img.shields.io/badge/Spring%20Data-JPA-blue.svg)](https://spring.io/projects/spring-data-jpa)
[![H2 Database](https://img.shields.io/badge/Database-H2-yellow.svg)](https://www.h2database.com/)
[![JWT Auth0](https://img.shields.io/badge/Auth-JWT%20(Auth0)-black.svg)](https://github.com/auth0/java-jwt)
[![Tests](https://img.shields.io/badge/Tests-JUnit%205%20%7C%20MockMvc-green.svg)](https://junit.org/junit5/)

Uma API RESTful completa e unificada desenvolvida em **Java 17** e **Spring Boot 3**, integrando 6 desafios técnicos de backend em um ecossistema coeso de **Banco Digital / Plataforma Financeira**.

---

## 📌 Visão Geral do Ecossistema

O projeto conecta 6 domínios técnicos que operavam isoladamente, criando um ciclo de vida financeiro completo para o usuário:

```text
  ┌─────────────────────────────────────────────────────────────┐
  │ 👤 1. ONBOARDING (/api/auth/register)                       │
  │    Validação de Senha Forte com Regras (SenhaSegura)        │
  └──────────────────────────────┬──────────────────────────────┘
                                 ▼
  ┌─────────────────────────────────────────────────────────────┐
  │ 🗄️ 2. PERSISTÊNCIA CENTRAL                                  │
  │    Entidade User salva na base H2 (Renda, Idade, CPF, Local)│
  └──────────────────────────────┬──────────────────────────────┘
                                 ▼
  ┌─────────────────────────────────────────────────────────────┐
  │ 🔑 3. AUTENTICAÇÃO (/api/auth/login)                        │
  │    Emissão de Token JWT Real (Auth0)                        │
  └──────────────────────────────┬──────────────────────────────┘
                                 ▼
  ┌─────────────────────────────────────────────────────────────┐
  │ 🛡️ 4. INTERCEPTOR DE SEGURANÇA (AuthenticationInterceptor)   │
  │    Decodifica JWT e Injeta ID do Usuário na Requisição       │
  └──────────────────────────────┬──────────────────────────────┘
                                 ▼
  ┌─────────────────────────────────────────────────────────────┐
  │                  💼 5. OPERAÇÕES PROTEGIDAS                 │
  ├──────────────────────────────┬──────────────────────────────┤
  │ 📊 ANÁLISE DE CRÉDITO        │ 💳 TRANSAÇÕES CRIPTOGRAFADAS  │
  │    (/api/loans/me)           │    (/api/transactions)       │
  │    Lê perfil do BD e oferta  │    AES (CPF em repouso)      │
  │    linhas pré-aprovadas      │    RSA (Cartão de Crédito)   │
  ├──────────────────────────────┴──────────────────────────────┤
  │ 📍 AGÊNCIAS E CAIXAS: /pois/nearby (Busca Euclidiana GPS)   │
  │ ✂️ INDIQUE E GANHE:   /shorten-url (Links Curtos Base62)    │
  └─────────────────────────────────────────────────────────────┘
```

---

## 🚀 O Fluxo do Usuário (Ciclo Fintech)

1. **Onboarding (`SenhaSegura`)**: O cliente se cadastra (`/api/auth/register`). A API valida requisitos estritos de segurança da senha (mínimo de 8 caracteres, maiúsculas, minúsculas, números e caracteres especiais).
2. **Login & Sessão (`Autenticação`)**: O cliente faz login (`/api/auth/login`) e recebe um **Token JWT** contendo suas credenciais e claims assinadas.
3. **Análise de Crédito Inteligente (`Empréstimo`)**: O cliente autenticado solicita crédito (`/api/loans/me`). A API busca seus dados cadastrais (renda, idade) no banco e determina automaticamente as linhas pré-aprovadas (*Pessoal*, *Garantia* ou *Consignado*).
4. **Pagamentos Seguros (`Criptografia`)**: O cliente realiza transações financeiras (`/api/transactions`). Os dados sensíveis são gravados com criptografia em repouso:
   - **CPF/Documento**: Criptografado com **AES (Simétrico)**.
   - **Cartão de Crédito**: Criptografado com **RSA (Assimétrico / Par de Chaves)**.
5. **Busca de Agências e Caixas Físicos (`PontoGps`)**: O cliente precisa de atendimento presencial e consulta caixas eletrônicos próximos à sua coordenada (`/pois/nearby`).
6. **Indique e Ganhe / Comprovantes (`EncurtadorUrl`)**: O cliente gera links curtos e rastreáveis para convidar amigos ou compartilhar recibos (`/shorten-url`).

---

## 🛠️ Tecnologias Utilizadas

- **Java 17 (LTS)**
- **Spring Boot 3.4.3**
  - Spring Web (MVC)
  - Spring Data JPA
  - Spring Validation (Bean Validation / Hibernate Validator)
- **H2 In-Memory Database** (para desenvolvimento e testes ágeis)
- **Auth0 Java-JWT (4.4.0)** (geração e verificação de tokens)
- **Jasypt / Java Cryptography Extension (JCE)** (AES-CBC e RSA)
- **JUnit 5, Mockito & MockMvc** (testes unitários e testes de integração de ponta a ponta)
- **Maven** (gerenciamento de dependências e build)

---

## 📡 Documentação de Endpoints

### 🔐 1. Autenticação & Usuários

| Método | Endpoint | Protegido? | Descrição |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Não (`@PublicEndpoint`) | Cadastro de novo usuário com validação de senha forte |
| `POST` | `/api/auth/login` | Não (`@PublicEndpoint`) | Login com email e senha, retornando Token JWT |
| `GET` | `/api/auth/me` | **Sim (Bearer JWT)** | Retorna o perfil cadastral do usuário logado |

#### Exemplo de Requisição — Cadastro (`POST /api/auth/register`)

```json
{
  "name": "Beatriz Oliveira",
  "email": "beatriz@exemplo.com",
  "password": "SenhaForte@2026!",
  "cpf": "123.456.789-00",
  "income": 5000.00,
  "age": 25,
  "latitude": -23.5505,
  "longitude": -46.6333
}
```

---

### 📊 2. Análise de Empréstimos

| Método | Endpoint | Protegido? | Descrição |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/loans/me` | **Sim (Bearer JWT)** | Calcula propostas de empréstimo para o usuário autenticado |
| `POST` | `/customer-loans` | Não (`@PublicEndpoint`) | Endpoint avulso para cálculo direto via payload |

#### Exemplo de Resposta — Proposta (`GET /api/loans/me?location=SP`)

```json
{
  "customer": "Beatriz Oliveira",
  "loans": [
    { "type": "PERSONAL", "interest_rate": 4 },
    { "type": "GUARANTEED", "interest_rate": 3 },
    { "type": "CONSIGNMENT", "interest_rate": 2 }
  ]
}
```

---

### 💳 3. Transações Criptografadas

| Método | Endpoint | Protegido? | Descrição |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/transactions` | Opcional / Autenticado | Cria transação criptografando documento (AES) e cartão (RSA) |
| `GET` | `/api/transactions` | Opcional / Autenticado | Lista transações (do usuário autenticado ou todas) |
| `GET` | `/api/transactions/{id}` | Não | Busca transação descriptografada por ID |
| `PUT` | `/api/transactions/{id}` | Não | Atualiza transação existente |
| `DELETE` | `/api/transactions/{id}` | Não | Remove transação |

#### Exemplo de Requisição — Transação (`POST /api/transactions`)

```json
{
  "creditCardToken": "4111222233334444",
  "value": 1500
}
```

*(Se autenticado via Bearer Token, o `userDocument` e o `userId` são vinculados automaticamente).*

---

### 📍 4. Pontos de Interesse (GPS / Caixas Eletrônicos)

| Método | Endpoint | Protegido? | Descrição |
| :--- | :--- | :--- | :--- |
| `POST` | `/pois` | Não | Cadastra um novo Ponto de Interesse |
| `GET` | `/pois` | Não | Lista todos os POIs cadastrados |
| `GET` | `/pois/nearby` | Não | Busca POIs em raio euclidiano (`?x=20&y=10&dmax=10`) |

---

### ✂️ 5. Encurtador de URLs (Indicações / Recibos)

| Método | Endpoint | Protegido? | Descrição |
| :--- | :--- | :--- | :--- |
| `POST` | `/shorten-url` | Não | Encurta uma URL original gerando código alfanumérico |
| `GET` | `/{shortCode}` | Não | Redirecionamento `HTTP 302 Found` para a URL original |

---

### 🔒 6. Validação de Senha (Isolada)

| Método | Endpoint | Protegido? | Descrição |
| :--- | :--- | :--- | :--- |
| `POST` | `/validate-password` | Não | Valida senha retornando `204 No Content` ou `400 Bad Request` |

---

## 🧪 Testes Automatizados

O projeto conta com ampla cobertura de testes unitários e de integração:

- **Testes Unitários**: Validação isolada de regras de domínio, estratégias de empréstimo, algoritmos criptográficos, gerador de códigos base62 e regras de senha.
- **Testes de Controller**: Validação de contratos REST, headers, códigos HTTP de retorno e tratamento global de exceções.
- **Teste End-to-End (`EndToEndFlowTest`)**:
  Simulação completa e sequencial do ciclo de vida:
  1. Cadastro com validação de senha forte.
  2. Login e emissão de JWT.
  3. Consulta de empréstimos baseada no cadastro.
  4. Execução de transação criptografada associada ao usuário.
  5. Busca de caixas eletrônicos por proximidade GPS.
  6. Encurtamento de URL e teste de redirecionamento.

Para rodar toda a suíte de testes:

```bash
mvn test
```

---

## ⚙️ Como Executar a Aplicação

### Pré-requisitos

- **Java 17+**
- **Maven 3.8+** (ou utilizar o wrapper `./mvnw`)

### Execução Local

```bash
# Clonar o repositório
git clone https://github.com/carlosbearzotti/INTEGRATE_SERVICES_JAVA_API.git

# Acessar a pasta do projeto
cd INTEGRATE_SERVICES_JAVA_API

# Compilar e executar
mvn spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`.

### Console do Banco H2

- **URL**: `http://localhost:8080/h2-console`
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **User**: `sa`
- **Password**: *(em branco)*

---

## 📂 Estrutura de Pacotes

```text
com.desafio.integrados
├── autenticacao      # Interceptor, JwtService, TokenValidation e configurações WebMvc
├── criptografia      # Entidade Transaction, Criptografia AES/RSA e Transações
├── emprestimo        # LoanService, Strategies (Pessoal, Garantia, Consignado) e /loans/me
├── encurtadorurl     # UrlShortenerService, gerador de código curto e redirecionamento
├── pontogps          # PointOfInterestService, cálculo de proximidade e coordenadas
├── senhasegura       # PasswordValidationService e regras de validação de senha
└── usuario           # Entidade User, UserRepository, UserService e AuthController
```

---

## 👨‍💻 Autor

Desenvolvido por **Carlos Bearzotti**  
GitHub: [@carlosbearzotti](https://github.com/carlosbearzotti)
