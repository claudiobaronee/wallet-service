#!/bin/bash

# Script para testar a segurança do Wallet Service
# Uso: ./test-security.sh

BASE_URL="http://localhost:8080"
API_BASE="$BASE_URL/api"

echo "🔒 Testando Segurança do Wallet Service"
echo "========================================"
echo ""

# Função para fazer requisições e mostrar resultados
test_request() {
    local method=$1
    local url=$2
    local data=$3
    local auth_header=$4
    local description=$5
    
    echo "📡 $description"
    echo "   $method $url"
    
    if [ -n "$data" ]; then
        echo "   Data: $data"
    fi
    
    if [ -n "$auth_header" ]; then
        echo "   Auth: $auth_header"
    fi
    
    if [ "$method" = "GET" ]; then
        if [ -n "$auth_header" ]; then
            response=$(curl -s -w "\n%{http_code}" -H "$auth_header" "$url")
        else
            response=$(curl -s -w "\n%{http_code}" "$url")
        fi
    else
        if [ -n "$auth_header" ]; then
            response=$(curl -s -w "\n%{http_code}" -X "$method" -H "Content-Type: application/json" -H "$auth_header" -d "$data" "$url")
        else
            response=$(curl -s -w "\n%{http_code}" -X "$method" -H "Content-Type: application/json" -d "$data" "$url")
        fi
    fi
    
    # Separar corpo da resposta do código de status
    body=$(echo "$response" | head -n -1)
    status=$(echo "$response" | tail -n 1)
    
    echo "   Status: $status"
    echo "   Response: $body"
    echo ""
}

# Teste 1: Health Check (público)
test_request "GET" "$API_BASE/wallets/health" "" "" "Health Check (Público)"

# Teste 2: Login
test_request "POST" "$API_BASE/auth/login" '{"username":"user1","password":"user123"}' "" "Login"

# Teste 3: Tentar acessar endpoint protegido sem token
test_request "GET" "$API_BASE/wallets/user1" "" "" "Tentar acessar carteira sem token (Deve falhar)"

# Teste 4: Login com credenciais inválidas
test_request "POST" "$API_BASE/auth/login" '{"username":"invalid","password":"wrong"}' "" "Login com credenciais inválidas"

# Teste 5: Login com admin
test_request "POST" "$API_BASE/auth/login" '{"username":"admin","password":"admin123"}' "" "Login como admin"

echo "✅ Testes de segurança concluídos!"
echo ""
echo "📊 Para testar com token JWT:"
echo "1. Execute o login para obter um token"
echo "2. Use o token no header: Authorization: Bearer <token>"
echo "3. Teste os endpoints protegidos"
echo ""
echo "📖 Para instruções detalhadas, consulte:"
echo "   SECURITY_GUIDE.md" 