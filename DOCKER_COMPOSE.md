# 🐳 Docker Compose Guide

This guide explains how to use Docker Compose to run the entire infrastructure and the Wallet Service microservice.

## 📋 Prerequisites

- Docker installed ([download](https://www.docker.com/get-started/))
- Docker Compose installed (comes with Docker Desktop)
- `.env` file configured (see `env.example`)

## 🚀 Quick Start

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

### 3. Start all services
```bash
docker-compose up --build -d
```

### 4. Check if services are running
```bash
docker-compose ps
```

### 5. View logs
```bash
docker-compose logs -f wallet-service
```

### 6. Stop all services
```bash
docker-compose down
```

## 🏗️ Services Overview

The following services are included in `docker-compose.yml`:

| Service         | Description                | Port(s)         |
|-----------------|---------------------------|-----------------|
| wallet-service  | Digital wallet API        | 8080            |
| postgres        | PostgreSQL database       | 5432            |
| redis           | Redis cache               | 6379            |
| prometheus      | Monitoring                | 9090            |
| grafana         | Dashboards/Observability  | 3000            |

## ⚙️ Environment Variables

All configuration is managed via the `.env` file. See `env.example` for all available variables.

## 🛠️ Useful Commands

- **Rebuild and restart everything:**
  ```bash
  docker-compose up --build -d
  ```
- **Stop all containers:**
  ```bash
  docker-compose down
  ```
- **View logs for a service:**
  ```bash
  docker-compose logs -f wallet-service
  ```
- **Access PostgreSQL CLI:**
  ```bash
  docker-compose exec postgres psql -U wallet_user -d wallet_db
  ```
- **Access Redis CLI:**
  ```bash
  docker-compose exec redis redis-cli
  ```

## 📡 Service URLs

- **Wallet Service API:** http://localhost:8080
- **Grafana:** http://localhost:3000 (admin/admin)
- **Prometheus:** http://localhost:9090

## 🧩 Tips

- If you change the `.env` file, restart the containers.
- For local development, you can run only the infrastructure with Docker and the app locally (see README).
- For troubleshooting, see `TROUBLESHOOTING.md`.

---

**Developed with ❤️ using Docker Compose** 