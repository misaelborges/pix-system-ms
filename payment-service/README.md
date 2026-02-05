# Payment Service 💰

Um microserviço robusto e escalável para gerenciamento de pagamentos PIX, construído com Spring Boot 3.5.10. Oferece operações de processamento de transferências PIX com validações de segurança, rastreamento de transações via eventos assincronos e integração com Account Service e Pix Key Service usando padrão de publicação de eventos com RabbitMQ.

## 📋 Visão Geral

O Payment Service é responsável por processar e gerenciar todas as transferências PIX do sistema financeiro. Ele fornece endpoints para criar transações de pagamento, consultar detalhes e histórico de transações. O serviço integra-se com Account Service para validar contas e realizar débito/crédito, valida chaves PIX com o Pix Key Service, e publica eventos de conclusão de pagamento via RabbitMQ para sincronização assincronista com outros serviços.

## ✨ Funcionalidades

- **Transferências PIX**: Criar e rastrear transferências PIX entre contas
- **Validação de Chave PIX**: Integração com Pix Key Service para validar chaves
- **Validação de Conta e Saldo**: Integração com Account Service para validações
- **Débito e Crédito Automático**: Atualização de saldo nas contas envolvidas
- **Status de Transações**: Pendente, Concluído
- **Histórico de Transações**: Listagem de todas as transferências por conta
- **Eventos de Pagamento**: Publicação de eventos via RabbitMQ quando pagamento é concluído
- **Integração com Account Service**: Validação de contas e operações monetárias
- **Integração com Pix Key Service**: Validação de chaves PIX
- **Tratamento Global de Exceções**: Respostas padronizadas e informativas
- **Validação de Entrada**: Jakarta Validation com mensagens customizadas em português
- **Timestamps Automáticos**: Rastreamento de criação e atualização

## 🛠️ Tecnologias

| Tecnologia          | Versão | Uso                                     |
| ------------------- | ------ | --------------------------------------- |
| Java                | 21     | Linguagem principal                     |
| Spring Boot         | 3.5.10 | Framework base                          |
| Spring Data MongoDB | 3.5.10 | Persistência de dados                   |
| MongoDB             | 6+     | Banco de dados NoSQL                    |
| RabbitMQ            | 3.12+  | Message broker para eventos assincronos |
| RestClient          | 3.5.10 | Cliente HTTP para integrações           |
| Jakarta Validation  | 3.5.10 | Validação de dados                      |
| Spring AMQP         | 3.5.10 | Integração com RabbitMQ                 |
| JUnit 5             | 5.10+  | Testes unitários                        |
| Mockito             | 5.x    | Mock de dependências                    |

## 📁 Estrutura do Projeto

```
payment-service/
├── src/main/java/com/financeiro/payment/
│   ├── controller/
│   │   └── PaymentController.java         # Endpoints da API REST
│   ├── service/
│   │   ├── PaymentService.java            # Lógica de negócio de transferências
│   │   ├── AccountServiceClient.java      # Cliente para Account Service
│   │   └── PixKeyServiceClient.java       # Cliente para Pix Key Service
│   ├── repository/
│   │   └── TransactionRepository.java     # Acesso a transações (MongoDB)
│   ├── entity/
│   │   ├── Transaction.java               # Entidade de transação (Document MongoDB)
│   │   └── StatusTransaction.java         # Enum dos status
│   ├── event/
│   │   └── PaymentCompletedEvent.java     # Evento de pagamento concluído
│   ├── publisher/
│   │   └── PaymentEventPublisher.java     # Publicador de eventos para RabbitMQ
│   ├── config/
│   │   ├── messaging/
│   │   │   └── MessagingConfig.java       # Configuração do RabbitMQ
│   │   └── rabbitmq/
│   │       └── RabbitMQConfig.java        # Configuração das filas
│   ├── exception/
│   │   ├── TransactionNotFoundException.java
│   │   ├── InvalidPixKeyException.java
│   │   ├── InsufficientBalanceException.java
│   │   ├── AccountServiceException.java
│   │   └── handler/
│   │       └── GlobalExceptionHandler.java # Interceptador global
│   ├── dto/
│   │   ├── request/
│   │   │   └── PaymentRequestDTO.java
│   │   └── response/
│   │       ├── PaymentResponseDTO.java
│   │       └── AccountResponseDTO.java
│   └── PaymentServiceApplication.java     # Classe principal
├── src/main/resources/
│   ├── application.properties              # Configurações gerais
│   └── static/templates/                   # Recursos estáticos
├── src/test/java/
│   └── com/financeiro/payment/             # Testes unitários
├── Dockerfile                              # Containerização
└── pom.xml                                 # Dependências Maven
```

## 🚀 Início Rápido

### Pré-requisitos

- Java 21+
- Maven 3.8+
- MongoDB 6+
- RabbitMQ 3.12+
- Account Service rodando em `http://localhost:8081`
- Pix Key Service rodando em `http://localhost:8082`
- Docker e Docker Compose (opcional)

### Instalação Local

1. **Clone o repositório**

   ```bash
   git clone <repository-url>
   cd payment-service
   ```

2. **Configure o banco de dados MongoDB**
   - Inicie o MongoDB localmente ou use Docker
   - Atualize `src/main/resources/application.properties` com suas credenciais

3. **Configure o RabbitMQ**
   - Inicie o RabbitMQ localmente ou use Docker
   - Atualize `src/main/resources/application.properties` com host/porta

4. **Configure as URLs dos serviços**

   ```properties
   account.service.url=http://localhost:8081/api/v1/accounts
   pixkey.service.url=http://localhost:8082/api/v1/pix-keys
   ```

5. **Execute o serviço**

   ```bash
   # Com Maven
   mvn clean spring-boot:run

   # Ou com Java direto
   mvn clean package
   java -jar target/payment-service-0.0.1-SNAPSHOT.jar
   ```

### Executar com Docker Compose

```bash
docker-compose up payment-service
```

## 📚 Endpoints da API

### Criar Transferência PIX

```
POST /api/v1/payments/pix-transfer
Content-Type: application/json

{
  "senderAccountId": 123,
  "pixKeyReceiver": "seu@email.com",
  "amount": 150.50,
  "description": "Pagamento de serviço"
}

Resposta (201 Created):
{
  "id": "507f1f77bcf86cd799439011",
  "senderAccountId": 123,
  "receiverAccountId": 456,
  "pixkeyReceiver": "seu@email.com",
  "amount": 150.50,
  "description": "Pagamento de serviço",
  "statusTransaction": "COMPLETED",
  "createdAt": "2026-02-05T10:30:00Z",
  "updatedAt": "2026-02-05T10:30:05Z"
}
```

### Obter Detalhes de Transação

```
GET /api/v1/payments/{transactionId}

Resposta (200 OK):
{
  "id": "507f1f77bcf86cd799439011",
  "senderAccountId": 123,
  "receiverAccountId": 456,
  "pixkeyReceiver": "seu@email.com",
  "amount": 150.50,
  "description": "Pagamento de serviço",
  "statusTransaction": "COMPLETED",
  "createdAt": "2026-02-05T10:30:00Z",
  "updatedAt": "2026-02-05T10:30:05Z"
}
```

### Listar Transações da Conta

```
GET /api/v1/payments/account/{accountId}

Resposta (200 OK):
[
  {
    "id": "507f1f77bcf86cd799439011",
    "senderAccountId": 123,
    "receiverAccountId": 456,
    "pixkeyReceiver": "seu@email.com",
    "amount": 150.50,
    "description": "Pagamento de serviço",
    "statusTransaction": "COMPLETED",
    "createdAt": "2026-02-05T10:30:00Z",
    "updatedAt": "2026-02-05T10:30:05Z"
  }
]
```

## 🔐 Fluxo de Validação

1. **Validação de Conta de Origem**: Confirma que a conta de origem existe via Account Service
2. **Validação de Chave PIX**: Valida se a chave PIX existe e está ativa via Pix Key Service
3. **Validação de Saldo**: Verifica se a conta tem saldo suficiente
4. **Processamento**: Realiza débito na conta de origem e crédito na conta de destino
5. **Publicação de Evento**: Publica evento de pagamento concluído para RabbitMQ

## 🧪 Testes

```bash
# Executar todos os testes
mvn test

# Executar com cobertura
mvn test jacoco:report

# Verificar relatório
open target/site/jacoco/index.html
```

## 📊 Modelo de Dados

### Entidade: Transaction (MongoDB Document)

| Campo             | Tipo              | Descrição                             |
| ----------------- | ----------------- | ------------------------------------- |
| id                | String (ObjectId) | Identificador único da transação      |
| senderAccountId   | Long              | ID da conta de origem                 |
| receiverAccountId | Long              | ID da conta de destino                |
| pixkeyReceiver    | String            | Chave PIX do destinatário             |
| amount            | BigDecimal        | Valor da transação                    |
| description       | String            | Descrição do pagamento                |
| statusTransaction | Enum              | PENDING, COMPLETED                    |
| createdAt         | Instant           | Data/hora de criação (automático)     |
| updatedAt         | Instant           | Data/hora de atualização (automático) |

## ⚙️ Configurações

### application.properties

```properties
# Identificação e porta
spring.application.name=Payment Service
server.port=8083

# MongoDB
spring.data.mongodb.uri=mongodb://admin:secret@localhost:27017/payment_db?authSource=admin

# RabbitMQ
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=user
spring.rabbitmq.password=password

# URLs dos serviços
account.service.url=http://localhost:8081/api/v1/accounts
pixkey.service.url=http://localhost:8082/api/v1/pix-keys

# RabbitMQ Exchange e Filas
payments.exchange.name=payments
payment.completed.queue=payment.completed
payment.completed.routing.key=payment.completed
```

## 🔄 Fluxo de Integração

```
User Request
    ↓
PaymentController
    ↓
PaymentService
    ├→ AccountServiceClient (valida conta de origem)
    ├→ PixKeyServiceClient (valida chave PIX e obtém conta destino)
    ├→ AccountServiceClient (verifica saldo)
    ├→ AccountServiceClient (realiza débito)
    ├→ AccountServiceClient (realiza crédito)
    ├→ TransactionRepository (persiste transação em MongoDB)
    └→ PaymentEventPublisher (publica evento via RabbitMQ)
    ↓
Response (201 Created)
```

## 📤 Eventos Publicados

### PaymentCompletedEvent

Quando uma transação é concluída com sucesso, um evento é publicado no RabbitMQ:

**Exchange**: `payments`  
**Queue**: `payment.completed`  
**Routing Key**: `payment.completed`

**Payload do Evento**:

```json
{
  "id": "507f1f77bcf86cd799439011",
  "senderAccountId": 123,
  "receiverAccountId": 456,
  "pixkeyReceiver": "seu@email.com",
  "amount": 150.5,
  "description": "Pagamento de serviço",
  "statusTransaction": "COMPLETED",
  "createdAt": "2026-02-05T10:30:00Z",
  "updatedAt": "2026-02-05T10:30:05Z"
}
```

Outros serviços podem se inscrever nesta fila para processar eventos de pagamento concluído (ex: geração de recibos, notificações, auditoria).

## 🚨 Tratamento de Erros

Todos os erros retornam em formato padronizado:

```json
{
  "timestamp": "2026-02-05T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Chave PIX inválida",
  "path": "/api/v1/payments/pix-transfer"
}
```

Exceções tratadas:

- **TransactionNotFoundException**: Transação não encontrada (404)
- **InsufficientBalanceException**: Saldo insuficiente (400)
- **InvalidPixKeyException**: Chave PIX inválida (400)
- **AccountServiceException**: Erro na integração com Account Service (503)

## 📝 Convenções de Código

- **Naming**: camelCase para variáveis/métodos, PascalCase para classes
- **Documentação**: JavaDoc para classes e métodos públicos
- **Validação**: Jakarta Validation com anotações nos DTOs
- **Exceções**: Customizadas para cada tipo de erro
- **Logs**: Usando SLF4J com níveis apropriados

## 🤝 Contribuindo

1. Crie uma branch para sua feature (`git checkout -b feature/MinhaFeature`)
2. Commit suas mudanças (`git commit -m 'Add MinhaFeature'`)
3. Push para a branch (`git push origin feature/MinhaFeature`)
4. Abra um Pull Request

## 📄 Licença

Este projeto está licenciado sob a MIT License - veja o arquivo LICENSE para detalhes.

---

**Última atualização**: Fevereiro 2026
