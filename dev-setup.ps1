# Script de configuração do ambiente de desenvolvimento para Wallet Service
Write-Host "Configurando ambiente de desenvolvimento para Wallet Service..." -ForegroundColor Green

# Verificar se Docker está rodando
try {
    docker info | Out-Null
    Write-Host "Docker esta rodando" -ForegroundColor Green
} catch {
    Write-Host "Docker nao esta rodando. Por favor, inicie o Docker primeiro." -ForegroundColor Red
    exit 1
}

# Criar rede se não existir
Write-Host "Criando rede Docker..." -ForegroundColor Yellow
docker network create wallet-network 2>$null
if ($LASTEXITCODE -eq 0) {
    Write-Host "Rede criada" -ForegroundColor Green
} else {
    Write-Host "Rede ja existe" -ForegroundColor Blue
}

# Subir todos os serviços
Write-Host "Iniciando todos os servicos..." -ForegroundColor Yellow
docker-compose up -d

# Aguardar serviços ficarem prontos
Write-Host "Aguardando servicos ficarem prontos..." -ForegroundColor Yellow
Start-Sleep -Seconds 60

# Verificar status dos serviços
Write-Host "Verificando status dos servicos..." -ForegroundColor Yellow
docker-compose ps

Write-Host "Ambiente de desenvolvimento configurado!" -ForegroundColor Green
Write-Host ""
Write-Host "URLs dos servicos:" -ForegroundColor Cyan
Write-Host "   - Wallet Service: http://localhost:8080" -ForegroundColor White
Write-Host "   - PostgreSQL: localhost:5432" -ForegroundColor White
Write-Host "   - Redis: localhost:6379" -ForegroundColor White
Write-Host "   - Prometheus: http://localhost:9090" -ForegroundColor White
Write-Host "   - Grafana: http://localhost:3000 (admin/admin)" -ForegroundColor White
Write-Host ""
Write-Host "Para ver os logs do wallet-service:" -ForegroundColor Cyan
Write-Host "   - Execute: docker-compose logs -f wallet-service" -ForegroundColor White
Write-Host ""
Write-Host "Se houver problemas de compilacao, execute apenas a infraestrutura:" -ForegroundColor Cyan
Write-Host "   - Execute: docker-compose up -d postgres redis prometheus grafana" -ForegroundColor White
Write-Host "   - Depois execute: ./mvnw spring-boot:run" -ForegroundColor White 