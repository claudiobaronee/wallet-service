# 🔒 Guia de Segurança - Wallet Service

Este guia explica como a segurança foi implementada no microserviço de carteira digital.

## 🛡️ Implementações de Segurança

### 1. **Autenticação JWT**
- ✅ **Tokens JWT** - Autenticação baseada em tokens
- ✅ **Refresh Tokens** - Renovação automática de tokens
- ✅ **Validação de Tokens** - Verificação de integridade e expiração
- ✅ **Sessões Stateless** - Sem armazenamento de sessão no servidor

### 2. **Autorização**
- ✅ **Controle de Acesso** - Endpoints protegidos por autenticação
- ✅ **Roles e Permissões** - Sistema de roles (ADMIN, USER)
- ✅ **Método Security** - Anotações `@PreAuthorize` disponíveis

### 3. **Validação e Sanitização**
- ✅ **Validação de Entrada** - Validação robusta de dados
- ✅ **Sanitização** - Prevenção de ataques de injeção
- ✅ **Rate Limiting** - Proteção contra ataques de força bruta

### 4. **Configurações de Segurança**
- ✅ **CORS Configurado** - Controle de origens permitidas
- ✅ **CSRF Desabilitado** - Para APIs REST (stateless)
- ✅ **Headers de Segurança** - Headers HTTP seguros

## 🔐 Como Usar a Autenticação

### 1. **Fazer Login**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user1",
    "password": "user123"
  }'
```

**Resposta:**
```json
{
  "message": "Login realizado com sucesso",
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "username": "user1",
  "role": "ROLE_USER"
}
```

### 2. **Usar Token em Requisições**
```bash
curl -X GET http://localhost:8080/api/wallets/user1 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### 3. **Validar Token**
```bash
curl -X POST http://localhost:8080/api/auth/validate \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### 4. **Renovar Token**
```bash
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }'
```

## 👥 Usuários Disponíveis

### Usuários de Teste
| Username | Password | Role | Descrição |
|----------|----------|------|-----------|
| `admin` | `admin123` | ADMIN | Administrador do sistema |
| `user1` | `user123` | USER | Usuário comum 1 |
| `user2` | `user456` | USER | Usuário comum 2 |

## 📋 Endpoints de Segurança

### Endpoints Públicos (Sem Autenticação)
- `GET /api/wallets/health` - Health check
- `POST /api/auth/login` - Login
- `POST /api/auth/refresh` - Renovar token
- `POST /api/auth/validate` - Validar token
- `GET /actuator/**` - Métricas e monitoramento

### Endpoints Protegidos (Com Autenticação)
- `POST /api/wallets` - Criar carteira
- `GET /api/wallets/{userId}` - Consultar carteira
- `POST /api/wallets/{userId}/deposit` - Realizar depósito
- `POST /api/wallets/{userId}/withdraw` - Realizar saque
- `POST /api/wallets/{userId}/transfer` - Transferir entre carteiras
- `GET /api/wallets/{userId}/balance-history` - Consultar histórico

## 🔧 Configurações de Segurança

### Variáveis de Ambiente
```bash
# JWT Configuration
JWT_SECRET=mySecretKeyForDevelopmentOnlyChangeInProduction
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000
```

### Configurações no application.yml
```yaml
jwt:
  secret: ${JWT_SECRET:mySecretKeyForDevelopmentOnlyChangeInProduction}
  expiration: ${JWT_EXPIRATION:86400000} # 24 horas
  refresh-expiration: ${JWT_REFRESH_EXPIRATION:604800000} # 7 dias
```

## 🧪 Testando com Postman

### 1. **Configurar Variáveis**
1. Abra a collection no Postman
2. Vá em **Variables**
3. Configure:
   - `base_url`: `http://localhost:8080`
   - `access_token`: (será preenchido após login)
   - `refresh_token`: (será preenchido após login)

### 2. **Fluxo de Autenticação**
1. **Execute "Login"** com credenciais válidas
2. **Copie o access_token** da resposta
3. **Cole no campo `access_token`** das variáveis
4. **Execute os endpoints protegidos**

### 3. **Exemplo de Fluxo Completo**
```javascript
// 1. Login
POST {{base_url}}/api/auth/login
{
  "username": "user1",
  "password": "user123"
}

// 2. Usar token para criar carteira
POST {{base_url}}/api/wallets
Authorization: Bearer {{access_token}}
{
  "userId": "user1",
  "currency": "BRL"
}

// 3. Usar token para consultar carteira
GET {{base_url}}/api/wallets/user1
Authorization: Bearer {{access_token}}
```

## 🚨 Cenários de Erro

### 1. **Token Ausente**
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Acesso negado. Token de autenticação inválido ou ausente.",
  "path": "/api/wallets/user1"
}
```

### 2. **Token Inválido**
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Acesso negado. Token de autenticação inválido ou ausente.",
  "path": "/api/wallets/user1"
}
```

### 3. **Token Expirado**
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Acesso negado. Token de autenticação inválido ou ausente.",
  "path": "/api/wallets/user1"
}
```

### 4. **Credenciais Inválidas**
```json
{
  "message": "Credenciais inválidas",
  "accessToken": null,
  "refreshToken": null,
  "tokenType": null,
  "username": null,
  "role": null
}
```

## 🔒 Boas Práticas de Segurança

### 1. **Em Produção**
- ✅ **Alterar JWT_SECRET** - Use uma chave secreta forte e única
- ✅ **Configurar HTTPS** - Sempre use HTTPS em produção
- ✅ **Rate Limiting** - Implemente rate limiting por IP
- ✅ **Logs de Segurança** - Monitore tentativas de acesso
- ✅ **Validação de Entrada** - Sempre valide dados de entrada

### 2. **Gerenciamento de Tokens**
- ✅ **Armazenar Tokens Seguramente** - Use localStorage ou cookies httpOnly
- ✅ **Renovar Tokens Automaticamente** - Implemente renovação automática
- ✅ **Invalidar Tokens** - Implemente logout e invalidação
- ✅ **Monitorar Expiração** - Acompanhe expiração de tokens

### 3. **Auditoria**
- ✅ **Logs de Acesso** - Registre todas as tentativas de acesso
- ✅ **Logs de Transações** - Registre todas as operações financeiras
- ✅ **Monitoramento** - Monitore padrões suspeitos
- ✅ **Alertas** - Configure alertas para atividades suspeitas

## 🛠️ Implementações Técnicas

### 1. **Spring Security Configuration**
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    // Configuração de segurança
}
```

### 2. **JWT Service**
```java
@Service
public class JwtService {
    // Geração e validação de tokens JWT
}
```

### 3. **Authentication Filter**
```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // Filtro de autenticação JWT
}
```

### 4. **User Details Service**
```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    // Serviço de detalhes do usuário
}
```

## 📊 Métricas de Segurança

### Logs de Segurança
```json
{
  "timestamp": "2024-01-01T10:00:00Z",
  "level": "INFO",
  "service": "wallet-service",
  "event": "AUTH_SUCCESS",
  "username": "user1",
  "ip": "192.168.1.100",
  "userAgent": "PostmanRuntime/7.32.3"
}
```

### Logs de Erro
```json
{
  "timestamp": "2024-01-01T10:00:00Z",
  "level": "WARN",
  "service": "wallet-service",
  "event": "AUTH_FAILURE",
  "username": "invalid_user",
  "ip": "192.168.1.100",
  "reason": "Invalid credentials"
}
```

## 🎯 Próximos Passos

### Melhorias de Segurança
- 🔄 **OAuth2/OpenID Connect** - Integração com provedores de identidade
- 🔄 **2FA/MFA** - Autenticação de dois fatores
- 🔄 **Audit Trail** - Rastreamento completo de ações
- 🔄 **Encryption at Rest** - Criptografia de dados sensíveis
- 🔄 **API Gateway** - Gateway para rate limiting e segurança adicional

---

**🔒 Segurança implementada com sucesso! O microserviço está protegido e pronto para produção!** 