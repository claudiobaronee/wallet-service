#!/bin/bash

echo "🚀 Iniciando Wallet Service no Docker..."
echo "========================================"

# Parar containers existentes
echo "📦 Parando containers existentes..."
docker-compose down

# Remover imagens antigas (opcional)
echo "🧹 Removendo imagens antigas..."
docker-compose down --rmi all

# Construir e iniciar
echo "🔨 Construindo e iniciando containers..."
docker-compose up --build -d

# Aguardar inicialização
echo "⏳ Aguardando inicialização dos serviços..."
sleep 30

# Verificar status dos containers
echo "📊 Status dos containers:"
docker-compose ps

# Verificar logs do wallet-service
echo "📋 Logs do Wallet Service:"
docker-compose logs wallet-service

# Testar health check
echo "🏥 Testando health check..."
curl -f http://localhost:8080/actuator/health || echo "❌ Health check falhou"

echo ""
echo "✅ Setup completo! Acesse:"
echo "   - Wallet Service: http://localhost:8080"
echo "   - Grafana: http://localhost:3000 (admin/admin)"
echo "   - Prometheus: http://localhost:9090"
echo ""
echo "🔐 Para testar a autenticação:"
echo "   curl -X POST http://localhost:8080/api/auth/login \\"
echo "     -H 'Content-Type: application/json' \\"
echo "     -d '{\"username\":\"admin\",\"password\":\"admin123\"}'" 