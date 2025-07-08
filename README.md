# Wallet Service

A microservice for managing digital wallets, allowing deposit, withdrawal, and transfer operations between users.

## 🚀 Features

### Functional
- ✅ **Create Wallet**: Create wallets for users
- ✅ **Check Balance**: Retrieve the current wallet balance
- ✅ **Balance History**: Query the balance at specific points in the past
- ✅ **Deposit Funds**: Deposit money into wallets
- ✅ **Withdraw Funds**: Withdraw money from wallets
- ✅ **Transfer Funds**: Transfer between user wallets

### Non-Functional
- ✅ **Full Traceability**: All movements are recorded for auditing
- ✅ **High Availability**: Production-ready configuration
- ✅ **Monitoring**: Health checks and metrics
- ✅ **Resilience**: Circuit breakers and retry policies

## 🛠️ Technologies

- **Java 17** - Main language
- **Spring Boot 3.2.0** - Web framework
- **Spring Data JDBC** - Data persistence
- **PostgreSQL** - Database
- **Docker & Docker Compose** - Containerization
- **Flyway** - Database migration
- **Resilience4j** - Resilience patterns
- **Prometheus & Grafana** - Monitoring
- **OpenAPI/Swagger** - API documentation

## 📋 Prerequisites

- Docker and Docker Compose installed
- At least 4GB RAM available
- Port 8080 available

## 🚀 Installation and Running

### 1. Clone the repository
```bash
git clone <repository-url>
cd wallet-service
```

### 2. Configure environment variables
```bash
cp env.example .env
# Edit the .env file if needed
```

### 3. Start the project
```bash
docker-compose up -d
```

### 4. Check if it is running
```bash
curl http://localhost:8080/api/wallets/health
```

## 📚 API Endpoints

### Base URL: `http://localhost:8080/api/wallets`

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/health` | Service health check |
| `POST` | `/` | Create new wallet |
| `GET` | `/{userId}` | Get wallet |
| `POST` | `/{userId}/deposit` | Deposit funds |
| `POST` | `/{userId}/withdraw` | Withdraw funds |
| `POST` | `/{userId}/transfer` | Transfer between wallets |
| `GET` | `/{userId}/balance-history` | Balance history |

### Usage Examples

#### 1. Create Wallet
```bash
curl -X POST http://localhost:8080/api/wallets \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "currency": "BRL"
  }'
```

#### 2. Get Wallet
```bash
curl http://localhost:8080/api/wallets/user123
```

#### 3. Deposit Funds
```bash
curl -X POST http://localhost:8080/api/wallets/user123/deposit \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 100.50,
    "currency": "BRL"
  }'
```

#### 4. Withdraw Funds
```bash
curl -X POST http://localhost:8080/api/wallets/user123/withdraw \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 50.25,
    "currency": "BRL"
  }'
```

#### 5. Transfer Between Wallets
```bash
curl -X POST http://localhost:8080/api/wallets/user123/transfer \
  -H "Content-Type: application/json" \
  -d '{
    "targetUserId": "user456",
    "amount": 25.00,
    "currency": "BRL"
  }'
```

#### 6. Get Balance History
```bash
curl http://localhost:8080/api/wallets/user123/balance-history
```

## 🧪 Testing

### Run Tests
```bash
docker-compose exec wallet-service mvn test
```

### Test Coverage
```bash
docker-compose exec wallet-service mvn jacoco:report
```

## 📊 Monitoring

### Prometheus
- URL: http://localhost:9090
- Available metrics: transactions, balances, errors, latency

### Grafana
- URL: http://localhost:3000
- Pre-configured dashboards for monitoring

### Health Check
```bash
curl http://localhost:8080/api/wallets/health
```

## 🏗️ Architecture

### Hexagonal Pattern (Ports & Adapters)
```
┌─────────────────────────────────────────────────────────────┐
│                    Application Layer                        │
├─────────────────────────────────────────────────────────────┤
│                    Domain Layer                             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │   Wallet    │  │ Transaction │  │BalanceHistory│         │
│  └─────────────┘  └─────────────┘  └─────────────┘         │
├─────────────────────────────────────────────────────────────┤
│                  Infrastructure Layer                       │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │ Controllers │  │ Repositories │  │   Database  │         │
│  └─────────────┘  └─────────────┘  └─────────────┘         │
└─────────────────────────────────────────────────────────────┘
```

### Main Components

1. **Domain Layer**: Business entities (Wallet, Transaction, BalanceHistory)
2. **Application Layer**: Use cases and business rules
3. **Infrastructure Layer**: Controllers, Repositories, Database

## 🔒 Security & Traceability

### Traceability
- Each operation generates a unique `transactionId`
- Full balance history recorded
- Timestamps on all operations
- Structured logs for auditing

### Validations
- Sufficient balance check
- Currency validation
- Wallet status check
- Prevent transfer to the same wallet

## 📈 Scalability

### Implemented Strategies
- **Redis Cache**: For frequent queries
- **Connection Pooling**: HikariCP for PostgreSQL
- **Circuit Breakers**: Resilience4j for resilience
- **Metrics**: Prometheus for monitoring

### Possible Improvements
- **Event Sourcing**: For full audit
- **CQRS**: Read/write separation
- **Saga Pattern**: For distributed transactions
- **API Gateway**: For rate limiting and authentication

## 🕐 Design Decisions

### 1. Hexagonal Architecture
**Decision**: Use Ports & Adapters for decoupling
**Rationale**: Facilitates testing, maintenance, and system evolution

### 2. Spring Data JDBC vs JPA
**Decision**: Use Spring Data JDBC
**Rationale**: Simpler, better performance, full SQL control

### 3. PostgreSQL
**Decision**: Relational database PostgreSQL
**Rationale**: ACID, reliability, JSON support, open-source

### 4. Value Objects
**Decision**: `Money` class as value object
**Rationale**: Encapsulates monetary logic, immutability, type safety

### 5. Balance History
**Decision**: Separate table for history
**Rationale**: Full traceability, audit, performance

## ⚖️ Trade-offs

### 1. Simplicity vs Complexity
**Trade-off**: Simple but scalable architecture
**Rationale**: 6-8 hour project, but production-ready

### 2. Performance vs Traceability
**Trade-off**: Full history impacts performance
**Rationale**: Critical requirement for financial audit

### 3. Flexibility vs Security
**Trade-off**: Strict validations
**Rationale**: Financial system requires maximum security

### 4. Technology vs Deadline
**Trade-off**: Modern but well-known stack
**Rationale**: Spring Boot is mature and productive

## 🚨 Assumptions

### 1. Authentication/Authorization
**Assumption**: Not implemented (out of scope)
**Rationale**: Focus on core wallet features

### 2. Single Currency per Wallet
**Assumption**: One wallet = one currency
**Rationale**: Simplifies the model and avoids FX complexity

### 3. Synchronous Transactions
**Assumption**: All operations are synchronous
**Rationale**: Ensures immediate consistency

### 4. One User per Wallet
**Assumption**: 1:1 relationship between user and wallet
**Rationale**: Simplified model for the scope

## 📝 Logs & Debugging

### Structured Logs
```json
{
  "timestamp": "2024-01-01T10:00:00Z",
  "level": "INFO",
  "service": "wallet-service",
  "transactionId": "1234567890",
  "userId": "user123",
  "operation": "deposit",
  "amount": 100.50,
  "currency": "BRL"
}
```

### Debugging
```bash
# View service logs
docker-compose logs -f wallet-service

# Access the database
docker-compose exec postgres psql -U wallet_user -d wallet_db
```

## 🔧 Configuration

### Environment Variables
```bash
# Database
DB_HOST=postgres
DB_PORT=5432
DB_NAME=wallet_db
DB_USER=wallet_user
DB_PASSWORD=wallet_password

# Redis
REDIS_HOST=redis
REDIS_PORT=6379

# Application
SERVER_PORT=8080
LOG_LEVEL=INFO
```

## 📊 Available Metrics

- Transactions count
- Balance queries
- Errors
- Latency

## 🕒 Time Invested

Estimated time: 6-8 hours 