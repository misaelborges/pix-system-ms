# Auth Service 🔐

Um microserviço de autenticação robusto e seguro, construído com Spring Boot 4.0.1 e Spring Security, oferecendo funcionalidades completas de login, validação de tokens JWT e gerenciamento de sessões.

## 📋 Visão Geral

O Auth Service é responsável por gerenciar a autenticação e autorização em toda a aplicação, utilizando JWT (JSON Web Tokens) com criptografia RSA. Ele fornece endpoints para login, refresh de tokens e validação de tokens, com tratamento abrangente de exceções e validações robustas.

## ✨ Funcionalidades

- **Autenticação com JWT**: Geração segura de access tokens e refresh tokens
- **Criptografia RSA**: Chaves públicas e privadas para máxima segurança
- **Validação de Credenciais**: Verificação de email e senha com BCrypt
- **Refresh Token**: Renovação de tokens expirados sem necessidade de re-login
- **Validação de Token**: Endpoint para verificar validade e extrair informações do token
- **Controle de Roles**: Suporte a múltiplas funções (ADMIN, BASIC)
- **Tratamento Global de Exceções**: Respostas padronizadas para todos os erros
- **Validação de Entrada**: Validações rigorosas usando Jakarta Validation

## 🛠️ Tecnologias

| Tecnologia | Versão | Uso |
|-----------|--------|-----|
| Java | 21 | Linguagem principal |
| Spring Boot | 4.0.1 | Framework base |
| Spring Security | 4.0.1 | Autenticação e autorização |
| Spring Data JPA | 4.0.1 | Persistência de dados |
| PostgreSQL | 42.7.8 | Banco de dados (produção) |
| H2 | 2.4.2 | Banco de dados (testes) |
| JWT (Nimbus) | 10.4 | Geração e validação de tokens |
| BCrypt | 7.0.2 | Hash de senhas |
| Lombok | 1.18.42 | Redução de boilerplate |
| JUnit 5 | 6.0.1 | Testes unitários |
| Mockito | 5.20 | Mock de dependências |

## 📁 Estrutura do Projeto

```
auth-service/
├── src/main/java/com/financeiro/auth/
│   ├── controller/
│   │   └── AuthController.java          # Endpoints da API
│   ├── service/
│   │   ├── AuthService.java             # Lógica de autenticação
│   │   └── JwtService.java              # Geração e validação de JWT
│   ├── repository/
│   │   ├── UserRepository.java          # Acesso a usuários
│   │   └── RoleRepository.java          # Acesso a roles
│   ├── entity/
│   │   ├── User.java                    # Entidade de usuário
│   │   └── Role.java                    # Entidade de papel
│   ├── dto/
│   │   ├── request/
│   │   │   ├── LoginRequestDTO.java
│   │   │   └── RefreshTokenRequestDTO.java
│   │   └── response/
│   │       ├── AuthResponseDTO.java
│   │       └── ValidateTokenResponseDTO.java
│   ├── exception/
│   │   ├── UserNotFoundException.java
│   │   ├── InvalidCredentialsException.java
│   │   ├── TokenExpiredException.java
│   │   └── handler/
│   │       ├── GlobalExceptionHandler.java
│   │       └── ErrorResponse.java
│   ├── config/
│   │   └── security/
│   │       └── SecurityConfig.java      # Configuração de segurança
│   └── AuthServiceApplication.java      # Classe principal
├── src/main/resources/
│   ├── application.properties            # Configurações gerais
│   ├── app.pub                           # Chave pública RSA
│   └── app.key                           # Chave privada RSA
├── src/test/java/
│   └── com/financeiro/auth/service/
│       └── AuthServiceTest.java          # Testes unitários
├── Dockerfile                            # Containerização
└── pom.xml                               # Dependências Maven
```

## 🚀 Início Rápido

### Pré-requisitos

- Java 21+
- Maven 3.8+
- PostgreSQL 12+
- Docker (opcional)

### Instalação Local

1. **Clone o repositório**
   ```bash
   git clone <repository-url>
   cd auth-service
   ```

2. **Configure o banco de dados**
   ```properties
   # application.properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/auth_db
   spring.datasource.username=seu_usuario
   spring.datasource.password=sua_senha
   ```

3. **Gere as chaves RSA** (se não existirem)
   ```bash
   # Gerar chave privada
   openssl genrsa -out src/main/resources/app.key 2048
   
   # Gerar chave pública
   openssl rsa -in src/main/resources/app.key -pubout -out src/main/resources/app.pub
   ```

4. **Compile e execute**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

5. **Verifique se está rodando**
   ```bash
   curl http://localhost:8080/health
   ```

### Instalação com Docker

1. **Build da imagem**
   ```bash
   docker build -t auth-service:0.0.1 .
   ```

2. **Execute o container**
   ```bash
   docker run -p 8080:8080 \
     -e SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/auth_db \
     -e SPRING_DATASOURCE_USERNAME=postgres \
     -e SPRING_DATASOURCE_PASSWORD=secret \
     auth-service:0.0.1
   ```

## 📚 API Endpoints

### 1. Login
Autentica um usuário e retorna access token e refresh token.

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "usuario@example.com",
  "password": "Senha@123"
}
```

**Resposta (200 OK)**
```json
{
  "acessToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600,
  "userId": 1
}
```

**Validações**
- Email deve ser um email válido
- Senha deve conter: mínimo 8 caracteres, letras maiúsculas, minúsculas, números e caracteres especiais

**Erros Possíveis**
- `404 NOT_FOUND`: Usuário não encontrado
- `401 UNAUTHORIZED`: Email ou senha inválidos

### 2. Refresh Token
Renova um access token usando o refresh token.

```http
POST /api/v1/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Resposta (200 OK)**
```json
{
  "acessToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600,
  "userId": 1
}
```

**Erros Possíveis**
- `401 UNAUTHORIZED`: Token expirado ou inválido
- `404 NOT_FOUND`: Usuário não encontrado

### 3. Validar Token
Valida um token e retorna informações do usuário e suas roles.

```http
POST /api/v1/auth/validate?token=eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Resposta (200 OK)**
```json
{
  "isValid": true,
  "userId": 1,
  "roles": ["ADMIN", "BASIC"]
}
```

**Resposta (token inválido)**
```json
{
  "isValid": false,
  "userId": null,
  "roles": []
}
```

## 🔒 Segurança

### Configuração JWT

| Propriedade | Descrição | Valor Padrão |
|------------|-----------|--------------|
| `jwt.access-token.expiration` | Duração do access token (segundos) | 3600 (1 hora) |
| `jwt.refresh-token.expiration` | Duração do refresh token (segundos) | 604800 (7 dias) |
| `jwt.public.key` | Caminho da chave pública RSA | `classpath:app.pub` |
| `jwt.private.key` | Caminho da chave privada RSA | `classpath:app.key` |

### Claims do Token

**Access Token contém:**
- `sub`: ID do usuário
- `email`: Email do usuário
- `roles`: Lista de roles do usuário
- `iss`: Issuer (auth)
- `iat`: Issued at
- `exp`: Expiration time

**Refresh Token contém:**
- `sub`: ID do usuário
- `type`: "refresh"
- `iss`: Issuer (auth)
- `iat`: Issued at
- `exp`: Expiration time

## 🧪 Testes

O projeto inclui testes unitários abrangentes usando JUnit 5 e Mockito.

### Executar testes
```bash
mvn test
```

### Cobertura de testes

Os testes cobrem:
- ✅ Login com sucesso com credenciais válidas
- ✅ Erro ao não encontrar usuário
- ✅ Erro com senha inválida
- ✅ Refresh token com sucesso
- ✅ Validação de token

## 🌍 Configuração de Ambientes

### application.properties (Desenvolvimento)
```properties
spring.application.name=Auth Service
server.port=8080
spring.datasource.url=jdbc:postgresql://localhost:5432/auth_db
spring.datasource.username=postgres
spring.datasource.password=secret
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

jwt.access-token.expiration=3600
jwt.refresh-token.expiration=604800
```

### application-test.properties (Testes)
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
```

## 📊 Modelo de Dados

### User (tbl_user)
```sql
CREATE TABLE tbl_user (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE
);
```

### Role (tbl_role)
```sql
CREATE TABLE tbl_role (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);
```

### User_Roles (tbl_user_roles)
```sql
CREATE TABLE tbl_user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES tbl_user(id),
    FOREIGN KEY (role_id) REFERENCES tbl_role(id)
);
```

## 🛠️ Tratamento de Erros

Todos os erros retornam uma resposta padronizada:

```json
{
  "timestamp": "2024-01-21T10:30:00",
  "status": 401,
  "error": "INVALID_CREDENTIALS",
  "message": "Email ou senha inválidos",
  "path": "/api/v1/auth/login",
  "validationErrors": null
}
```

### Códigos de Erro

| Código | Erro | Descrição |
|--------|------|-----------|
| 401 | `INVALID_CREDENTIALS` | Email ou senha inválidos |
| 401 | `TOKEN_EXPIRED` | Token expirado |
| 404 | `USER_NOT_FOUND` | Usuário não encontrado |
| 400 | `VALIDATION_ERROR` | Dados de entrada inválidos |
| 500 | `INTERNAL_SERVER_ERROR` | Erro interno do servidor |

## 🤝 Contribuindo

1. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
2. Commit suas mudanças (`git commit -m 'Add AmazingFeature'`)
3. Push para a branch (`git push origin feature/AmazingFeature`)
4. Abra um Pull Request

## 📝 Licença

Este projeto está licenciado sob a Licença MIT - veja o arquivo LICENSE para detalhes.

## 📧 Contato

Para dúvidas ou sugestões, abra uma issue no repositório.

---

**Versão**: 0.0.1 | **Status**: Development | **Última atualização**: Janeiro 2026
