@echo off
echo 🚀 Iniciando Wallet Service...
echo ================================

REM Verificar se o Docker está rodando
docker info >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Docker não está rodando. Por favor, inicie o Docker Desktop.
    pause
    exit /b 1
)

echo ✅ Docker está rodando

REM Parar containers existentes (se houver)
echo 🛑 Parando containers existentes...
docker-compose down

REM Build e iniciar os serviços
echo 🔨 Fazendo build e iniciando serviços...
docker-compose up --build -d

REM Aguardar os serviços ficarem prontos
echo ⏳ Aguardando serviços ficarem prontos...
timeout /t 30 /nobreak >nul

REM Verificar status dos serviços
echo 📊 Status dos serviços:
docker-compose ps

REM Verificar health check
echo 🏥 Verificando health check...
curl -f http://localhost:8080/actuator/health >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ Health check OK
) else (
    echo ❌ Health check falhou
)

echo.
echo 🎉 Wallet Service iniciado com sucesso!
echo.
echo 📋 Endpoints disponíveis:
echo    API: http://localhost:8080
echo    Health: http://localhost:8080/actuator/health
echo    Prometheus: http://localhost:8080/actuator/prometheus
echo    Prometheus UI: http://localhost:9090
echo.
echo 📖 Exemplos de uso:
echo    curl -X POST http://localhost:8080/api/v1/wallets -H "Content-Type: application/json" -d "{\"userId\": \"user123\", \"currency\": \"BRL\"}"
echo.
echo 📚 Documentação:
echo    README.md - Instruções completas
echo    docs/ - Collection do Postman e diagramas
echo.
echo 🛑 Para parar: docker-compose down
pause 