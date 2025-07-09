#!/bin/bash

echo "🚀 Configurando ambiente de desenvolvimento para Wallet Service..."

# Verificar se Docker está rodando
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker não está rodando. Por favor, inicie o Docker primeiro."
    exit 1
fi

# Criar rede se não existir
echo "📡 Criando rede Docker..."
docker network create wallet-network 2>/dev/null || echo "Rede já existe"

# Subir todos os serviços
echo "🐳 Iniciando todos os serviços..."
docker-compose up -d

# Aguardar serviços ficarem prontos
echo "⏳ Aguardando serviços ficarem prontos..."
sleep 60

# Verificar status dos serviços
echo "🔍 Verificando status dos serviços..."
docker-compose ps

echo "✅ Ambiente de desenvolvimento configurado!"
echo ""
echo "📋 URLs dos serviços:"
echo "   - Wallet Service: http://localhost:8080"
echo "   - PostgreSQL: localhost:5432"
echo "   - Redis: localhost:6379"
echo "   - Prometheus: http://localhost:9090"
echo "   - Grafana: http://localhost:3000 (admin/admin)"
echo ""
echo "🔧 Para ver os logs do wallet-service:"
echo "   - Execute: docker-compose logs -f wallet-service"
echo ""
echo "🐳 Para executar apenas a infraestrutura (sem wallet-service):"
echo "   - Execute: docker-compose up -d postgres redis prometheus grafana"
echo "   - Depois execute: ./mvnw spring-boot:run" 