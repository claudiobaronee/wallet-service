# Wallet Service - Microserviço de Carteira Digital

Um microserviço robusto para gerenciamento de carteiras digitais, construído com arquitetura hexagonal, Spring Boot e foco em alta confiabilidade, rastreabilidade e auditabilidade.

## 🚀 Funcionalidades

- ✅ **Criação de carteiras** - Criação de carteiras digitais para usuários
- ✅ **Consultas de saldo** - Consulta de saldo atual da carteira
- ✅ **Depósitos** - Realização de depósitos na carteira
- ✅ **Saques** - Realização de saques da carteira (com validação de saldo)
- ✅ **Transferências** - Transferência entre carteiras de usuários
- ✅ **Histórico de saldo** - Consulta do histórico completo de movimentações
- ✅ **Health check** - Verificação de status do serviço
- ✅ **Validações robustas** - Validação completa de dados de entrada
- ✅ **Transações atômicas** - Garantia de consistência de dados
- ✅ **Rastreabilidade** - Logs detalhados e IDs de transação únicos

## 🏗️ Arquitetura

### Hexagonal Architecture (Ports & Adapters)
```
┌─────────────────────────────────────────────────────────────┐
│                    Application Layer                        │
│  ┌─────────────────┐  ┌─────────────────┐  ┌──────────────┐ │
│  │   Controllers   │  │   Use Cases     │  │   Services   │ │
│  └─────────────────┘  └─────────────────┘  └──────────────┘ │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                     Domain Layer                            │
│  ┌─────────────────┐  ┌─────────────────┐  ┌──────────────┐ │
│  │    Entities     │  │  Value Objects  │  │ Domain Events│ │
│  └─────────────────┘  └─────────────────┘  └──────────────┘ │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                  Infrastructure Layer                       │
│  ┌─────────────────┐  ┌─────────────────┐  ┌──────────────┐ │
│  │   Repositories  │  │   Controllers   │  │   Security   │ │
│  └─────────────────┘  └─────────────────┘  └──────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

### Tecnologias Utilizadas

- **Java 17** - Linguagem principal
- **Spring Boot 3.x** - Framework principal
- **PostgreSQL** - Banco de dados principal
- **Redis** - Cache e sessões
- **Docker & Docker Compose** - Containerização
- **Flyway** - Migração de banco de dados
- **Resilience4j** - Padrões de resiliência
- **Prometheus & Grafana** - Monitoramento e observabilidade
- **Maven** - Gerenciamento de dependências

## 🛠️ Configuração e Execução

### Pré-requisitos

- Java 17+
- Docker e Docker Compose

### 1. Clone o repositório
```bash
git clone <repository-url>
cd wallet-service
```

### 2. Configure as variáveis de ambiente
```bash
cp env.example .env
# Edite o arquivo .env com suas configurações
```

### 3. Execute com Docker Compose

#### Opção 1: Script Automático (Recomendado)
```bash
# Windows (PowerShell)
.\dev-setup.ps1

# Linux/Mac
chmod +x dev-setup.sh
./dev-setup.sh
```

#### Opção 2: Comando Manual
```bash
# Parar containers existentes
docker-compose down

# Construir e iniciar
docker-compose up --build -d

# Verificar logs
docker-compose logs -f wallet-service
```

### 4. Verifique se os serviços estão rodando
```bash
docker-compose ps
```

### 5. Acessos após inicialização
- **Wallet Service**: http://localhost:8080
- **Grafana**: http://localhost:3000 (admin/admin)
- **Prometheus**: http://localhost:9090

## 🔧 Configuração Unificada

O projeto agora utiliza uma **configuração unificada** que funciona tanto para desenvolvimento local quanto para Docker:

### Arquivos de Configuração
- **`application.yml`** - Configuração única com variáveis de ambiente
- **`env.example`** - Exemplo de variáveis de ambiente
- **`docker-compose.yml`** - Configuração dos serviços Docker

### Variáveis de Ambiente Principais
```bash
# Banco de Dados
SPRING_DATASOURCE_HOST=localhost  # postgres (Docker)
SPRING_DATASOURCE_PORT=5432
SPRING_DATASOURCE_DB=wallet_db

# Redis
REDIS_HOST=localhost  # redis (Docker)
REDIS_PORT=6379

# JWT
JWT_SECRET=your-secret-key
JWT_EXPIRATION=86400000

# Servidor
SERVER_PORT=8080
```

### Modos de Execução

#### Desenvolvimento Local
1. Configure o arquivo `.env` com `localhost` para hosts
2. Execute: `./mvnw spring-boot:run`

#### Docker Completo
1. Execute: `docker-compose up -d`
2. Todos os serviços rodam em containers

#### Infraestrutura Docker + App Local
1. Execute: `.\dev-setup.ps1` (Windows) ou `./dev-setup.sh` (Linux/Mac)
2. Configure `.env` com `localhost` para hosts
3. Execute: `./mvnw spring-boot:run`

## 📡 API Endpoints

### Base URL
```
http://localhost:8080/api/wallets
```

### Endpoints Disponíveis

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/health` | Health check do serviço |
| `POST` | `/` | Criar nova carteira |
| `GET` | `/{userId}` | Consultar carteira |
| `POST` | `/{userId}/deposit` | Realizar depósito |
| `POST` | `/{userId}/withdraw` | Realizar saque |
| `POST` | `/{userId}/transfer` | Transferir entre carteiras |
| `GET` | `/{userId}/balance-history` | Consultar histórico de saldo |

## 🧪 Testando a API

### Usando a Collection do Postman

1. **Importe a collection**: Abra o Postman e importe o arquivo `Wallet_Service_API.postman_collection.json`

2. **Configure as variáveis**:
   - `base_url`: `http://localhost:8080`
   - `userId`: `user123` (ou qualquer ID de usuário)

3. **Execute os testes**:
   - **Health Check**: Verifica se o serviço está funcionando
   - **Cenários Básicos**: Criação, consulta, depósito, saque e transferência
   - **Testes de Validação**: Cenários de erro e validação
   - **Cenários Completos**: Fluxos end-to-end

### Exemplos de Uso

#### 1. Criar uma carteira
```bash
curl -X POST http://localhost:8080/api/wallets \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "currency": "BRL"
  }'
```

#### 2. Realizar um depósito
```bash
curl -X POST http://localhost:8080/api/wallets/user123/deposit \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 100.50,
    "currency": "BRL",
    "description": "Depósito inicial"
  }'
```

#### 3. Consultar saldo
```bash
curl -X GET http://localhost:8080/api/wallets/user123
```

## 📊 Monitoramento

### Prometheus
- URL: http://localhost:9090
- Métricas disponíveis:
  - Taxa de requisições
  - Tempo de resposta
  - Taxa de erro
  - Uso de recursos

### Grafana
- URL: http://localhost:3000
- Usuário: `admin`
- Senha: `admin`
- Dashboards pré-configurados para monitoramento

## 🔒 Segurança

### Autenticação JWT
- ✅ **Tokens JWT** - Autenticação baseada em tokens
- ✅ **Refresh Tokens** - Renovação automática de tokens
- ✅ **Validação de Tokens** - Verificação de integridade e expiração
- ✅ **Sessões Stateless** - Sem armazenamento de sessão no servidor

### Autorização
- ✅ **Controle de Acesso** - Endpoints protegidos por autenticação
- ✅ **Roles e Permissões** - Sistema de roles (ADMIN, USER)
- ✅ **Método Security** - Anotações `@PreAuthorize` disponíveis

### Validação e Sanitização
- ✅ **Validação de Entrada** - Validação robusta de dados
- ✅ **Sanitização** - Prevenção de ataques de injeção
- ✅ **Rate Limiting** - Proteção contra ataques de força bruta

### Configurações de Segurança
- ✅ **CORS Configurado** - Controle de origens permitidas
- ✅ **CSRF Desabilitado** - Para APIs REST (stateless)
- ✅ **Headers de Segurança** - Headers HTTP seguros

### Usuários de Teste
| Username | Password | Role | Descrição |
|----------|----------|------|-----------|
| `admin` | `admin123` | ADMIN | Administrador do sistema |
| `user1` | `user123` | USER | Usuário comum 1 |
| `user2` | `user456` | USER | Usuário comum 2 |

**📖 Para instruções detalhadas de segurança, consulte: `SECURITY_GUIDE.md`**

## 📝 Logs

Os logs são estruturados e incluem:
- IDs de transação únicos
- Timestamps precisos
- Contexto completo das operações
- Níveis de log apropriados (INFO, WARN, ERROR)

## 🧪 Testes

### Executar testes unitários
```bash
mvn test
```

### Executar testes de integração
```bash
mvn verify
```

## 📈 Status do Projeto

### ✅ Implementado (95%)
- ✅ Arquitetura hexagonal completa
- ✅ Todas as funcionalidades principais
- ✅ Validações robustas
- ✅ Transações atômicas
- ✅ Logs estruturados
- ✅ Monitoramento com Prometheus/Grafana
- ✅ Containerização com Docker
- ✅ Collection Postman completa
- ✅ Documentação atualizada
- ✅ **Segurança JWT completa**
- ✅ **Autenticação e autorização**
- ✅ **Sistema de roles e permissões**

### 🔄 Em Desenvolvimento (5%)
- 🔄 Testes de integração avançados
- 🔄 Otimizações de performance
- 🔄 Melhorias de segurança adicionais (OAuth2, 2FA)

## 🤝 Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.

## 🆘 Suporte

Para suporte e dúvidas:
- Abra uma issue no GitHub
- Consulte a documentação da API
- Use a collection do Postman para testes

---

**Desenvolvido com ❤️ usando Spring Boot e arquitetura hexagonal** 