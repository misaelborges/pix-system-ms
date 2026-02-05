# API Gateway 🚪

Um Gateway de Roteamento para o sistema PIX, construído com Spring Cloud Gateway (webmvc). Centraliza e roteia todas as requisições da API para os microserviços (Account Service, Pix Key Service, Payment Service, Receipt Service).

## 📋 Visão Geral

O API Gateway é o ponto de entrada único para o sistema PIX. Ele roteia requisições HTTP para os microserviços apropriados baseado no caminho da URL, implementando padrão de roteamento transparente sem modificação de requisições.

## ✨ Funcionalidades

- **Roteamento centralizado**: Todos os serviços acessíveis via porta única (8079)
- **Predicados baseados em Path**: Rota automática conforme `/api/v1/accounts/**`, `/api/v1/pix-keys/**`, etc.
- **Suporte a múltiplos serviços**: Account, Pix Key, Payment, Receipt Services
- **Logging detalhado**: DEBUG para gateway e requests HTTP
- **Configuração em Properties**: Rotas declarativas e simples de manter

## 🛠️ Tecnologias

| Tecnologia           | Versão   | Uso                  |
| -------------------- | -------- | -------------------- |
| Java                 | 21       | Linguagem principal  |
| Spring Boot          | 3.5.10   | Framework base       |
| Spring Cloud Gateway | 2025.0.1 | Roteamento e gateway |

## 📁 Estrutura do Projeto

```
api-gateway/
├── src/main/java/com/financeiro/gateway/
│   └── ApiGatewayApplication.java       # Classe principal
├── src/main/resources/
│   └── application.properties            # Configuração de rotas
└── pom.xml                               # Dependências Maven
```

## 🚀 Início Rápido

### Pré-requisitos

- Java 21+
- Maven 3.8+
- Microserviços rodando nas portas corretas:
  - Account Service: `http://localhost:8081`
  - Pix Key Service: `http://localhost:8082`
  - Payment Service: `http://localhost:8083`
  - Receipt Service: `http://localhost:8084`

### Instalação Local

1. **Clone o repositório**

   ```bash
   git clone <repository-url>
   cd api-gateway
   ```

2. **Configure as URIs dos serviços** (se necessário) em `src/main/resources/application.properties`:

   ```properties
   # Account Service
   spring.cloud.gateway.server.webmvc.routes[0].uri=http://localhost:8081

   # Pix Key Service
   spring.cloud.gateway.server.webmvc.routes[1].uri=http://localhost:8082

   # Payment Service
   spring.cloud.gateway.server.webmvc.routes[2].uri=http://localhost:8083

   # Receipt Service
   spring.cloud.gateway.server.webmvc.routes[3].uri=http://localhost:8084
   ```

3. **Execute o serviço**

   ```bash
   mvn clean spring-boot:run
   ```

   Ou empacote e execute:

   ```bash
   mvn clean package
   java -jar target/api-gateway-0.0.1-SNAPSHOT.jar
   ```

4. **Verifique se está rodando**
   ```bash
   # Deve rotear para Account Service (porta 8081)
   curl http://localhost:8079/api/v1/accounts/1
   ```

### Executar com Docker Compose

Adicione ao seu `docker-compose.yml`:

```yaml
api-gateway:
  build: ./api-gateway
  ports:
    - "8079:8079"
  environment:
    # URIs dos serviços dentro da rede Docker
    SPRING_CLOUD_GATEWAY_ROUTES[0]_URI: http://account-service:8081
    SPRING_CLOUD_GATEWAY_ROUTES[1]_URI: http://pix-key-service:8082
    SPRING_CLOUD_GATEWAY_ROUTES[2]_URI: http://payment-service:8083
    SPRING_CLOUD_GATEWAY_ROUTES[3]_URI: http://receipt-service:8084
  depends_on:
    - account-service
    - pix-key-service
    - payment-service
    - receipt-service
```

## 📚 Mapa de Rotas

| Rota                  | Serviço         | Porta | Path             |
| --------------------- | --------------- | ----- | ---------------- |
| `/api/v1/accounts/**` | Account Service | 8081  | Contas bancárias |
| `/api/v1/pix-keys/**` | Pix Key Service | 8082  | Chaves PIX       |
| `/api/v1/payments/**` | Payment Service | 8083  | Transferências   |
| `/api/v1/receipts/**` | Receipt Service | 8084  | Recibos          |

### Exemplos de Uso

```bash
# Criar conta (Account Service)
curl -X POST http://localhost:8079/api/v1/accounts \
  -H "Content-Type: application/json" \
  -d '{"email": "user@example.com", "phone": "11999999999", "cpf": "12345678901"}'

# Criar chave PIX (Pix Key Service)
curl -X POST http://localhost:8079/api/v1/pix-keys \
  -H "Content-Type: application/json" \
  -d '{"accountId": 1, "keyType": "CPF", "keyValue": "12345678901"}'

# Criar transferência PIX (Payment Service)
curl -X POST http://localhost:8079/api/v1/payments/pix-transfer \
  -H "Content-Type: application/json" \
  -d '{"senderAccountId": 1, "pixKeyReceiver": "12345678901", "amount": 100.00, "description": "Pagamento"}'

# Obter recibo (Receipt Service)
curl http://localhost:8079/api/v1/receipts/{transactionId}
```

## ⚙️ Configuração

### application.properties

```properties
# Identificação e porta
spring.application.name=Api Gateway
server.port=8079

# ========== ROTAS ==========
# Rota 0: Account Service
spring.cloud.gateway.server.webmvc.routes[0].id=account-service
spring.cloud.gateway.server.webmvc.routes[0].uri=http://localhost:8081
spring.cloud.gateway.server.webmvc.routes[0].predicates[0]=Path=/api/v1/accounts/**

# Rota 1: Pix Key Service
spring.cloud.gateway.server.webmvc.routes[1].id=pix-key-service
spring.cloud.gateway.server.webmvc.routes[1].uri=http://localhost:8082
spring.cloud.gateway.server.webmvc.routes[1].predicates[0]=Path=/api/v1/pix-keys/**

# Rota 2: Payment Service
spring.cloud.gateway.server.webmvc.routes[2].id=payment-service
spring.cloud.gateway.server.webmvc.routes[2].uri=http://localhost:8083
spring.cloud.gateway.server.webmvc.routes[2].predicates[0]=Path=/api/v1/payments/**

# Rota 3: Receipt Service
spring.cloud.gateway.server.webmvc.routes[3].id=receipt-service
spring.cloud.gateway.server.webmvc.routes[3].uri=http://localhost:8084
spring.cloud.gateway.server.webmvc.routes[3].predicates[0]=Path=/api/v1/receipts/**

# ========== LOGGING DETALHADO ==========
logging.level.root=INFO
logging.level.org.springframework.cloud.gateway=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.apache.http=DEBUG
```

### Adicionar Nova Rota

Para adicionar um novo serviço, adicione uma nova rota em `application.properties`:

```properties
# Rota N: Novo Serviço
spring.cloud.gateway.server.webmvc.routes[N].id=novo-service
spring.cloud.gateway.server.webmvc.routes[N].uri=http://localhost:PORT
spring.cloud.gateway.server.webmvc.routes[N].predicates[0]=Path=/api/v1/novo/**
```

## 🔄 Fluxo de Requisição

```
Cliente HTTP
    ↓
http://localhost:8079/api/v1/...
    ↓
API Gateway
    ↓
    ├→ /api/v1/accounts/** → Account Service (8081)
    ├→ /api/v1/pix-keys/** → Pix Key Service (8082)
    ├→ /api/v1/payments/** → Payment Service (8083)
    └→ /api/v1/receipts/** → Receipt Service (8084)
    ↓
Resposta HTTP
    ↓
Cliente
```

## 📊 Níveis de Logging

O gateway expõe logs em diferentes níveis para facilitar troubleshooting:

- **ROOT**: INFO (geral)
- **org.springframework.cloud.gateway**: DEBUG (roteamento)
- **org.springframework.web**: DEBUG (requisições HTTP)
- **org.apache.http**: DEBUG (cliente HTTP)

Exemplos de logs (DEBUG):

```
DEBUG: Received HTTP request to /api/v1/accounts/1
DEBUG: Gateway matched route: account-service
DEBUG: Forwarding to http://localhost:8081/api/v1/accounts/1
DEBUG: Response status: 200 OK
```

## 🐛 Troubleshooting

### Erro 404 - Rota não encontrada

Verifique se o path corresponde a uma rota configurada:

```bash
# Exemplos válidos:
/api/v1/accounts/...
/api/v1/pix-keys/...
/api/v1/payments/...
/api/v1/receipts/...
```

### Erro de conexão ao serviço (503)

Verifique se o serviço está rodando na porta configurada:

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

### Aumentar verbosidade de logs

Em `application.properties`, altere para:

```properties
logging.level.root=DEBUG
logging.level.org.springframework.cloud.gateway=TRACE
```

## 📝 Licença

Este projeto está licenciado sob a MIT License - veja o arquivo LICENSE para detalhes.

---

**Última atualização**: Fevereiro 2026
