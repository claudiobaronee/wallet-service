# 🐳 Docker Compose - Wallet Service

Este documento descreve como usar o Docker Compose para executar o Wallet Service e todos os seus componentes.

## 🚀 Comandos Principais

### Iniciar todos os serviços
```bash
docker-compose up -d
```

### Ver logs em tempo real
```bash
docker-compose logs -f
```

### Ver logs de um serviço específico
```bash
docker-compose logs wallet-service
docker-compose logs postgres
docker-compose logs redis
```

### Parar todos os serviços
```bash
docker-compose down
```

### Parar e remover volumes (limpa dados)
```bash
docker-compose down -v
```

### Rebuild e reiniciar
```bash
docker-compose up --build -d
```

### Verificar status dos serviços
```bash
docker-compose ps
```

## 📊 Serviços Disponíveis

### 1. Wallet Service (Porta 8080)
- **URL**: http://localhost:8080
- **Health Check**: http://localhost:8080/actuator/health
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs

### 2. PostgreSQL (Porta 5432)
- **Database**: wallet_db
- **User**: wallet_user
- **Password**: wallet_password
- **Health Check**: Automático via Docker

### 3. Redis (Porta 6379)
- **Cache**: Ativo
- **Health Check**: Automático via Docker
- **CLI Access**: `docker exec -it wallet-redis redis-cli`

### 4. Prometheus (Porta 9090)
- **URL**: http://localhost:9090
- **Metrics**: http://localhost:8080/actuator/prometheus
- **Health Check**: Automático via Docker

### 5. Grafana (Porta 3000)
- **URL**: http://localhost:3000
- **User**: admin
- **Password**: admin
- **Health Check**: Automático via Docker

## 🔧 Configuração

### Variáveis de Ambiente
As variáveis de ambiente estão definidas no `docker-compose.yml` e podem ser sobrescritas criando um arquivo `.env`:

```bash
# Copiar exemplo
cp env.example .env

# Editar configurações
nano .env
```

### Volumes
- **PostgreSQL**: `postgres_data` - Dados do banco
- **Redis**: `redis_data` - Dados do cache
- **Prometheus**: `prometheus_data` - Métricas
- **Grafana**: `grafana_data` - Dashboards e configurações

### Networks
Todos os serviços usam a rede `wallet-network` para comunicação interna.

## 🏥 Health Checks

Cada serviço possui health checks configurados:

- **PostgreSQL**: Verifica conexão e executa query
- **Redis**: Verifica resposta PING
- **Wallet Service**: Verifica endpoint /actuator/health
- **Prometheus**: Verifica endpoint /-/healthy
- **Grafana**: Verifica endpoint /api/health

## 🐛 Troubleshooting

### Problemas de Conexão
```bash
# Verificar se todos os containers estão rodando
docker-compose ps

# Verificar logs de erro
docker-compose logs --tail=50 wallet-service

# Verificar conectividade entre containers
docker exec wallet-service ping postgres
docker exec wallet-service ping redis
```

### Problemas de Banco de Dados
```bash
# Conectar ao PostgreSQL
docker exec -it wallet-postgres psql -U wallet_user -d wallet_db

# Verificar migrações
docker-compose logs postgres | grep "Flyway"
```

### Problemas de Cache
```bash
# Conectar ao Redis
docker exec -it wallet-redis redis-cli

# Testar Redis
docker exec wallet-redis redis-cli ping
```

### Reiniciar Serviços
```bash
# Reiniciar apenas o wallet-service
docker-compose restart wallet-service

# Reiniciar banco de dados
docker-compose restart postgres

# Reiniciar cache
docker-compose restart redis
```

## 📈 Monitoramento

### Métricas Disponíveis
- **JVM Metrics**: Memória, CPU, threads
- **HTTP Metrics**: Requisições, latência, erros
- **Database Metrics**: Conexões, queries
- **Cache Metrics**: Hit/miss ratio
- **Business Metrics**: Transações, saldos

### Dashboards Grafana
- **Wallet Overview**: Visão geral do sistema
- **Performance**: Métricas de performance
- **Errors**: Monitoramento de erros
- **Database**: Métricas do PostgreSQL

## 🔒 Segurança

### Usuários Não-Root
Todos os containers rodam com usuários não-root para segurança.

### Redes Isoladas
Serviços se comunicam apenas através da rede interna `wallet-network`.

### Volumes Seguros
Dados sensíveis são armazenados em volumes Docker seguros.

## 📝 Logs

### Estrutura de Logs
```bash
# Logs da aplicação
docker-compose logs wallet-service

# Logs do banco
docker-compose logs postgres

# Logs do cache
docker-compose logs redis

# Logs de monitoramento
docker-compose logs prometheus
docker-compose logs grafana
```

### Níveis de Log
- **ERROR**: Erros críticos
- **WARN**: Avisos importantes
- **INFO**: Informações gerais
- **DEBUG**: Informações detalhadas (desenvolvimento)

## 🚀 Produção

### Configurações Recomendadas
1. Usar secrets para senhas
2. Configurar backup automático
3. Monitorar recursos
4. Configurar alertas
5. Usar HTTPS em produção

### Escalabilidade
- **Horizontal**: Múltiplas instâncias do wallet-service
- **Vertical**: Aumentar recursos dos containers
- **Cache**: Redis cluster para alta disponibilidade
- **Database**: PostgreSQL cluster/replicação 