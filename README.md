# Wallet Service - Digital Wallet Microservice

A robust microservice for managing digital wallets, built with hexagonal architecture, Spring Boot, and focused on high reliability, traceability, and auditability.

## 🚀 Features

- ✅ **Wallet creation** - Create digital wallets for users
- ✅ **Balance queries** - Query the current wallet balance
- ✅ **Deposits** - Make deposits into the wallet
- ✅ **Withdrawals** - Withdraw from the wallet (with balance validation)
- ✅ **Transfers** - Transfer between user wallets
- ✅ **Balance history** - Query the complete transaction history
- ✅ **Health check** - Service status check
- ✅ **Robust validations** - Complete input data validation
- ✅ **Atomic transactions** - Data consistency guarantee
- ✅ **Traceability** - Detailed logs and unique transaction IDs

## 🏗️ Architecture

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

### Technologies Used

- **Java 17** - Main language
- **Spring Boot 3.x** - Main framework
- **PostgreSQL** - Main database
- **Redis** - Cache and sessions
- **Docker & Docker Compose** - Containerization
- **Flyway** - Database migration
- **Resilience4j** - Resilience patterns
- **Prometheus & Grafana** - Monitoring and observability
- **Maven** - Dependency management

## 🛠️ Setup and Execution

### Prerequisites

- Java 17+
- Docker and Docker Compose

### 1. Clone the repository
```bash
git clone <repository-url>
cd wallet-service
```

### 2. Configure environment variables
```bash
cp env.example .env
# Edit the .env file with your settings
```

### 3. Run with Docker Compose

#### Option 1: Automatic Script (Recommended)
```bash
# Windows (PowerShell)
./dev-setup.ps1

# Linux/Mac
chmod +x dev-setup.sh
./dev-setup.sh
```

#### Option 2: Manual Command
```bash
# Stop existing containers
docker-compose down

# Build and start
docker-compose up --build -d

# Check logs
docker-compose logs -f wallet-service
```

### 4. Check if services are running
```bash
docker-compose ps
```

### 5. Access after startup
- **Wallet Service**: http://localhost:8080
- **Grafana**: http://localhost:3000 (admin/admin)
- **Prometheus**: http://localhost:9090

## 🔧 Unified Configuration

The project now uses a **unified configuration** that works for both local development and Docker:

### Configuration Files
- **`application.yml`** - Single configuration with environment variables
- **`env.example`** - Example environment variables
- **`docker-compose.yml`** - Docker services configuration

### Main Environment Variables
```bash
# Database
SPRING_DATASOURCE_HOST=localhost  # postgres (Docker)
SPRING_DATASOURCE_PORT=5432
SPRING_DATASOURCE_DB=wallet_db

# Redis
REDIS_HOST=localhost  # redis (Docker)
REDIS_PORT=6379

# JWT
JWT_SECRET=your-secret-key
JWT_EXPIRATION=86400000

# Server
SERVER_PORT=8080
```

### Execution Modes

#### Local Development
1. Configure the `.env` file with `localhost` for hosts
2. Run: `./mvnw spring-boot:run`

#### Full Docker
1. Run: `docker-compose up -d`
2. All services run in containers

#### Docker Infrastructure + Local App
1. Run: `./dev-setup.ps1` (Windows) or `./dev-setup.sh` (Linux/Mac)
2. Configure `.env` with `localhost` for hosts
3. Run: `./mvnw spring-boot:run`

## 📡 API Endpoints

### Base URL
```
http://localhost:8080/api/wallets
```

### Available Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET`  | `/health` | Service health check |
| `POST` | `/` | Create new wallet |
| `GET`  | `/{userId}` | Get wallet |
| `POST` | `/{userId}/deposit` | Make deposit |
| `POST` | `/{userId}/withdraw` | Make withdrawal |
| `POST` | `/{userId}/transfer` | Transfer between wallets |
| `GET`  | `/{userId}/balance-history` | Get balance history |

## 🧪 Testing the API

### Using the Postman Collection

1. **Import the collection**: Open Postman and import the `Wallet_Service_API.postman_collection.json` file
2. **Configure variables**:
   - `base_url`: `http://localhost:8080`
   - `userId`: `user123` (or any user ID)
3. **Run the tests**:
   - **Health Check**: Check if the service is running
   - **Basic Scenarios**: Create, get, deposit, withdraw, and transfer
   - **Validation Tests**: Error and validation scenarios
   - **Complete Scenarios**: End-to-end flows

### Usage Examples

#### 1. Create a wallet
```bash
curl -X POST http://localhost:8080/api/wallets \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "currency": "BRL"
  }'
```

#### 2. Make a deposit
```bash
curl -X POST http://localhost:8080/api/wallets/user123/deposit \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 100.50,
    "currency": "BRL",
    "description": "Initial deposit"
  }'
```

#### 3. Get balance
```bash
curl -X GET http://localhost:8080/api/wallets/user123
```

## 📊 Monitoring

### Prometheus
- URL: http://localhost:9090
- Available metrics:
  - Request rate
  - Response time
  - Error rate
  - Resource usage

### Grafana
- URL: http://localhost:3000
- User: `admin`
- Password: `admin`
- Pre-configured dashboards for monitoring

## 🔒 Security

### JWT Authentication
- ✅ **JWT Tokens** - Token-based authentication
- ✅ **Refresh Tokens** - Automatic token renewal
- ✅ **Token Validation** - Integrity and expiration check
- ✅ **Stateless Sessions** - No session storage on the server

### Authorization
- ✅ **Access Control** - Endpoints protected by authentication
- ✅ **Roles and Permissions** - Role system (ADMIN, USER)
- ✅ **Method Security** - `@PreAuthorize` annotations available

### Validation and Sanitization
- ✅ **Input Validation** - Robust data validation
- ✅ **Sanitization** - Injection attack prevention
- ✅ **Rate Limiting** - Protection against brute force attacks

### Security Settings
- ✅ **CORS Configured** - Allowed origins control
- ✅ **CSRF Disabled** - For REST APIs (stateless)
- ✅ **Security Headers** - Secure HTTP headers

### Test Users
| Username | Password | Role | Description |
|----------|----------|------|-------------|
| `admin`  | `admin123` | ADMIN | System administrator |
| `user1`  | `user123`  | USER  | Regular user 1       |
| `user2`  | `user456`  | USER  | Regular user 2       |

**📖 For detailed security instructions, see: `SECURITY_GUIDE.md`**

## 📝 Logs

Logs are structured and include:
- Unique transaction IDs
- Precise timestamps
- Full operation context
- Appropriate log levels (INFO, WARN, ERROR)

## 🧪 Tests

### Run unit tests
```bash
mvn test
```

### Run integration tests
```bash
mvn verify
```

## 📈 Project Status

### ✅ Implemented (95%)
- ✅ Complete hexagonal architecture
- ✅ All main features
- ✅ Robust validations
- ✅ Atomic transactions
- ✅ Structured logs
- ✅ Monitoring with Prometheus/Grafana
- ✅ Docker containerization
- ✅ Complete Postman collection
- ✅ Updated documentation
- ✅ **Full JWT security**
- ✅ **Authentication and authorization**
- ✅ **Role and permission system**

### 🔄 In Development (5%)
- 🔄 Advanced integration tests
- 🔄 Performance optimizations
- 🔄 Additional security improvements (OAuth2, 2FA)

## 🤝 Contribution

1. Fork the project
2. Create a branch for your feature (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is under the MIT license. See the `LICENSE` file for more details.

## 🆘 Support

For support and questions:
- Open an issue on GitHub
- Check the API documentation
- Use the Postman collection for testing

---