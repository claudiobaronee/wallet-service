# Wallet Service

Um microserviço para gerenciamento de carteiras digitais, permitindo operações de depósito, saque e transferência entre usuários.

## 🚀 Funcionalidades

### Funcionais
- ✅ **Criar Carteira**: Criação de carteiras para usuários
- ✅ **Consultar Saldo**: Recuperação do saldo atual da carteira
- ✅ **Histórico de Saldo**: Consulta do saldo em pontos específicos do passado
- ✅ **Depositar Fundos**: Depósito de dinheiro nas carteiras
- ✅ **Sacar Fundos**: Saque de dinheiro das carteiras
- ✅ **Transferir Fundos**: Transferência entre carteiras de usuários

### Não-Funcionais
- ✅ **Rastreabilidade Completa**: Todos os movimentos são registrados para auditoria
- ✅ **Alta Disponibilidade**: Configuração para ambiente de produção
- ✅ **Monitoramento**: Health checks e métricas
- ✅ **Resiliência**: Circuit breakers e retry policies

## 🛠️ Tecnologias

- **Java 17** - Linguagem principal
- **Spring Boot 3.2.0** - Framework web
- **Spring Data JDBC** - Persistência de dados
- **PostgreSQL** - Banco de dados
- **Docker & Docker Compose** - Containerização
- **Flyway** - Migração de banco de dados
- **Resilience4j** - Padrões de resiliência
- **Prometheus & Grafana** - Monitoramento
- **OpenAPI/Swagger** - Documentação da API

## 📋 Pré-requisitos

- Docker e Docker Compose instalados
- Mínimo 4GB de RAM disponível
- Porta 8080 disponível

## 🚀 Instalação e Execução

### 1. Clone o repositório
```bash
git clone <repository-url>
cd wallet-service
```

### 2. Configure as variáveis de ambiente
```bash
cp env.example .env
# Edite o arquivo .env se necessário
```

### 3. Execute o projeto
```bash
docker-compose up -d
```

### 4. Verifique se está funcionando
```bash
curl http://localhost:8080/api/wallets/health
```

## 📚 API Endpoints

### Base URL: `http://localhost:8080/api/wallets`

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/health` | Health check do serviço |
| `POST` | `/` | Criar nova carteira |
| `GET` | `/{userId}` | Consultar carteira |
| `POST` | `/{userId}/deposit` | Realizar depósito |
| `POST` | `/{userId}/withdraw` | Realizar saque |
| `POST` | `/{userId}/transfer` | Transferir entre carteiras |
| `GET` | `/{userId}/balance-history` | Histórico de saldo |

### Exemplos de Uso

#### 1. Criar Carteira
```bash
curl -X POST http://localhost:8080/api/wallets \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "currency": "BRL"
  }'
```

#### 2. Consultar Carteira
```bash
curl http://localhost:8080/api/wallets/user123
```

#### 3. Realizar Depósito
```bash
curl -X POST http://localhost:8080/api/wallets/user123/deposit \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 100.50,
    "currency": "BRL"
  }'
```

#### 4. Realizar Saque
```bash
curl -X POST http://localhost:8080/api/wallets/user123/withdraw \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 50.25,
    "currency": "BRL"
  }'
```

#### 5. Transferir Entre Carteiras
```bash
curl -X POST http://localhost:8080/api/wallets/user123/transfer \
  -H "Content-Type: application/json" \
  -d '{
    "targetUserId": "user456",
    "amount": 25.00,
    "currency": "BRL"
  }'
```

#### 6. Consultar Histórico de Saldo
```bash
curl http://localhost:8080/api/wallets/user123/balance-history
```

## 🧪 Testes

### Executar Testes
```bash
docker-compose exec wallet-service mvn test
```

### Cobertura de Testes
```bash
docker-compose exec wallet-service mvn jacoco:report
```

## 📊 Monitoramento

### Prometheus
- URL: http://localhost:9090
- Métricas disponíveis: transações, saldos, erros, latência

### Grafana
- URL: http://localhost:3000
- Dashboards pré-configurados para monitoramento

### Health Check
```bash
curl http://localhost:8080/api/wallets/health
```

## 🏗️ Arquitetura

### Padrão Hexagonal (Ports & Adapters)
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

### Componentes Principais

1. **Domain Layer**: Entidades de negócio (Wallet, Transaction, BalanceHistory)
2. **Application Layer**: Casos de uso e regras de negócio
3. **Infrastructure Layer**: Controllers, Repositories, Database

## 🔒 Segurança e Rastreabilidade

### Rastreabilidade
- Cada operação gera um `transactionId` único
- Histórico completo de saldos registrado
- Timestamps em todas as operações
- Logs estruturados para auditoria

### Validações
- Verificação de saldo suficiente
- Validação de moeda
- Verificação de status da carteira
- Prevenção de transferências para mesma carteira

## 📈 Escalabilidade

### Estratégias Implementadas
- **Cache Redis**: Para consultas frequentes
- **Connection Pooling**: HikariCP para PostgreSQL
- **Circuit Breakers**: Resilience4j para resiliência
- **Métricas**: Prometheus para monitoramento

### Possíveis Melhorias
- **Event Sourcing**: Para auditoria completa
- **CQRS**: Separação de leitura/escrita
- **Saga Pattern**: Para transações distribuídas
- **API Gateway**: Para rate limiting e autenticação

## 🕐 Decisões de Design

### 1. Arquitetura Hexagonal
**Decisão**: Usar Ports & Adapters para desacoplamento
**Justificativa**: Facilita testes, manutenção e evolução do sistema

### 2. Spring Data JDBC vs JPA
**Decisão**: Usar Spring Data JDBC
**Justificativa**: Mais simples, melhor performance, controle total sobre SQL

### 3. PostgreSQL
**Decisão**: Banco relacional PostgreSQL
**Justificativa**: ACID, confiabilidade, suporte a JSON, open-source

### 4. Value Objects
**Decisão**: Classe `Money` como value object
**Justificativa**: Encapsula lógica monetária, imutabilidade, type safety

### 5. Histórico de Saldo
**Decisão**: Tabela separada para histórico
**Justificativa**: Rastreabilidade completa, auditoria, performance

## ⚖️ Trade-offs e Compromissos

### 1. Simplicidade vs Complexidade
**Compromisso**: Arquitetura simples mas escalável
**Justificativa**: Projeto de 6-8 horas, mas preparado para produção

### 2. Performance vs Rastreabilidade
**Compromisso**: Histórico completo impacta performance
**Justificativa**: Requisito crítico para auditoria financeira

### 3. Flexibilidade vs Segurança
**Compromisso**: Validações rigorosas
**Justificativa**: Sistema financeiro requer segurança máxima

### 4. Tecnologia vs Prazo
**Compromisso**: Stack moderna mas conhecida
**Justificativa**: Spring Boot é maduro e produtivo

## 🚨 Assunções

### 1. Autenticação/Autorização
**Assunção**: Não implementada (fora do escopo)
**Justificativa**: Foco nas funcionalidades core do wallet

### 2. Moeda Única por Carteira
**Assunção**: Uma carteira = uma moeda
**Justificativa**: Simplifica o modelo e evita complexidade de câmbio

### 3. Transações Síncronas
**Assunção**: Todas as operações são síncronas
**Justificativa**: Garante consistência imediata

### 4. Usuário Único por Carteira
**Assunção**: Relação 1:1 entre usuário e carteira
**Justificativa**: Modelo simplificado para o escopo

## 📝 Logs e Debugging

### Logs Estruturados
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
# Ver logs do serviço
docker-compose logs -f wallet-service

# Acessar banco de dados
docker-compose exec postgres psql -U wallet -d wallet_db
```

## 🔧 Configuração

### Variáveis de Ambiente
```bash
# Database
DB_HOST=postgres
DB_PORT=5432
DB_NAME=wallet_db
DB_USER=wallet
DB_PASSWORD=wallet123

# Redis
REDIS_HOST=redis
REDIS_PORT=6379

# Application
SERVER_PORT=8080
LOG_LEVEL=INFO
```

## 📊 Métricas Disponíveis

- `wallet_operations_total`: Total de operações
- `wallet_balance_current`: Saldo atual por usuário
- `wallet_transactions_duration`: Duração das transações
- `wallet_errors_total`: Total de erros

## 🚀 Deploy em Produção

### 1. Build da Imagem
```bash
docker build -t wallet-service:latest .
```

### 2. Configuração de Produção
```bash
# Ajuste as variáveis de ambiente
# Configure volumes para persistência
# Configure networks para segurança
```

### 3. Monitoramento
```bash
# Configure alertas no Prometheus
# Configure dashboards no Grafana
# Configure logs centralizados
```

## 📞 Suporte

Para dúvidas ou problemas:
1. Verifique os logs: `docker-compose logs wallet-service`
2. Consulte a documentação da API: http://localhost:8080/swagger-ui.html
3. Verifique o health check: http://localhost:8080/api/wallets/health

## ⏱️ Estimativa de Tempo

**Tempo Investido**: ~6 horas
- **Setup inicial**: 30 min
- **Implementação core**: 3 horas
- **Testes e validação**: 1 hora
- **Documentação**: 1 hora
- **Refinamentos**: 30 min

---

**Desenvolvido com ❤️ seguindo as melhores práticas de desenvolvimento de software.** 