# Receipt Service 🧾

Microserviço responsável por gerar e armazenar recibos em PDF quando pagamentos são concluídos. Implementado com Spring Boot, escuta eventos de pagamento concluído via RabbitMQ, gera PDFs com iText e persiste metadados em PostgreSQL.

## 📋 Visão Geral

O Receipt Service recebe eventos de pagamento concluído (exchange `payments`, queue `payment.completed`), gera um recibo em PDF contendo informações da transação e salva o arquivo em disco (pasta configurável). Também expõe endpoints para recuperar metadados do recibo e o caminho do PDF gerado.

## ✨ Funcionalidades

- Geração de recibos em PDF a partir de eventos `PaymentCompletedEvent`
- Persistência de metadados do recibo em PostgreSQL via JPA
- Exposição de endpoints REST para consulta por `transactionId` e `accountId`
- Publicação/consumo via RabbitMQ (fila `payment.completed`)
- Configuração do diretório de upload (`app.upload.dir`) com valor padrão `./uploads/receipts`

## 🛠️ Tecnologias

| Tecnologia | Versão / Uso |
|-----------|--------------|
| Java | 21 |
| Spring Boot | 3.5.10 |
| Spring Data JPA | 3.5.10 |
| PostgreSQL | Runtime DB |
| RabbitMQ (Spring AMQP) | Mensageria (consumer)
| iText 7 | Geração de PDF (itext7-core)
| JUnit / Spring Boot Test | Testes

## 📁 Estrutura do Projeto (resumo)

```
receipt-service/
├── src/main/java/com/financeiro/receipt/
│   ├── controller/
│   │   └── ReceiptController.java       # Endpoints REST
│   ├── service/
│   │   ├── ReceiptService.java          # Lógica de negócio (salvar PDF/metadados)
│   │   └── PdfService.java              # Geração de PDF com iText
│   ├── repository/
│   │   └── ReceiptRepository.java       # JPA repository
│   ├── entity/
│   │   └── Receipt.java                 # Entidade JPA
│   ├── event/
│   │   └── PaymentCompletedEvent.java   # Record do evento consumido
│   ├── listener/
│   │   └── PaymentEventListener.java    # RabbitMQ listener
│   └── config/
│       └── RabbitMqConfig.java         # Exchange/queue binding
└── pom.xml
```

## 🚀 Início Rápido

### Pré-requisitos

- Java 21+
- Maven 3.8+
- PostgreSQL 12+
- RabbitMQ 3.8+
- Docker e Docker Compose (opcional)

### Configuração local

1. Clone e entre na pasta:

```bash
git clone <repository-url>
cd receipt-service
```

2. Atualize `src/main/resources/application.properties` (ex.: dados do Postgres e RabbitMQ).

3. Propriedade importante:

```properties
# Diretório onde os PDFs serão salvos
app.upload.dir=./uploads/receipts
```

4. Execute com Maven:

```bash
mvn clean spring-boot:run
```

Ou empacote e execute:

```bash
mvn clean package
java -jar target/receipt-service-0.0.1-SNAPSHOT.jar
```

### Docker Compose (exemplo rápido)

Inclua este serviço no seu `docker-compose.yml` junto com Postgres e RabbitMQ:

```yaml
receipt-service:
  build: ./receipt-service
  ports:
    - "8084:8084"
  environment:
    SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/receipt_db
    SPRING_DATASOURCE_USERNAME: postgres
    SPRING_DATASOURCE_PASSWORD: secret
    SPRING_RABBITMQ_HOST: rabbitmq
  depends_on:
    - postgres
    - rabbitmq
```

## 📚 API Endpoints

Base URL: `/api/v1/receipts`

- GET `/api/v1/receipts/{transactionId}`
  - Retorna metadados do recibo para a transação (inclui `pdfPath`).

- GET `/api/v1/receipts/account/{accountId}`
  - Retorna lista de recibos associados a `accountId` (recebidos).

Exemplo de resposta (GET /api/v1/receipts/{transactionId}):

```json
{
  "id": 1,
  "transactionId": "507f1f77bcf86cd799439011",
  "senderAccountId": 123,
  "receiverAccountId": 456,
  "amount": 150.50,
  "pdfPath": "C:/path/to/uploads/receipts/507f1f77-...pdf",
  "pdfGeneratedAt": "2026-02-05T10:30:05"
}
```

## 🔄 Fluxo de Integração

1. Payment Service publica `PaymentCompletedEvent` na exchange `payments` com routing key `payment.completed`.
2. Receipt Service, via `PaymentEventListener`, consome a mensagem da fila `payment.completed`.
3. `ReceiptService` gera o PDF (`PdfService.generateReceipt()`), salva em disco e persiste um registro em `tbl_receipt`.

## 📤 Evento Consumido

`PaymentCompletedEvent` (campo principais):

- `transactionId` (String)
- `senderAccountId` (Long)
- `receiverAccountId` (Long)
- `amount` (BigDecimal)
- `status` (String)
- `createdAt` (ISO date-time String)

## 📊 Modelo de Dados (resumo)

Tabela: `tbl_receipt`

- `id` BIGSERIAL
- `transaction_id` VARCHAR
- `sender_account_id` BIGINT
- `receiver_account_id` BIGINT
- `amount` NUMERIC
- `pdf_path` VARCHAR
- `pdf_generated_at` TIMESTAMP

## 🧪 Testes

Execute os testes unitários:

```bash
mvn test
```

## ⚙️ Observações Operacionais

- O diretório `app.upload.dir` deve ser gravável pelo processo. Em ambientes containerizados, monte um volume se necessário.
- Geração de PDF usa `iText 7` (ver `pom.xml`). Para personalizar layout, edite `PdfService`.
- O listener usa conversão JSON via `Jackson2JsonMessageConverter` (veja `RabbitMqConfig`).

## 📝 Contribuindo

Siga o mesmo padrão dos outros serviços: crie branch, escreva testes, abra PR.

---

**Última atualização**: Fevereiro 2026
