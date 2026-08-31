# 🏦 INTEGRATE_SERVICES_JAVA_API — Ecossistema Fintech & Hub Centralizador

[![Java 17](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot 3.4.3](https://img.shields.io/badge/Spring%20Boot-3.4.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Data JPA](https://img.shields.io/badge/Spring%20Data-JPA-blue.svg)](https://spring.io/projects/spring-data-jpa)
[![H2 Database](https://img.shields.io/badge/Database-H2-yellow.svg)](https://www.h2database.com/)
[![PostgreSQL Multi--Tenant](https://img.shields.io/badge/PostgreSQL-Multi--Tenant-blue.svg)](https://www.postgresql.org/)
[![JWT Auth0](https://img.shields.io/badge/Auth-JWT%20(Auth0)-black.svg)](https://github.com/auth0/java-jwt)
[![Tests](https://img.shields.io/badge/Tests-JUnit%205%20%7C%20MockMvc-green.svg)](https://junit.org/junit5/)

Uma **API RESTful Centralizadora (Hub & Spoke)** desenvolvida em **Java 17** e **Spring Boot 3**, atuando como o **Core Bancário, Motor de Inteligência e Sistema Nervoso Central** de um ecossistema completo de microserviços e aplicações frontend/middleware especializadas (*Consumers*).

---

## 🏛️ Arquitetura Hub & Spoke: Matriz de Serviços e Consumers

O backend **`Integrados`** centraliza as regras de negócio, persistência, criptografia e orquestração assíncrona, servindo múltiplos **Consumers Especializados**:

```text
                                  ┌─────────────────────────────────────────┐
                                  │   📱 consumerLãoBank (Porta 3000)       │
                                  │   Internet Banking Digital (B2C)        │
                                  └────────────────────┬────────────────────┘
                                                       │ REST + JWT
                                                       ▼
┌─────────────────────────────────────────┐     ┌─────────────────────────────────────────┐
│  🏢 consumerBackOffice (Porta 3001)     │────▶│                                         │
│  B2B Partner Hub & Gestão Corporativa   │     │      🏦 INTEGRADOS CENTRAL API          │
└─────────────────────────────────────────┘     │      (Spring Boot 3 - Porta 8080)       │
                     ▲                          │                                         │
                     │                          └────────────────────┬────────────────────┘
                     │                                               │ Webhooks (HMAC-SHA256)
                     └───────────────────────────────────────────────┼────────────────────┐
                                                                     ▼                    │
                                                ┌─────────────────────────────────────────┴┐
                                                │   📬 consumerNotification (Porta 3002)   │
                                                │   Middleware de E-mail & Web Inbox       │
                                                └──────────────────────────────────────────┘
```

---

## 🗺️ Matriz de Consumo por Aplicação

| Serviço / Domínio no Core Backend | Endpoints / Recursos | 📱 `consumerLãoBank` (B2C) | 🏢 `consumerBackOffice` (B2B) | 📬 `consumerNotification` (Middleware) |
| :--- | :--- | :---: | :---: | :---: |
| **🔐 Autenticação & Recuperação** | `/api/auth/login`, `/api/auth/register`, `/api/auth/forgot-password`, `/api/auth/reset-password`, `/api/auth/me` | ✅ (Login, Registro, Token, Recuperação) | ✅ (Validação de sessão admin) | ❌ |
| **🛡️ Validação de Senha Forte** | `/api/validate-password` (`SenhaSegura`) | ✅ (Validação em tempo real) | ✅ (Auditoria de políticas) | ❌ |
| **📊 Motor de Empréstimos** | `/api/loans/me`, `/customer-loans` | ✅ (Simulação e contratação de crédito) | ✅ (Estatísticas de concessão) | ❌ |
| **🔒 Cofre Criptográfico** | `/api/transactions` (AES-256 no CPF, RSA no Cartão) | ✅ (Pagamento de fatura, extrato, Pix) | ✅ (Monitoramento de volume cifrado) | ❌ |
| **📍 Pontos de Interesse (GPS)** | `/pois`, `/pois/nearby` | ✅ (Caixas e agências no raio do cliente) | ✅ (Radar geográfico) | ❌ |
| **✂️ Encurtador de URLs** | `/shorten-url`, `/{shortCode}` | ✅ (Indique e Ganhe, recibos curtos) | ✅ (Métricas de cliques) | ❌ |
| **🚨 Radar Antifraude & Geofencing** | `/api/fraud/evaluate`, `/api/fraud/alerts` | ❌ (Transparente para o usuário) | ✅ (Fila de moderação e radar visual) | ❌ |
| **🏛️ Compliance & LGPD** | `/api/compliance/export/{id}`, `/api/compliance/anonymize/{id}`, `/api/audit-logs` | ❌ | ✅ (Exportação de dossiê e esquecimento) | ❌ |
| **📈 Renda Fixa & Custódia** | `/api/investments/simulate`, `/api/investments/products`, `/api/investments/positions` | ❌ | ✅ (Simulador CDI/IR e posições) | ❌ |
| **⚡ Gateway de Webhooks** | `/api/webhooks/subscribe`, `/api/webhooks/dispatch`, `/api/webhooks/deliveries` | ❌ | ✅ (Gestão de endpoints e logs HMAC) | ✅ (Recepção de payloads) |
| **⏱️ Agendamento (@Scheduled)** | `/api/scheduled-transfers` | ❌ | ✅ (Fila de pagamentos programados) | ❌ |
| **🏢 Conectores ERP/CRM & B2B** | `/api/integrations/connectors`, `/api/integrations/api-keys`, `/api/integrations/transform` | ❌ | ✅ (SAP, TOTVS, Salesforce e chaves) | ❌ |
| **📬 Mensageria & DLQ** | `/api/messaging/topics`, `/api/messaging/dlq`, `/api/messaging/reprocess` | ❌ | ✅ (Monitor de vazão, tópicos e DLQ) | ✅ (Consumo de eventos de e-mail) |
| **💳 Faturamento & Billing B2B** | `/api/billing/invoices`, `/api/billing/tariffs`, `/api/billing/export` | ❌ | ✅ (Extrato tarifário e CNAB 240) | ❌ |
| **👥 Multi-Tenancy & MDM** | `X-API-Key` header, Schema per tenant (`tenant_fintech`, `tenant_laobank`) | ❌ | ✅ (Governança, Whitelist Zero Trust) | ❌ |

---

## 🛠️ Tecnologias & Arquitetura Interna

- **Linguagem & Framework**: Java 17 (LTS), Spring Boot 3.4.3
- **Segurança & Criptografia**:
  - JWT Stateless com Auth0 (`com.auth0:java-jwt`)
  - Criptografia Simétrica **AES-256-CBC** (Documentos/CPF)
  - Criptografia Assimétrica **RSA-2048** (Tokens de Cartão de Crédito)
  - Assinatura Digital de Webhooks via **HMAC-SHA256** (`X-Signature`)
  - Motor de validação de senhas com 5 regras estritas (`SenhaSegura`)
- **Persistência & Multi-Tenancy**:
  - Spring Data JPA / Hibernate
  - Suporte a H2 In-Memory (Testes) e PostgreSQL com Schema-per-Tenant
  - Resolução dinâmica de Tenant via cabeçalho `X-API-Key`
- **Processamento Assíncrono**:
  - Tarefas agendadas via `@Scheduled` / Cron
  - Event Dispatcher interno com políticas de retenção em Dead Letter Queue (DLQ)
- **Qualidade & Testes**:
  - JUnit 5, Mockito, MockMvc e testes ponta a ponta (`EndToEndFlowTest`)

---

## 📡 Catálogo Resumido de Endpoints

### 1. Autenticação & Usuários
- `POST /api/auth/register`: Cadastro de usuário com validação de senha forte.
- `POST /api/auth/login`: Autenticação e emissão de JWT.
- `GET /api/auth/me`: Perfil cadastral do usuário autenticado (`Bearer JWT`).
- `POST /api/auth/forgot-password`: Solicitação de código de recuperação de senha de 6 dígitos via e-mail.
- `POST /api/auth/reset-password`: Redefinição de senha com validação de código e regras de senha forte.

### 2. Transações Criptografadas
- `POST /api/transactions`: Cria transação com criptografia AES/RSA e verificação antifraude.
- `GET /api/transactions`: Extrato de transações do usuário logado.

### 3. Empréstimos & Crédito
- `GET /api/loans/me`: Avaliação automática de linhas pré-aprovadas com base na renda e perfil.
- `POST /customer-loans`: Avaliação direta de crédito via payload avulso.

### 4. Geolocalização & POIs
- `POST /pois`: Cadastro de Ponto de Interesse.
- `GET /pois/nearby?x={lat}&y={lng}&dmax={km}`: Busca de agências/caixas por raio euclidiano.

### 5. Encurtador de URLs
- `POST /shorten-url`: Encurtamento de URL em Base62.
- `GET /{shortCode}`: Redirecionamento HTTP 302 com métricas de cliques.

---

## 🏃 Como Executar

### Pré-requisitos
- **Java 17+**
- **Maven 3.8+** (ou utilizar o wrapper `./mvnw`)

```bash
# Compilar e rodar a API Central
./mvnw spring-boot:run
```
A API estará online em `http://localhost:8080`.

---

## 👨‍💻 Autor
Desenvolvido por **Carlos Bearzotti**  
GitHub: [@carlosbearzotti](https://github.com/carlosbearzotti)
