# 🏦 Wallet Service

A robust, enterprise-grade microservice for digital wallet management built with Spring Boot, following Domain-Driven Design (DDD) and Hexagonal Architecture principles.

## 🚀 Features

- **Wallet Management**: Create, query, and manage digital wallets
- **Financial Operations**: Deposit, withdraw, and transfer funds
- **Transaction History**: Complete audit trail of all operations
- **High Availability**: Circuit breakers, retry mechanisms, and resilience patterns
- **Security**: JWT authentication, rate limiting, and API key support
- **Performance**: Redis caching and optimized database queries
- **Observability**: Prometheus metrics, health checks, and structured logging
- **Event-Driven**: Asynchronous event processing for scalability

## 🏗️ Architecture

### Hexagonal Architecture (Ports & Adapters)

```
┌─────────────────────────────────────────────────────────────┐
│                    Application Layer                        │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │
│  │ Use Cases   │  │ DTOs        │  │ Events      │        │
│  └─────────────┘  └─────────────┘  └─────────────┘        │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                      Domain Layer                           │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │
│  │ Entities    │  │ Aggregates  │  │ Value Objs  │        │
│  └─────────────┘  └─────────────┘  └─────────────┘        │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                  Infrastructure Layer                       │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │
│  │ Controllers │  │ Repositories│  │ Security    │        │
│  └─────────────┘  └─────────────┘  └─────────────┘        │
└─────────────────────────────────────────────────────────────┘
```

### Technology Stack

- **Framework**: Spring Boot 3.2
- **Language**: Java 17
- **Database**: PostgreSQL 15
- **Cache**: Redis 7
- **Security**: Spring Security + JWT
- **Monitoring**: Prometheus + Grafana
- **Testing**: JUnit 5 + TestContainers
- **Documentation**: OpenAPI 3.0 (Swagger)

## 🛠️ Prerequisites

- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- PostgreSQL 15
- Redis 7

## 🚀 Quick Start

### 1. Clone and Setup

```bash
git clone <repository-url>
cd wallet-service
```

### 2. Start All Services

```bash
# Iniciar todos os serviços
docker-compose up -d

# Ver logs em tempo real
docker-compose logs -f

# Verificar status dos serviços
docker-compose ps
```

### 3. Access the Application

- **API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Health Check**: http://localhost:8080/actuator/health
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (admin/admin)

### 4. Stop Services

```bash
# Parar todos os serviços
docker-compose down

# Parar e remover volumes (limpa dados)
docker-compose down -v
```

### 5. Troubleshooting

```bash
# Ver logs de um serviço específico
docker-compose logs wallet-service

# Reiniciar um serviço específico
docker-compose restart wallet-service

# Rebuild e reiniciar
docker-compose up --build -d
```

## 🐳 Docker Only

Este projeto é configurado para rodar **apenas com Docker Compose**. Todos os serviços (PostgreSQL, Redis, Prometheus, Grafana) são gerenciados automaticamente.

## 📚 API Documentation

### Authentication

All API endpoints require authentication via JWT token:

```bash
# Get JWT token (implement your auth service)
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "user", "password": "password"}'

# Use token in requests
curl -H "Authorization: Bearer <your-jwt-token>" \
  http://localhost:8080/api/v1/wallets
```

### Core Endpoints

#### Create Wallet
```bash
POST /api/v1/wallets
{
  "userId": "user123",
  "currency": "BRL"
}
```

#### Get Wallet Balance
```bash
GET /api/v1/wallets/{walletId}/balance
```

#### Deposit Funds
```bash
POST /api/v1/wallets/{walletId}/deposit
{
  "amount": 100.50,
  "currency": "BRL",
  "description": "Initial deposit"
}
```

#### Withdraw Funds
```bash
POST /api/v1/wallets/{walletId}/withdraw
{
  "amount": 50.25,
  "currency": "BRL",
  "description": "ATM withdrawal"
}
```

#### Transfer Funds
```bash
POST /api/v1/wallets/{walletId}/transfer
{
  "targetWalletId": "wallet456",
  "amount": 25.00,
  "currency": "BRL",
  "description": "Payment to friend"
}
```

#### Get Transaction History
```bash
GET /api/v1/wallets/{walletId}/transactions?page=0&size=20
```

## 🧪 Testing

### Unit Tests
```bash
mvn test
```

### Integration Tests
```bash
mvn verify
```

### Test Coverage
```bash
mvn jacoco:report
open target/site/jacoco/index.html
```

### API Tests
```bash
# Using curl
curl -X POST http://localhost:8080/api/v1/wallets \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"userId": "test123", "currency": "BRL"}'

# Using Swagger UI
open http://localhost:8080/swagger-ui.html
```

## 📊 Monitoring

### Health Checks
- **Application**: `http://localhost:8080/actuator/health`
- **Database**: `http://localhost:8080/actuator/health/db`
- **Redis**: `http://localhost:8080/actuator/health/redis`

### Metrics
- **Prometheus**: `http://localhost:9090`
- **Grafana**: `http://localhost:3000` (admin/admin)

### Key Metrics
- `wallet.created` - Number of wallets created
- `wallet.deposit` - Number of deposits
- `wallet.withdraw` - Number of withdrawals
- `wallet.transfer` - Number of transfers
- `transaction.created` - Number of transactions
- `transaction.failed` - Number of failed transactions

## 🔧 Configuration

### Application Properties

```yaml
# Server
server:
  port: 8080

# Database
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/wallet_db
    username: ${POSTGRES_USER}
    password: ${POSTGRES_PASSWORD}
  
  # Redis Cache
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD:}
  
  # Cache Configuration
  cache:
    type: redis
    redis:
      time-to-live: 1800000 # 30 minutes

# Security
jwt:
  secret: ${JWT_SECRET}
  expiration: ${JWT_EXPIRATION:86400000}

# Rate Limiting
rate-limit:
  ip-requests-per-minute: ${RATE_LIMIT_IP:100}
  user-requests-per-minute: ${RATE_LIMIT_USER:1000}

# Resilience4j
resilience4j:
  circuitbreaker:
    instances:
      wallet-service:
        failure-rate-threshold: 50
        wait-duration-in-open-state: 60s
      database:
        failure-rate-threshold: 30
        wait-duration-in-open-state: 30s
  retry:
    instances:
      wallet-service:
        max-attempts: 3
        wait-duration: 100ms
      database:
        max-attempts: 3
        wait-duration: 200ms

# Monitoring
management:
  endpoints:
    web:
      exposure:
        include: health,info,prometheus,metrics
  endpoint:
    health:
      show-details: always
  metrics:
    export:
      prometheus:
        enabled: true

# API Documentation
springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
```

## 🏗️ Project Structure

```
src/
├── main/
│   ├── java/com/wallet/
│   │   ├── WalletServiceApplication.java
│   │   ├── domain/                    # Domain Layer
│   │   │   ├── entities/             # Business entities
│   │   │   ├── aggregates/           # Aggregates
│   │   │   ├── valueobjects/         # Value objects
│   │   │   ├── events/               # Domain events
│   │   │   └── enums/                # Enumerations
│   │   ├── application/              # Application Layer
│   │   │   ├── usecases/            # Use cases
│   │   │   ├── dto/                 # Data transfer objects
│   │   │   ├── events/              # Application events
│   │   │   └── ports/               # Ports (interfaces)
│   │   ├── infrastructure/          # Infrastructure Layer
│   │   │   ├── controllers/         # REST controllers
│   │   │   ├── security/            # Security configuration
│   │   │   ├── cache/               # Cache configuration
│   │   │   ├── resilience/          # Circuit breaker config
│   │   │   ├── metrics/             # Metrics configuration
│   │   │   └── config/              # Other configurations
│   │   └── adapters/                # Adapters Layer
│   │       ├── rest/                # REST adapters
│   │       └── infrastructure/      # Infrastructure adapters
│   └── resources/
│       ├── application.yml          # Application configuration
│       ├── db/migration/            # Database migrations
│       └── monitoring/              # Monitoring configs
└── test/                            # Test code
    ├── java/
    └── resources/
```

## 🔒 Security

### Authentication
- **JWT Tokens**: Stateless authentication
- **API Keys**: Service-to-service authentication
- **Rate Limiting**: Protection against abuse

### Authorization
- **Role-based**: USER, SERVICE roles
- **Resource-based**: Users can only access their own wallets

### Data Protection
- **Encryption**: Sensitive data encrypted at rest
- **Audit Trail**: Complete transaction history
- **Input Validation**: Comprehensive validation

## 📈 Performance

### Caching Strategy
- **Redis**: Distributed caching
- **TTL**: 15 minutes for wallets, 10 minutes for transactions
- **Cache Keys**: User-specific to ensure isolation

### Database Optimization
- **Indexes**: Optimized for common queries
- **Connection Pooling**: HikariCP configuration
- **Query Optimization**: Efficient SQL queries

### Resilience Patterns
- **Circuit Breaker**: Prevents cascade failures
- **Retry**: Automatic retry for transient failures
- **Timeout**: Configurable timeouts

## 🚀 Deployment

### Docker

```bash
# Build image
docker build -t wallet-service .

# Run container
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=docker \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/wallet_db \
  wallet-service
```

### Kubernetes

```bash
# Apply configurations
kubectl apply -f k8s/

# Check status
kubectl get pods -l app=wallet-service
```

### Production Checklist

- [ ] Change default passwords
- [ ] Configure SSL/TLS
- [ ] Set up monitoring alerts
- [ ] Configure backup strategy
- [ ] Set up CI/CD pipeline
- [ ] Configure logging aggregation
- [ ] Set up disaster recovery

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines

- Follow DDD principles
- Write comprehensive tests
- Use meaningful commit messages
- Follow code style guidelines
- Update documentation

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

- **Documentation**: Check this README and API docs
- **Issues**: Create an issue on GitHub
- **Discussions**: Use GitHub Discussions
- **Email**: support@walletservice.com

## 🗺️ Roadmap

- [ ] Multi-currency support
- [ ] International transfers
- [ ] Fraud detection
- [ ] Mobile SDK
- [ ] Webhook notifications
- [ ] Advanced analytics
- [ ] Blockchain integration

---

**Built with ❤️ using Spring Boot and DDD principles** 