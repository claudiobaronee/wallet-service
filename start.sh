#!/bin/bash

echo "🚀 Iniciando Wallet Service..."
echo "================================"

# Verificar se o Docker está rodando
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker não está rodando. Por favor, inicie o Docker Desktop."
    exit 1
fi

echo "✅ Docker está rodando"

# Parar containers existentes (se houver)
echo "🛑 Parando containers existentes..."
docker-compose down

# Remover volumes antigos (opcional - descomente se quiser limpar dados)
# echo "🧹 Removendo volumes antigos..."
# docker-compose down -v

# Build e iniciar os serviços
echo "🔨 Fazendo build e iniciando serviços..."
docker-compose up --build -d

# Aguardar os serviços ficarem prontos
echo "⏳ Aguardando serviços ficarem prontos..."
sleep 30

# Verificar status dos serviços
echo "📊 Status dos serviços:"
docker-compose ps

# Verificar health check
echo "🏥 Verificando health check..."
curl -f http://localhost:8080/actuator/health || echo "❌ Health check falhou"

echo ""
echo "🎉 Wallet Service iniciado com sucesso!"
echo ""
echo "📋 Endpoints disponíveis:"
echo "   API: http://localhost:8080"
echo "   Health: http://localhost:8080/actuator/health"
echo "   Prometheus: http://localhost:8080/actuator/prometheus"
echo "   Prometheus UI: http://localhost:9090"
echo ""
echo "📖 Exemplos de uso:"
echo "   curl -X POST http://localhost:8080/api/v1/wallets \\"
echo "     -H 'Content-Type: application/json' \\"
echo "     -d '{\"userId\": \"user123\", \"currency\": \"BRL\"}'"
echo ""
echo "📚 Documentação:"
echo "   README.md - Instruções completas"
echo "   docs/ - Collection do Postman e diagramas"
echo ""
echo "🛑 Para parar: docker-compose down" 