# PIX System Microservices 🏦

Sistema distribuído de microserviços para processamento de transações PIX. Implementado em Spring Boot 3.5.10/4.0.1, com suporte completo a gerenciamento de contas, validação de chaves PIX, processamento de transferências, geração de recibos e integração via API Gateway centralizado.

## 📋 Visão Geral

O **PIX System** é uma arquitetura de microserviços que implementa o fluxo completo de uma transferência PIX:

1. **Criar Conta** (Account Service) — Cadastro de usuários e contas bancárias
2. **Registrar Chave PIX** (Pix Key Service) — Validação e armazenamento de chaves
3. **Processar Transferência** (Payment Service) — Execução da transação com débito/crédito
4. **Gerar Recibo** (Receipt Service) — Geração automática de PDF via eventos
5. **Acessar via Gateway** (API Gateway) — Roteamento centralizado

### Arquitetura

```
┌──────────────────────────────────────────────────────────┐
│                        Cliente                           │
└─────────────────────────┬────────────────────────────────┘
                          │
              ┌───────────▼───────────┐
              │  API Gateway 🚪       │ (Porta 8079)
              │  Spring Cloud GW      │
              └─┬───┬───────┬─────┬───┘
        ┌───────┘   │       │     └──────┐
        │           │       │            │
   ┌────▼───┐   ┌───▼──┐ ┌──▼────┐   ┌───▼──────┐
   │Account │   │ Pix  │ │Payment│   │ Receipt  │
   │Service │   │ Key  │ │Service│   │ Service  │
   │(8081)  │   │(8082)│ │(8083) │   │ (8084)   │
   └────┬───┘   └──┬───┘ └───┬───┘   └────┬─────┘
        │          │         │            │
        │     ┌────┴─────────┼────────────┘
        │     │              │
     ┌──▼─────▼──┐      ┌────▼────┐
     │PostgreSQL │      │ MongoDB  │
     │(Account,  │      │(Payment) │
     │Pix Key,   │      └──────────┘
     │Receipt)   │
     │ + Redis   │
     └───────────┘

     ┌────────────────────────────┐
     │RabbitMQ │ Redis (cache)    │
     │(eventos)│                  │
     └────────────────────────────┘
```

## 🏗️ Microserviços

### 1. Account Service 💳

Gerenciamento de contas bancárias com cache Redis.

- **Porta**: 8081
- **Banco**: PostgreSQL
- **Tech**: Spring Boot 4.0.1, JPA, Redis
- **README**: [account-service/README.md](account-service/README.md)

**Endpoints principais**:

- `POST /api/v1/accounts` — Criar conta
- `GET /api/v1/accounts/{id}` — Obter conta
- `GET /api/v1/accounts/{id}/balance` — Obter saldo (cachedo)

---

### 2. Pix Key Service 🔑

Gerenciamento e validação de chaves PIX.

- **Porta**: 8082
- **Banco**: PostgreSQL
- **Tech**: Spring Boot 4.0.2, JPA, Redis, RestClient
- **README**: [pix-key-service/README.md](pix-key-service/README.md)

**Endpoints principais**:

- `POST /api/v1/pix-keys` — Registrar chave PIX
- `GET /api/v1/pix-keys/account/{accountId}` — Listar chaves
- `GET /api/v1/pix-keys/validate/{keyValue}` — Validar chave

---

### 3. Payment Service 💰

Processamento de transferências PIX com eventos assincronos.

- **Porta**: 8083
- **Banco**: MongoDB
- **Tech**: Spring Boot 3.5.10, MongoDB, RabbitMQ, RestClient
- **README**: [payment-service/README.md](payment-service/README.md)

**Endpoints principais**:

- `POST /api/v1/payments/pix-transfer` — Criar transferência
- `GET /api/v1/payments/{transactionId}` — Obter transação
- `GET /api/v1/payments/account/{accountId}` — Listar transações

---

### 4. Receipt Service 🧾

Geração automática de recibos em PDF.

- **Porta**: 8084
- **Banco**: PostgreSQL
- **Tech**: Spring Boot 3.5.10, JPA, RabbitMQ (consumer), iText 7
- **README**: [receipt-service/README.md](receipt-service/README.md)

**Endpoints principais**:

- `GET /api/v1/receipts/{transactionId}` — Obter recibo
- `GET /api/v1/receipts/account/{accountId}` — Listar recibos da conta

---

### 5. API Gateway 🚪

Roteamento centralizado para todos os serviços.

- **Porta**: 8079
- **Tech**: Spring Cloud Gateway (webmvc)
- **README**: [api-gateway/README.md](api-gateway/README.md)

**Rotas**:

- `/api/v1/accounts/**` → Account Service (8081)
- `/api/v1/pix-keys/**` → Pix Key Service (8082)
- `/api/v1/payments/**` → Payment Service (8083)
- `/api/v1/receipts/**` → Receipt Service (8084)

---

## 🛠️ Stack Tecnológico

### Linguagem & Framework

| Componente               | Versão                 | Uso                  |
| ------------------------ | ---------------------- | -------------------- |
| **Java**                 | 21                     | Linguagem principal  |
| **Spring Boot**          | 3.5.10 / 4.0.1 / 4.0.2 | Framework base       |
| **Spring Cloud**         | 2025.0.1               | Cloud ecosystem      |
| **Spring Cloud Gateway** | 2025.0.1               | API Gateway (webmvc) |
| **Spring Data JPA**      | 3.5.10 / 4.0.1 / 4.0.2 | ORM para SQL         |
| **Spring Data MongoDB**  | 3.5.10                 | ODM para NoSQL       |
| **Spring AMQP**          | 3.5.10                 | RabbitMQ integration |

### Bibliotecas & Utilitários

| Componente             | Versão                 | Uso                     |
| ---------------------- | ---------------------- | ----------------------- |
| **MapStruct**          | 1.6.3                  | Mapeamento DTO ↔ Entity |
| **Lombok**             | 1.18.30 / 1.18.42      | Redução de boilerplate  |
| **iText 7**            | 9.5.0                  | Geração de PDFs         |
| **Jakarta Validation** | 3.5.10 / 4.0.1 / 4.0.2 | Validação de dados      |
| **RestClient**         | Spring 3.5.10 / 4.0.x  | Cliente HTTP síncrono   |

### Banco de Dados & Cache

| Componente            | Versão | Uso                                   |
| --------------------- | ------ | ------------------------------------- |
| **PostgreSQL**        | 15     | BD para Account, Pix Key, Receipt     |
| **MongoDB**           | 6      | BD NoSQL para Payment                 |
| **Redis**             | 7      | Cache distribuído (Account & Pix Key) |
| **PostgreSQL Driver** | 42.7.8 | Conector JDBC                         |
| **H2 Database**       | 2.4.2  | BD em memória (testes)                |

### Message Broker

| Componente   | Versão            | Uso                      |
| ------------ | ----------------- | ------------------------ |
| **RabbitMQ** | 3.12+             | Message broker (eventos) |
| **Jackson**  | (via Spring Boot) | Serialização JSON        |

### Testes

| Componente             | Versão          | Uso                  |
| ---------------------- | --------------- | -------------------- |
| **JUnit 5**            | 5.10.0+ / 6.0.1 | Framework de testes  |
| **Mockito**            | 5.x             | Mock de dependências |
| **Spring Boot Test**   | 3.5.10 / 4.0.x  | Testes integrados    |
| **Spring Rabbit Test** | 3.5.10          | Testes com RabbitMQ  |

### Build & DevOps

| Componente         | Versão | Uso                |
| ------------------ | ------ | ------------------ |
| **Maven**          | 3.8+   | Build tool         |
| **Docker**         | Latest | Containerização    |
| **Docker Compose** | 3.8    | Orquestração local |

---

## 🚀 Início Rápido

### Opção 1: Docker Compose (Recomendado)

Levanta todo o stack em containers:

```bash
# Clone o repositório
git clone <repository-url>
cd pix-system-ms

# Execute todos os serviços
docker-compose up -d

# Verifique os serviços
docker-compose ps

# Acesse o gateway
curl http://localhost:8079/api/v1/accounts/1

# Parar tudo
docker-compose down
```

**Serviços disponíveis após `docker-compose up`**:

| Serviço              | Porta | URL                                    |
| -------------------- | ----- | -------------------------------------- |
| API Gateway          | 8079  | http://localhost:8079                  |
| Account Service      | 8081  | http://localhost:8081                  |
| Pix Key Service      | 8082  | http://localhost:8082                  |
| Payment Service      | 8083  | http://localhost:8083                  |
| Receipt Service      | 8084  | http://localhost:8084                  |
| PostgreSQL (Account) | 5433  | localhost:5433                         |
| PostgreSQL (Pix Key) | 5434  | localhost:5434                         |
| PostgreSQL (Receipt) | 5435  | localhost:5435                         |
| MongoDB              | 27017 | localhost:27017                        |
| Redis                | 6379  | localhost:6379                         |
| RabbitMQ Web         | 15672 | http://localhost:15672 (user/password) |

---

### Opção 2: Executar Localmente (Maven)

Levanta cada serviço em um terminal separado:

1. **PostgreSQL, MongoDB, Redis, RabbitMQ** (via Docker):

   ```bash
   docker-compose up postgres-account postgres-pixkey postgres-receipt mongodb redis rabbitmq
   ```

2. **API Gateway** (Terminal 1):

   ```bash
   cd api-gateway
   mvn clean spring-boot:run
   ```

3. **Account Service** (Terminal 2):

   ```bash
   cd account-service
   mvn clean spring-boot:run
   ```

4. **Pix Key Service** (Terminal 3):

   ```bash
   cd pix-key-service
   mvn clean spring-boot:run
   ```

5. **Payment Service** (Terminal 4):

   ```bash
   cd payment-service
   mvn clean spring-boot:run
   ```

6. **Receipt Service** (Terminal 5):
   ```bash
   cd receipt-service
   mvn clean spring-boot:run
   ```

---

## 📊 Fluxo Completo de uma Transferência PIX

```
1. Cliente cria conta
   POST /api/v1/accounts
   ↓ Account Service ↓
   PostgreSQL (tbl_account)

2. Cliente registra chave PIX
   POST /api/v1/pix-keys
   ↓ Pix Key Service ↓
   PostgreSQL (tbl_pixkey)
   Redis (cache)

3. Cliente inicia transferência
   POST /api/v1/payments/pix-transfer
   ↓ Payment Service ↓
   • Valida conta (→ Account Service)
   • Valida chave PIX (→ Pix Key Service)
   • Valida saldo
   • Débito/Crédito (→ Account Service)
   • Persiste transação
   ↓ MongoDB (transactions)
   • Publica evento "PaymentCompleted"
   ↓ RabbitMQ (exchange: payments)

4. Receipt Service consome evento
   @RabbitListener (queue: payment.completed)
   ↓ Receipt Service ↓
   • Gera PDF com iText
   • Salva PDF em disco (./uploads/receipts)
   • Persiste metadados
   ↓ PostgreSQL (tbl_receipt)

5. Cliente consulta recibo
   GET /api/v1/receipts/{transactionId}
   ↓ Receipt Service ↓
   Retorna: id, transactionId, amount, pdfPath, pdfGeneratedAt
```

---

## 📁 Estrutura do Projeto

```
pix-system-ms/
├── api-gateway/                    # Spring Cloud Gateway
│   ├── src/main/resources/
│   │   └── application.properties   # Rotas
│   └── pom.xml
├── account-service/                # Contas bancárias
│   ├── src/main/java/com/financeiro/account/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   ├── config/
│   │   └── exception/
│   ├── README.md
│   └── pom.xml
├── pix-key-service/                # Chaves PIX
│   ├── src/main/java/com/financeiro/pixkey/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   ├── validator/
│   │   ├── config/
│   │   └── exception/
│   ├── README.md
│   └── pom.xml
├── payment-service/                # Transferências PIX
│   ├── src/main/java/com/financeiro/payment/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   ├── event/
│   │   ├── publisher/
│   │   ├── config/
│   │   └── exception/
│   ├── README.md
│   └── pom.xml
├── receipt-service/                # Recibos em PDF
│   ├── src/main/java/com/financeiro/receipt/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   ├── event/
│   │   ├── listener/
│   │   ├── config/
│   │   └── exception/
│   ├── README.md
│   └── pom.xml
├── docker-compose.yml              # Orquestração de containers
├── README.md                        # Este arquivo
└── uploads/                         # PDFs dos recibos
    └── receipts/
```

---

## 🔌 Integração entre Serviços

### Account Service → Account Service (Validação Interna)

```
Payment/Pix Key Service
    ↓ RestClient
Account Service Internal Endpoint
    ↓
POST /api/v1/accounts/internal/validate/{accountId}
    ↓
Retorna: true/false (conta existe)
```

### Pix Key Service → Account Service

```
Pix Key Service
    ↓ RestClient
Account Service
    ↓
POST /api/v1/accounts/internal/validate/{accountId}
GET /api/v1/accounts/{accountId}/balance
    ↓
Valida existência e saldo
```

### Payment Service → Account Service + Pix Key Service

```
Payment Service
    ├→ AccountServiceClient.validateAccountExists()
    ├→ PixKeyServiceClient.validatePixKey()
    ├→ AccountServiceClient.debit()
    └→ AccountServiceClient.credit()
```

### Payment Service → Receipt Service (RabbitMQ)

```
Payment Service
    ↓ (após salvar transação)
PublishPaymentCompletedEvent
    ↓ RabbitMQ
Exchange: payments
Routing Key: payment.completed
    ↓
Receipt Service PaymentEventListener
    ↓
Cria recibo em PDF
```

---

## 📝 Configurações Principais

### Banco de Dados

| Serviço | Database   | Porta | User     | Pass   |
| ------- | ---------- | ----- | -------- | ------ |
| Account | account_db | 5433  | postgres | secret |
| Pix Key | pixkey_db  | 5434  | postgres | secret |
| Receipt | receipt_db | 5435  | postgres | secret |
| Payment | MongoDB    | 27017 | admin    | secret |

### Cache

- **Redis**: localhost:6379
- **Padrão**: Chaves de account e pixkey com TTL de 5-10 minutos

### Message Broker

- **RabbitMQ**: localhost:5672
- **Management UI**: http://localhost:15672 (user/password)
- **Exchange**: `payments`
- **Queue**: `payment.completed`
- **Routing Key**: `payment.completed`

---

## 🧪 Testes

Execute testes de cada serviço:

```bash
# Account Service
cd account-service && mvn test && cd ..

# Pix Key Service
cd pix-key-service && mvn test && cd ..

# Payment Service
cd payment-service && mvn test && cd ..

# Receipt Service
cd receipt-service && mvn test && cd ..
```

---

## 📚 Documentação Detalhada

- [Account Service](account-service/README.md) — Gerenciamento de contas
- [Pix Key Service](pix-key-service/README.md) — Validação de chaves PIX
- [Payment Service](payment-service/README.md) — Processamento de transferências
- [Receipt Service](receipt-service/README.md) — Geração de recibos
- [API Gateway](api-gateway/README.md) — Roteamento centralizado

---

## 🐛 Troubleshooting

### Erro ao conectar ao PostgreSQL

```bash
# Verifique se os containers estão rodando
docker-compose ps

# Reinicie os containers
docker-compose restart postgres-account postgres-pixkey postgres-receipt
```

### Erro ao conectar ao MongoDB

```bash
# Verifique se MongoDB está rodando
docker-compose logs mongodb

# Reinicie
docker-compose restart mongodb
```

### RabbitMQ Connection Refused

```bash
# Verifique se RabbitMQ está rodando
docker-compose logs rabbitmq

# Acesse Management UI
http://localhost:15672 (user/password)
```

### Receita Service não recebe eventos

```bash
# Verifique se a fila existe
docker-compose exec rabbitmq rabbitmqctl list_queues

# Verifique logs do Receipt Service
docker-compose logs receipt-service
```

---

## 📊 Monitoramento

### Logs em tempo real

```bash
# Todos os serviços
docker-compose logs -f

# Serviço específico
docker-compose logs -f payment-service

# Últimas N linhas
docker-compose logs -n 100 account-service
```

### Health Check

```bash
# Account Service
curl http://localhost:8081/api/v1/accounts/1

# Pix Key Service
curl http://localhost:8082/api/v1/pix-keys/account/1

# Payment Service
curl http://localhost:8083/api/v1/payments/account/1

# Receipt Service
curl http://localhost:8084/api/v1/receipts/account/1
```

---

## 🤝 Contribuindo

1. Faça fork do repositório
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
3. Escreva testes
4. Commit suas mudanças (`git commit -m 'Add nova-feature'`)
5. Push para a branch (`git push origin feature/nova-feature`)
6. Abra um Pull Request

---

## 📄 Licença

Este projeto está licenciado sob a MIT License - veja o arquivo LICENSE para detalhes.

---

## 📞 Contato / Suporte

Para dúvidas, issues ou sugestões, abra uma issue no repositório.

---

**Status**: Em Desenvolvimento 🚀  
**Versão**: 0.0.1-SNAPSHOT  
**Última atualização**: Fevereiro 2026
