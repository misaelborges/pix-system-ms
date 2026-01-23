# Pix Key Service 🔑

Um microserviço robusto e escalável para gerenciamento de chaves PIX, construído com Spring Boot 4.0.2. Oferece operações completas de CRUD de chaves PIX com suporte a 5 tipos (CPF, CNPJ, Email, Telefone e Aleatória), validações de formato, cache Redis de alta performance e integração com o Account Service.

## 📋 Visão Geral

O Pix Key Service é responsável por gerenciar todas as chaves PIX do sistema financeiro. Ele fornece endpoints para criar, listar, deletar e validar chaves PIX associadas a contas bancárias. O serviço integra-se com o Account Service para validar existência de contas, utiliza Redis para cache de chaves frequentemente consultadas e implementa soft delete (desativação lógica) para auditoria.

## ✨ Funcionalidades

- **Gerenciamento Completo de Chaves PIX**: Criar, listar, deletar chaves associadas a contas
- **5 Tipos de Chaves Suportadas**: CPF, CNPJ, Email, Telefone e Aleatória (UUID)
- **Validações Rigorosas**: Formatação específica para cada tipo de chave
- **Limite de Chaves**: Máximo 5 chaves ativas por conta
- **Soft Delete**: Chaves são desativadas, não deletadas (auditoria)
- **Cache Redis**: Consultas ultrarrápidas com TTL de 10 minutos
- **Integração com Account Service**: Valida existência de conta antes de criar chave
- **Mapeamento Automático**: MapStruct para conversão entre entidades e DTOs
- **Tratamento Global de Exceções**: Respostas padronizadas e informativas
- **Validação de Entrada**: Jakarta Validation com mensagens customizadas
- **Cliente REST**: RestClient para integração com Account Service

## 🛠️ Tecnologias

| Tecnologia | Versão  | Uso |
|-----------|---------|-----|
| Java | 21 | Linguagem principal |
| Spring Boot | 4.0.2 | Framework base |
| Spring Data JPA | 4.0.2 | Persistência de dados |
| PostgreSQL | 12+ | Banco de dados (produção) |
| Redis | 6+ | Cache distribuído (reativo) |
| MapStruct | 1.6.3 | Mapeamento automático de objetos |
| Lombok | 1.18.30 | Redução de boilerplate |
| RestClient | 4.0.2 | Cliente HTTP para integração |
| Jakarta Validation | 4.0.2 | Validação de dados |
| JUnit 5 | 4.0.2 | Testes unitários |
| Mockito | 4.0.2 | Mock de dependências |

## 📁 Estrutura do Projeto

```
pix-key-service/
├── src/main/java/com/financeiro/pixkey/
│   ├── controller/
│   │   └── PixKeyController.java          # Endpoints da API REST
│   ├── service/
│   │   ├── PixKeyService.java             # Lógica de negócio
│   │   ├── CacheService.java              # Gerenciamento de cache Redis
│   │   └── AccountServiceClient.java      # Cliente para Account Service
│   ├── repository/
│   │   └── PixKeyRepository.java          # Acesso a chaves PIX (JPA)
│   ├── entity/
│   │   ├── PixKey.java                    # Entidade de chave PIX
│   │   └── KeyTypeEnum.java               # Enum dos tipos de chaves
│   ├── config/
│   │   ├── mapper/
│   │   │   └── PixKeyMapper.java          # Mapeamento MapStruct
│   │   └── redis/
│   │       └── RedisConfig.java           # Configuração do Redis
│   ├── validator/
│   │   └── PixKeyValidator.java           # Validador de formato de chaves
│   ├── exception/
│   │   ├── InvalidPixKeyFormatException.java
│   │   ├── PixKeyAlreadyExistsException.java
│   │   ├── MaxPixKeysLimitException.java
│   │   ├── PixKeyNotFoundException.java
│   │   ├── AccountNotFoundException.java
│   │   ├── AccountServiceException.java
│   │   └── handler/
│   │       ├── GlobalExceptionHandler.java # Interceptador global
│   │       └── ErrorResponse.java          # Resposta padronizada
│   ├── dto/
│   │   ├── request/
│   │   │   └── CreatePixKeyRequestDTO.java
│   │   └── response/
│   │       ├── PixKeyResponseDTO.java
│   │       ├── PixKeyResumoDTO.java
│   │       └── ValidatePixKeyResponseDTO.java
│   └── PixKeyServiceApplication.java      # Classe principal
├── src/main/resources/
│   ├── application.properties              # Configurações gerais
│   └── application-test.properties         # Configurações de testes
├── src/test/java/
│   └── com/financeiro/pixkey/service/
│       └── PixKeyServiceTest.java          # Testes unitários
├── Dockerfile                              # Containerização
└── pom.xml                                 # Dependências Maven
```

## 🚀 Início Rápido

### Pré-requisitos

- Java 21+
- Maven 3.8+
- PostgreSQL 12+
- Redis 6+
- Account Service rodando em `http://localhost:8081`
- Docker e Docker Compose (opcional)

### Instalação Local

1. **Clone o repositório**
   ```bash
   git clone <repository-url>
   cd pix-key-service
   ```

2. **Configure o banco de dados PostgreSQL**
   ```properties
   # application.properties
   spring.datasource.url=jdbc:postgresql://localhost:5434/pixkey_db
   spring.datasource.username=postgres
   spring.datasource.password=secret
   ```

3. **Configure o Redis**
   ```properties
   spring.redis.host=localhost
   spring.redis.port=6379
   ```

4. **Configure a URL do Account Service**
   ```properties
   account.service.url=http://localhost:8081
   ```

5. **Compile e execute**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

6. **Verifique se está rodando**
   ```bash
   curl http://localhost:8082/api/v1/pix-keys/account/1
   ```

### Instalação com Docker Compose

1. **Crie um arquivo `docker-compose.yml`:**
   ```yaml
   version: '3.8'
   services:
     postgres-pixkey:
       image: postgres:15
       environment:
         POSTGRES_DB: pixkey_db
         POSTGRES_PASSWORD: secret
       ports:
         - "5434:5432"
       volumes:
         - postgres_pixkey_data:/var/lib/postgresql/data

     redis:
       image: redis:7
       ports:
         - "6379:6379"

     pix-key-service:
       build: .
       ports:
         - "8082:8082"
       depends_on:
         - postgres-pixkey
         - redis
       environment:
         SPRING_DATASOURCE_URL: jdbc:postgresql://postgres-pixkey:5432/pixkey_db
         SPRING_REDIS_HOST: redis
         SPRING_REDIS_PORT: 6379
         ACCOUNT_SERVICE_URL: http://account-service:8081
       restart: unless-stopped

   volumes:
     postgres_pixkey_data:
   ```

2. **Execute**
   ```bash
   docker-compose up
   ```

## 📚 API Endpoints

### 1️⃣ Criar Chave PIX
Cria uma nova chave PIX associada a uma conta.

```http
POST /api/v1/pix-keys
Content-Type: application/json

{
  "accountId": 1,
  "keyType": "CPF",
  "keyValue": "12345678901"
}
```

**Resposta (201 CREATED)**
```json
{
  "id": 1,
  "accountId": 1,
  "keyType": "CPF",
  "keyValue": "12345678901",
  "createdAt": "2024-01-21T10:30:00Z",
  "active": true
}
```

**Tipos de Chaves Suportadas**

| Tipo | Formato | Exemplo | Validação |
|------|---------|---------|-----------|
| CPF | 11 dígitos | 12345678901 | 11 dígitos numéricos |
| CNPJ | 14 dígitos | 12345678000191 | 14 dígitos numéricos |
| EMAIL | Email válido | usuario@example.com | Formato de email válido |
| PHONE | 10-11 dígitos | 11999999999 | 10 ou 11 dígitos |
| RANDOM | UUID | (auto-gerado) | UUID gerado automaticamente |

**Validações**
- Conta deve existir (validado com Account Service)
- Máximo 5 chaves ativas por conta
- Não pode existir 2 chaves do mesmo tipo na mesma conta
- Formato deve ser válido conforme tipo

**Erros Possíveis**
- `400 BAD_REQUEST`: Formato de chave inválido
- `404 NOT_FOUND`: Conta não encontrada
- `409 CONFLICT`: Chave já existe ou limite de 5 chaves atingido
- `503 SERVICE_UNAVAILABLE`: Account Service indisponível

---

### 2️⃣ Listar Chaves por Conta
Lista todas as chaves PIX ativas de uma conta (resumido).

```http
GET /api/v1/pix-keys/account/{accountId}
```

**Resposta (200 OK)**
```json
[
  {
    "id": 1,
    "keyType": "CPF",
    "keyValue": "12345678901"
  },
  {
    "id": 2,
    "keyType": "EMAIL",
    "keyValue": "usuario@example.com"
  },
  {
    "id": 3,
    "keyType": "RANDOM",
    "keyValue": "f47ac10b-58cc-4372-a567-0e02b2c3d479"
  }
]
```

**Validações**
- Conta deve existir

**Erros Possíveis**
- `404 NOT_FOUND`: Conta não encontrada
- `503 SERVICE_UNAVAILABLE`: Account Service indisponível

---

### 3️⃣ Deletar Chave PIX
Deleta (desativa) uma chave PIX via soft delete.

```http
DELETE /api/v1/pix-keys/{pixKeyId}
```

**Resposta (204 NO CONTENT)**
```
(sem corpo)
```

**Funcionamento**
- Marca a chave como `active = false`
- Invalida cache
- Não remove do banco (auditoria)

**Erros Possíveis**
- `404 NOT_FOUND`: Chave PIX não encontrada

---

### 4️⃣ Validar Chave PIX
Valida uma chave PIX e retorna informações associadas.

```http
GET /api/v1/pix-keys/validate/{pixKeyValue}
```

**Resposta (200 OK)**
```json
{
  "accountId": 1,
  "keyType": "CPF",
  "keyValue": "12345678901"
}
```

**Como funciona:**
1. Verifica se chave está no cache Redis (~1ms)
2. Se sim, valida se ainda está ativa e retorna
3. Se não, busca no banco (~50ms)
4. Se encontrada e ativa, armazena em cache (10 minutos)

**Erros Possíveis**
- `404 NOT_FOUND`: Chave PIX não encontrada ou inativa

---

### 5️⃣ Validar Existência (Interno)
Endpoint interno para outros serviços verificarem se uma chave PIX existe.

```http
POST /api/v1/pix-keys/internal/validate/{pixKeyValue}
```

**Resposta (200 OK)**
```json
true
```

ou

```json
false
```

**Nota:** Este endpoint é protegido para uso interno entre serviços (ex: Transaction Service).

---

## ⚡ Cache Redis

### Estratégia de Cache

```
GET /api/v1/pix-keys/validate/{pixKeyValue}
    ↓
Redis ("pix:{keyValue}")
    ↓
Encontrado? → Valida se ativo → Retorna imediatamente ⚡ (~1ms)
    ↓
Não encontrado? → Busca no banco → Armazena em Redis (TTL: 10 min)
```

### Configuração

```java
@Service
public class CacheService {
    private static final Long CACHE_TTL = 10L;  // 10 minutos
    private static final String PIX_KEY_PREFIX = "pix:";
    
    public void cachePixKey(String keyValue, Long accountId) {
        String key = PIX_KEY_PREFIX + keyValue;
        redisTemplate.opsForValue().set(key, accountId.toString(), 
            CACHE_TTL, TimeUnit.MINUTES);
    }
}
```

### Invalidação de Cache

Quando uma chave é deletada:
1. Delete cache (invalida)
2. Marca como inativa no banco
3. Próxima consulta refaz query

Garante consistência entre cache e banco! ✅

---

## 🏗️ MapStruct - Mapeamento Automático

O projeto usa **MapStruct** para converter entre entidades e DTOs automaticamente em tempo de compilação.

```java
@Mapper(componentModel = "spring")
public interface PixKeyMapper {
    PixKey toEntity(CreatePixKeyRequestDTO dto);
    PixKeyResponseDTO toResponseDTO(PixKey pixKey);
    List<PixKeyResumoDTO> toListResumoDTO(List<PixKey> pixKeyList);
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
  "status": 400,
  "error": "Bad Request",
  "message": "CPF deve conter 11 dígitos",
  "path": "/api/v1/pix-keys"
}
```

### Exceções Tratadas

| Exceção | Status | Erro | Descrição |
|---------|--------|------|-----------|
| `InvalidPixKeyFormatException` | 400 | Bad Request | Formato inválido para chave |
| `PixKeyAlreadyExistsException` | 409 | Conflict | Chave já cadastrada na conta |
| `MaxPixKeysLimitException` | 409 | Conflict | Limite de 5 chaves atingido |
| `PixKeyNotFoundException` | 404 | Not Found | Chave PIX não encontrada |
| `AccountNotFoundException` | 404 | Not Found | Conta não encontrada |
| `AccountServiceException` | 503 | Service Unavailable | Account Service indisponível |
| `MethodArgumentNotValidException` | 400 | Bad Request | Validação de DTO falhou |
| `MethodArgumentTypeMismatchException` | 400 | Bad Request | Tipo de parâmetro inválido |
| `Exception` (genérica) | 500 | Internal Server Error | Erro inesperado |

---

## 🧪 Testes

O projeto inclui testes unitários abrangentes usando JUnit 5 e Mockito.

### Executar Testes
```bash
mvn test
```

### Cobertura de Testes

Os testes cobrem:
- ✅ Criar chave PIX com sucesso
- ✅ Erro ao criar segunda chave do mesmo tipo
- ✅ Erro com CPF inválido
- ✅ Soft delete funcionando corretamente
- ✅ Cache funcionando ao validar chave

### Exemplo de Teste

```java
@Test
@DisplayName("Deve criar chave Pix com sucesso quando todos os dados forem válidos")
void shouldCreatePixKeySuccessfully() {
    when(accountServiceClient.validateAccountExists(1L)).thenReturn(true);
    when(pixKeyRepository.countByAccountIdAndActiveTrue(1L)).thenReturn(0L);
    when(pixKeyRepository.existsByAccountIdAndKeyTypeAndActiveTrue(
        1L, KeyTypeEnum.CPF)).thenReturn(false);
    when(pixKeyRepository.save(any(PixKey.class))).thenReturn(pixKey);
    when(pixKeyMapper.toResponseDTO(pixKey)).thenReturn(pixKeyResponseDTO);

    PixKeyResponseDTO result = pixKeyService.create(createPixKeyRequestDTO);

    assertNotNull(result);
    assertEquals(1L, result.accountId());
    assertEquals("CPF", result.keyType());
    verify(cacheService, times(1)).cachePixKey("12345678901", 1L);
}
```

---

## 📊 Modelo de Dados

### PixKey (tbl_pixkey)

```sql
CREATE TABLE tbl_pixkey (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL,
    key_type VARCHAR(20) NOT NULL,
    key_value VARCHAR(255) UNIQUE NOT NULL,
    active BOOLEAN DEFAULT true NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    inactivated_at TIMESTAMP WITH TIME ZONE NULL
);

-- Índices para performance
CREATE INDEX idx_account_id_active ON tbl_pixkey(account_id, active);
CREATE INDEX idx_key_value_active ON tbl_pixkey(key_value, active);
CREATE INDEX idx_key_type_account ON tbl_pixkey(key_type, account_id);
```

---

## 🎯 Fluxo Completo - Criar Chave PIX

```
1. POST /api/v1/pix-keys { accountId, keyType, keyValue }
   ↓
2. PixKeyController.create() recebe a requisição
   ↓
3. @Valid valida o DTO
   - accountId é nulo?
   - keyType é nulo?
   - keyValue é nulo?
   ↓
4. PixKeyService.create() executa
   ↓
5. AccountServiceClient.validateAccountExists(accountId)
   - Conta existe?
   ↓
6. PixKeyValidator.validate() (conforme keyType)
   - CPF: 11 dígitos?
   - CNPJ: 14 dígitos?
   - EMAIL: formato válido?
   - PHONE: 10-11 dígitos?
   - RANDOM: (sem validação)
   ↓
7. countByAccountIdAndActiveTrue(accountId)
   - Conta já tem 5 chaves?
   ↓
8. existsByAccountIdAndKeyTypeAndActiveTrue(accountId, keyType)
   - Já existe chave desse tipo?
   ↓
9. Se RANDOM, gera UUID
   Senão, usa keyValue fornecido
   ↓
10. pixKeyRepository.save(pixKey)
    - Persiste no banco
    - @CreationTimestamp preenche createdAt
    ↓
11. cacheService.cachePixKey(keyValue, accountId)
    - Armazena em Redis (10 min)
    ↓
12. PixKeyMapper.toResponseDTO()
    - Converte para Response DTO
    ↓
13. Retorna 201 CREATED
```

---

## 🌍 Configuração de Ambientes

### application.properties (Desenvolvimento)
```properties
spring.application.name=Pix Key Service
server.port=8082

spring.datasource.url=jdbc:postgresql://localhost:5434/pixkey_db
spring.datasource.username=postgres
spring.datasource.password=secret

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

spring.redis.host=localhost
spring.redis.port=6379

account.service.url=http://localhost:8081
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
- **Validação com cache**: ~1ms vs ~50ms (sem cache)
- **Redução de queries**: 85%+ para chaves frequentes
- **TTL**: 10 minutos (customizável)
- **Invalidação**: Automática ao deletar chave

### Índices de Banco
- `(account_id, active)`: Listagem rápida por conta
- `(key_value, active)`: Busca rápida por chave
- `(key_type, account_id)`: Validação de duplicação

### Soft Delete
- Preserva histórico (auditoria)
- Não afeta performance (query filtra `active = true`)

---

## 🐛 Troubleshooting

### Account Service não conecta
```bash
# Verifique se Account Service está rodando
curl http://localhost:8081/api/v1/accounts/1

# Se não, inicie o Account Service
cd ../account-service
mvn spring-boot:run
```

### Redis não conecta
```bash
# Verifique se Redis está rodando
redis-cli ping
# Deve retornar: PONG

# Se não estiver rodando:
redis-server
```

### Erro "Chave PIX já existe"
```bash
# Verifique chaves ativas
SELECT * FROM tbl_pixkey WHERE account_id = 1 AND active = true;

# Para deletar (soft delete via API):
DELETE /api/v1/pix-keys/{pixKeyId}
```

### MapStruct não gera código
```bash
mvn clean install

# Se persistir, verifique o pom.xml:
# Certifique-se de ter o annotationProcessorPath configurado para Lombok e MapStruct
```

---

## 📞 Integração com Outros Serviços

### Auth Service
O Pix Key Service pode usar tokens do Auth Service para autorização em futuras versões.

### Account Service
Valida existência de conta antes de criar chave:

```http
POST /api/v1/accounts/internal/validate/{accountId}
```

### Transaction Service (Futuro)
Poderá chamar para validar chaves PIX antes de transações:

```http
GET /api/v1/pix-keys/validate/{pixKeyValue}
```

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