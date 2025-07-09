# Script de teste rápido da API Wallet Service
Write-Host "Testando Wallet Service API..." -ForegroundColor Green

$baseUrl = "http://localhost:8080"

# Teste 1: Health Check
Write-Host "`n1. Testando Health Check..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/wallets/health" -Method GET
    Write-Host "✅ Health Check OK: $($response.status)" -ForegroundColor Green
} catch {
    Write-Host "❌ Health Check falhou: $($_.Exception.Message)" -ForegroundColor Red
}

# Teste 2: Criar Carteira
Write-Host "`n2. Testando Criação de Carteira..." -ForegroundColor Yellow
$createWalletBody = @{
    userId = "user123"
    currency = "BRL"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/wallets" -Method POST -Body $createWalletBody -ContentType "application/json"
    Write-Host "✅ Carteira criada: ID $($response.id), Saldo: $($response.balance) $($response.currency)" -ForegroundColor Green
} catch {
    Write-Host "❌ Criação de carteira falhou: $($_.Exception.Message)" -ForegroundColor Red
}

# Teste 3: Consultar Carteira
Write-Host "`n3. Testando Consulta de Carteira..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/wallets/user123" -Method GET
    Write-Host "✅ Carteira consultada: Saldo atual: $($response.balance) $($response.currency)" -ForegroundColor Green
} catch {
    Write-Host "❌ Consulta de carteira falhou: $($_.Exception.Message)" -ForegroundColor Red
}

# Teste 4: Realizar Depósito
Write-Host "`n4. Testando Depósito..." -ForegroundColor Yellow
$depositBody = @{
    amount = 100.50
    currency = "BRL"
    description = "Depósito inicial"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/wallets/user123/deposit" -Method POST -Body $depositBody -ContentType "application/json"
    Write-Host "✅ Depósito realizado: $($response.amount) $($response.currency), Novo saldo: $($response.newBalance)" -ForegroundColor Green
} catch {
    Write-Host "❌ Depósito falhou: $($_.Exception.Message)" -ForegroundColor Red
}

# Teste 5: Realizar Saque
Write-Host "`n5. Testando Saque..." -ForegroundColor Yellow
$withdrawBody = @{
    amount = 25.00
    currency = "BRL"
    description = "Saque teste"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/wallets/user123/withdraw" -Method POST -Body $withdrawBody -ContentType "application/json"
    Write-Host "✅ Saque realizado: $($response.amount) $($response.currency), Novo saldo: $($response.newBalance)" -ForegroundColor Green
} catch {
    Write-Host "❌ Saque falhou: $($_.Exception.Message)" -ForegroundColor Red
}

# Teste 6: Consultar Histórico
Write-Host "`n6. Testando Histórico de Saldo..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/wallets/user123/balance-history" -Method GET
    Write-Host "✅ Histórico consultado: $($response.history.Count) registros encontrados" -ForegroundColor Green
} catch {
    Write-Host "❌ Histórico falhou: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n🎉 Testes concluídos!" -ForegroundColor Green
Write-Host "Para mais testes, use a collection do Postman ou acesse:" -ForegroundColor Cyan
Write-Host "   - API: http://localhost:8080" -ForegroundColor White
Write-Host "   - Grafana: http://localhost:3000 (admin/admin)" -ForegroundColor White
Write-Host "   - Prometheus: http://localhost:9090" -ForegroundColor White 