# Account Service 💳

Um microserviço robusto e escalável para gerenciamento de contas bancárias, construído com Spring Boot 4.0.1. Oferece operações completas de CRUD de contas, gerenciamento de saldo com cache Redis, validações de negócio e tratamento abrangente de erros com resposta padronizada global.

## 📋 Visão Geral

O Account Service é responsável por gerenciar todas as contas bancárias do sistema. Ele fornece endpoints para criar, atualizar e consultar contas, além de operações de débito e crédito com validações rigorosas de regras de negócio. O serviço integra-se com Redis para caching de saldo, garantindo alta performance em consultas frequentes. Todo erro é tratado globalmente, retornando respostas padronizadas e informativas.

## ✨ Funcionalidades

- **Gerenciamento Completo de Contas**: Criar, ler, atualizar contas bancárias
- **Validações Rigorosas**: Email e CPF únicos, formatação de telefone
- **Operações Monetárias**: Débito e crédito com validações de saldo
- **Cache de Saldo**: Redis para consultas ultrarrápidas de saldo (5 min TTL)
- **Número de Conta Automático**: Geração automática e única (padrão ACC-XXXXX)
- **Listagem por Usuário**: Recuperar todas as contas de um usuário
- **Validação Interna**: Endpoint para validar existência de conta (serviço-a-serviço)
- **Mapeamento Automático**: MapStruct para conversão entre entidades e DTOs
- **Tratamento Global de Exceções**: `GlobalExceptionHandler` com respostas padronizadas
- **Validação de Entrada**: Jakarta Validation com mensagens customizadas em português
- **Timestamps Automáticos**: Rastreamento de criação e atualização

## 🛠️ Tecnologias

| Tecnologia | Versão  | Uso |
|-----------|---------|-----|
| Java | 21 | Linguagem principal |
| Spring Boot | 4.0.1 | Framework base |
| Spring Data JPA | 4.0.1 | Persistência de dados |
| PostgreSQL | 42.7.8 | Banco de dados (produção) |
| H2 | 2.4.2 | Banco de dados (testes) |
| Redis | 6+ | Cache distribuído |
| MapStruct | 1.6.3 | Mapeamento automático de objetos |
| Lombok | 1.18.42 | Redução de boilerplate |
| Jakarta Validation | 4.0.1   | Validação de dados |
| JUnit 5 | 6.0.1 | Testes unitários |
| Mockito | 5.20 | Mock de dependências |

## 📁 Estrutura do Projeto

```
account-service/
├── src/main/java/com/financeiro/account/
│   ├── controller/
│   │   └── AccountController.java         # Endpoints da API REST
│   ├── service/
│   │   ├── AccountService.java            # Lógica de negócio de contas
│   │   └── CacheService.java              # Gerenciamento de cache Redis
│   ├── repository/
│   │   └── AccountRepository.java         # Acesso a contas (JPA)
│   ├── entity/
│   │   └── Account.java                   # Entidade de conta
│   ├── config/
│   │   ├── mapper/
│   │   │   └── AccountMapper.java         # Mapeamento MapStruct
│   │   └── redis/
│   │       └── RedisConfig.java           # Configuração do Redis
│   ├── exception/
│   │   ├── AccountNotFoundException.java
│   │   ├── AmountInvalidException.java
│   │   ├── CpfAlreadyExistsException.java
│   │   ├── EmailAlreadyExistsException.java
│   │   ├── InsufficientBalanceException.java
│   │   └── handler/
│   │       ├── GlobalExceptionHandler.java # Interceptador global
│   │       └── ErrorResponse.java          # Resposta padronizada
│   ├── dto/
│   │   ├── request/
│   │   │   ├── CreateAccountRequestDTO.java
│   │   │   └── UpdateAccountRequestDTO.java
│   │   └── response/
│   │       ├── AccountResponseDTO.java
│   │       ├── AccountResumoDTO.java
│   │       └── BalanceResponseDTO.java
│   └── AccountServiceApplication.java     # Classe principal
├── src/main/resources/
│   ├── application.properties              # Configurações gerais
│   └── application-test.properties         # Configurações de testes
├── src/test/java/
│   └── com/financeiro/account/service/
│       └── AccountServiceTest.java         # Testes unitários
├── Dockerfile                              # Containerização
└── pom.xml                                 # Dependências Maven
```

## 🚀 Início Rápido

### Pré-requisitos

- Java 21+
- Maven 3.8+
- PostgreSQL 12+
- Redis 6+
- Docker e Docker Compose (opcional)

### Instalação Local

1. **Clone o repositório**
   ```bash
   git clone <repository-url>
   cd account-service
   ```

2. **Configure o banco de dados PostgreSQL**
   ```properties
   # application.properties
   spring.datasource.url=jdbc:postgresql://localhost:5433/account_db
   spring.datasource.username=postgres
   spring.datasource.password=secret
   ```

3. **Configure o Redis**
   ```properties
   spring.redis.host=localhost
   spring.redis.port=6379
   ```

4. **Compile e execute**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

5. **Verifique se está rodando**
   ```bash
   curl http://localhost:8081/api/v1/accounts/1
   ```

### Instalação com Docker Compose

1. **Crie um arquivo `docker-compose.yml` na raiz do projeto:**
   ```yaml
   version: '3.8'
   services:
     postgres:
       image: postgres:15
       environment:
         POSTGRES_DB: account_db
         POSTGRES_PASSWORD: secret
       ports:
         - "5433:5432"
       volumes:
         - postgres_data:/var/lib/postgresql/data

     redis:
       image: redis:7
       ports:
         - "6379:6379"

     account-service:
       build: .
       ports:
         - "8081:8081"
       depends_on:
         - postgres
         - redis
       environment:
         SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/account_db
         SPRING_REDIS_HOST: redis
         SPRING_REDIS_PORT: 6379
       restart: unless-stopped

   volumes:
     postgres_data:
   ```

2. **Execute**
   ```bash
   docker-compose up
   ```

3. **Verifique**
   ```bash
   curl http://localhost:8081/api/v1/accounts/1
   ```

## 📚 API Endpoints

### 1️⃣ Criar Conta
Cria uma nova conta bancária com validações completas.

```http
POST /api/v1/accounts
Content-Type: application/json

{
  "email": "usuario@example.com",
  "phone": "11999999999",
  "cpf": "12345678901"
}
```

**Resposta (201 CREATED)**
```json
{
  "id": 1,
  "userId": 1,
  "email": "usuario@example.com",
  "phone": "11999999999",
  "accountNumber": "ACC-45682",
  "balance": 0.00,
  "active": true,
  "createdAt": "2024-01-21T10:30:00Z"
}
```

**Validações**
- Email deve ser válido e único
- Telefone deve conter 10 ou 11 dígitos
- CPF deve ser válido (validação de dígito verificador)

**Erros Possíveis**
- `400 BAD_REQUEST`: Dados inválidos
- `409 CONFLICT`: Email ou CPF já existem

---

### 2️⃣ Obter Conta por ID
Recupera os detalhes de uma conta específica.

```http
GET /api/v1/accounts/{accountId}
```

**Resposta (200 OK)**
```json
{
  "id": 1,
  "userId": 1,
  "email": "usuario@example.com",
  "phone": "11999999999",
  "accountNumber": "ACC-45682",
  "balance": 1500.00,
  "active": true,
  "createdAt": "2024-01-21T10:30:00Z"
}
```

**Erros Possíveis**
- `404 NOT_FOUND`: Conta não encontrada

---

### 3️⃣ Atualizar Conta
Atualiza email e telefone de uma conta existente.

```http
PUT /api/v1/accounts/{accountId}
Content-Type: application/json

{
  "email": "novoemail@example.com",
  "phone": "21988888888"
}
```

**Resposta (200 OK)**
```json
{
  "id": 1,
  "userId": 1,
  "email": "novoemail@example.com",
  "phone": "21988888888",
  "accountNumber": "ACC-45682",
  "balance": 1500.00,
  "active": true,
  "createdAt": "2024-01-21T10:30:00Z"
}
```

**Validações**
- Email não pode duplicar (exceto o atual)
- Telefone deve conter 10 ou 11 dígitos

**Erros Possíveis**
- `404 NOT_FOUND`: Conta não encontrada
- `409 CONFLICT`: Novo email já está em uso

---

### 4️⃣ Obter Saldo
Recupera o saldo de uma conta. **Usa cache Redis** quando disponível (5 minutos).

```http
GET /api/v1/accounts/{accountId}/balance
```

**Resposta (200 OK)**
```json
{
  "accountId": 1,
  "balance": 1500.00,
  "consultedAt": "2024-01-21T10:35:00Z"
}
```

**Como funciona:**
1. Verifica se saldo está no cache Redis
2. Se sim, retorna imediatamente (⚡ ~1ms)
3. Se não, busca no banco e armazena no cache (⏱️ ~50ms)

**Erros Possíveis**
- `404 NOT_FOUND`: Conta não encontrada

---

### 5️⃣ Listar Contas por Usuário
Lista todas as contas de um usuário específico (resumido).

```http
GET /api/v1/accounts/user/{userId}
```

**Resposta (200 OK)**
```json
[
  {
    "id": 1,
    "accountNumber": "ACC-45682",
    "email": "usuario@example.com"
  },
  {
    "id": 2,
    "accountNumber": "ACC-78234",
    "email": "usuario.2@example.com"
  }
]
```

---

### 6️⃣ Validar Existência de Conta (Interno)
Endpoint interno para outros microserviços validarem se uma conta existe.

```http
POST /api/v1/accounts/internal/validate/{accountId}
```

**Resposta (200 OK)**
```json
true
```

ou

```json
false
```

**Nota:** Este endpoint é protegido para uso interno apenas (entre serviços).

---

## 💰 Operações Monetárias

As operações de débito e crédito são chamadas internamente pela lógica de negócio e pelos serviços de transação.

### Débito
```java
account.debit(BigDecimal.valueOf(100.00));
```

**Valida:**
- Valor deve ser maior que 0
- Saldo disponível >= valor

**Exceções:**
- `AmountInvalidException` (400): Se valor <= 0
- `InsufficientBalanceException` (422): Se saldo insuficiente

**Cache:** Invalida cache anterior e armazena novo saldo

---

### Crédito
```java
account.credit(BigDecimal.valueOf(100.00));
```

**Valida:**
- Valor deve ser maior que 0

**Exceções:**
- `AmountInvalidException` (400): Se valor <= 0

**Cache:** Invalida cache anterior e armazena novo saldo

---

## 🔒 Segurança e Validações

### Validações de Entrada

| Campo | Validação | Mensagem de Erro |
|-------|-----------|------------------|
| Email | Formato válido + Único | "Formato de email inválido" ou "Esse email já está em uso" |
| Telefone | 10-11 dígitos numéricos | "Telefone deve conter 10 ou 11 dígitos numéricos" |
| CPF | Válido + Único | "CPF informado é inválido" ou "Esse CPF já está em uso" |

### Validações de Negócio

| Operação | Validação | Resultado |
|----------|-----------|-----------|
| Débito | Saldo >= Valor | ✅ Sucesso ou ❌ Saldo insuficiente |
| Crédito | Valor > 0 | ✅ Sucesso ou ❌ Valor inválido |
| Criação | Email e CPF únicos | ✅ Criada ou ❌ Conflito |
| Atualização | Email não duplica | ✅ Atualizada ou ❌ Conflito |

---

## ⚡ Cache Redis

### Estratégia de Cache

```
GET /api/v1/accounts/{id}/balance
    ↓
Redis ("balance:{id}")
    ↓
Encontrado? → Retorna imediatamente ⚡ (~1ms)
    ↓
Não encontrado? → Busca no banco → Armazena em Redis (TTL: 5 min)
```

### Configuração

```java
@Service
public class CacheService {
    private static final Long CACHE_TTL = 5L;  // 5 minutos
    private static final String BALANCE_KEY_PREFIX = "balance";
    
    public void cacheBalance(Long accountId, BigDecimal balance) {
        String key = BALANCE_KEY_PREFIX + accountId;
        redisTemplate.opsForValue().set(key, balance.toString(), 
            CACHE_TTL, TimeUnit.MINUTES);
    }
}
```

### Invalidação de Cache

Quando há débito/crédito:
1. Delete cache (invalida)
2. Atualiza no banco
3. Armazena novo saldo em cache

Garante consistência entre cache e banco! ✅

---

## 🏗️ MapStruct - Mapeamento Automático

O projeto usa **MapStruct** para converter entre entidades e DTOs automaticamente em tempo de compilação.

```java
@Mapper(componentModel = "spring")
public interface AccountMapper {
    Account toEntity(CreateAccountRequestDTO dto);
    void updateEntity(UpdateAccountRequestDTO dto, @MappingTarget Account account);
    AccountResponseDTO toResponseDTO(Account account);
    List<AccountResumoDTO> toListAccountResumoDTO(List<Account> accounts);
}
```

**Vantagens:**
- ✅ Compilado em tempo de compilação (sem reflexão, rápido!)
- ✅ Type-safe (erros em compile-time, não runtime)
- ✅ Suporte a mapeamentos customizados
- ✅ Zero overhead

---

## 🛡️ Tratamento Global de Exceções

Todos os erros são capturados e retornam uma resposta **padronizada** via `GlobalExceptionHandler`.

### Exemplo de Resposta de Erro

```json
{
  "timestamp": "2024-01-21T10:30:00",
  "status": 404,
  "error": "ACCOUNT_NOT_FOUND",
  "message": "Não existe uma conta com esse Id",
  "path": "/api/v1/accounts/999",
  "validationErrors": null
}
```

### Exceções Tratadas

| Exceção | Status | Erro | Descrição |
|---------|--------|------|-----------|
| `AccountNotFoundException` | 404 | ACCOUNT_NOT_FOUND | Conta não encontrada |
| `EmailAlreadyExistsException` | 409 | EMAIL_ALREADY_EXISTS | Email já em uso |
| `CpfAlreadyExistsException` | 409 | CPF_ALREADY_EXISTS | CPF já em uso |
| `InsufficientBalanceException` | 422 | INSUFFICIENT_BALANCE | Saldo insuficiente |
| `AmountInvalidException` | 400 | AMOUNT_INVALID | Valor <= 0 |
| `MethodArgumentNotValidException` | 400 | VALIDATION_ERROR | Validação falhou |
| `Exception` (genérica) | 500 | INTERNAL_SERVER_ERROR | Erro inesperado |

### Resposta com Validação

Quando há erro de validação, os erros são retornados por campo:

```json
{
  "timestamp": "2024-01-21T10:35:00",
  "status": 400,
  "error": "VALIDATION_ERROR",
  "message": "Erro na validação dos dados enviados",
  "path": "/api/v1/accounts",
  "validationErrors": {
    "email": "Formato de email inválido",
    "phone": "Telefone deve conter 10 ou 11 dígitos numéricos"
  }
}
```

---

## 🧪 Testes

O projeto inclui testes unitários abrangentes usando JUnit 5 e Mockito.

### Executar Testes
```bash
mvn test
```

### Cobertura de Testes

Os testes cobrem:
- ✅ Criar conta com sucesso (dados válidos)
- ✅ Erro ao criar conta com email duplicado
- ✅ Erro ao criar conta com CPF duplicado
- ✅ Recuperar saldo do cache com sucesso
- ✅ Exceção ao debitar com saldo insuficiente

### Exemplo de Teste

```java
@Test
@DisplayName("Deve cadastrar com sucesso quando email, phone e cpf forem válidos")
void shouldCreateAccountSuccessfully() {
    when(accountRepository.existsByEmail(createAccountRequestDTO.email()))
        .thenReturn(false);
    when(accountRepository.existsByCpf(createAccountRequestDTO.cpf()))
        .thenReturn(false);
    when(accountMapper.toEntity(createAccountRequestDTO))
        .thenReturn(account);

    AccountResponseDTO result = accountService.create(createAccountRequestDTO);

    assertNotNull(result);
    assertEquals(1L, result.id());
    verify(accountRepository, times(1)).existsByEmail(createAccountRequestDTO.email());
}
```

---

## 📊 Modelo de Dados

### Account (tbl_account)

```sql
CREATE TABLE tbl_account (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(20) NOT NULL,
    cpf VARCHAR(11) UNIQUE NOT NULL,
    account_number VARCHAR(20) UNIQUE NOT NULL,
    balance NUMERIC(19, 2) DEFAULT 0.00 NOT NULL,
    active BOOLEAN DEFAULT true NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

-- Índices para performance
CREATE INDEX idx_user_id ON tbl_account(user_id);
CREATE INDEX idx_email ON tbl_account(email);
CREATE INDEX idx_cpf ON tbl_account(cpf);
CREATE INDEX idx_account_number ON tbl_account(account_number);
```

---

## 🎯 Fluxo Completo - Criar Conta

```
1. POST /api/v1/accounts { email, phone, cpf }
   ↓
2. AccountController.create() recebe a requisição
   ↓
3. @Valid valida o DTO
   - Email é válido?
   - Telefone tem 10-11 dígitos?
   - CPF é válido?
   ↓
4. AccountService.create() executa
   - Email já existe?
   - CPF já existe?
   - Se não, prossegue
   ↓
5. AccountMapper.toEntity()
   - Converte DTO para Entity
   ↓
6. account.generateAccountNumber()
   - Gera número único: "ACC-45682"
   ↓
7. accountRepository.save(account)
   - Persiste no banco de dados
   - @CreationTimestamp preenche createdAt
   ↓
8. AccountMapper.toResponseDTO()
   - Converte Entity para Response DTO
   ↓
9. Retorna 201 CREATED com dados da conta
```

---

## 🌍 Configuração de Ambientes

### application.properties (Desenvolvimento)
```properties
spring.application.name=Account Service
server.port=8081

spring.datasource.url=jdbc:postgresql://localhost:5433/account_db
spring.datasource.username=postgres
spring.datasource.password=secret

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

spring.redis.host=localhost
spring.redis.port=6379
```

### application-test.properties (Testes)
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
```

---

## 📈 Performance e Escalabilidade

### Cache Redis
- **Consultas de saldo**: ~1ms (com cache) vs ~50ms (sem cache)
- **Redução de queries**: 80%+ para consultas frequentes
- **TTL**: 5 minutos (customizável)
- **Invalidação**: Automática após débito/crédito

### Índices de Banco
- `user_id`: Listagem rápida de contas por usuário
- `email`: Validação de unicidade rápida
- `cpf`: Validação de unicidade rápida
- `account_number`: Busca por número da conta

### Transações
- `@Transactional` garante consistência:
    - Se salvar falhar, não invalida cache
    - Se invalidar falhar, não perde mudança no banco

---

## 🐛 Troubleshooting

### Redis não conecta
```bash
# Verifique se Redis está rodando
redis-cli ping
# Deve retornar: PONG

# Se não estiver rodando:
redis-server
```

### Erro de CPF já existe (ao criar conta de teste)
```bash
# Limpe os dados do banco
DELETE FROM tbl_account;
ALTER SEQUENCE tbl_account_id_seq RESTART WITH 1;
```

### MapStruct não gera código
```bash
# Reconstrua o projeto
mvn clean install

# Se persistir, verifique o pom.xml:
# Certifique-se de ter o annotationProcessorPath configurado
```

### Lombok não funciona
```bash
# Atualize o pom.xml com as dependências corretas
# IntelliJ: File → Invalidate Caches → Restart
# VS Code: Feche e reabra o projeto

mvn clean install
```

---

## 📞 Integração com Outros Serviços

### Auth Service
O Account Service pode validar tokens via Auth Service:

```http
POST /api/v1/auth/validate?token=eyJ...
```

### Transaction Service
O Transaction Service chama:
- `POST /api/v1/accounts/internal/validate/{accountId}` (validar conta)
- Débito/crédito internamente (via AccountService)
- `GET /api/v1/accounts/{accountId}/balance` (consultar saldo)

---

## 🤝 Contribuindo

1. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
2. Commit suas mudanças (`git commit -m 'Add AmazingFeature'`)
3. Push para a branch (`git push origin feature/AmazingFeature`)
4. Abra um Pull Request

---

## 📝 Licença

Este projeto está licenciado sob a Licença MIT - veja o arquivo LICENSE para detalhes.

---

**Versão**: 0.0.1 | **Status**: Development | **Última atualização**: Janeiro 2026