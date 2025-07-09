# 🧪 Guia de Testes com Postman

Este guia explica como usar a collection do Postman para testar completamente o microserviço de carteira digital.

## 📋 Pré-requisitos

1. **Postman instalado** - Baixe em [postman.com](https://www.postman.com/downloads/)
2. **Serviço rodando** - Execute `docker-compose up -d`
3. **Collection importada** - Importe o arquivo `Wallet_Service_API.postman_collection.json`

## 🚀 Configuração Inicial

### 1. Importar a Collection

1. Abra o Postman
2. Clique em **Import**
3. Selecione o arquivo `Wallet_Service_API.postman_collection.json`
4. A collection será importada com todas as requisições organizadas

### 2. Configurar Variáveis

1. Clique na collection **Wallet Service API**
2. Vá na aba **Variables**
3. Configure as variáveis:

| Variável | Valor | Descrição |
|----------|-------|-----------|
| `base_url` | `http://localhost:8080` | URL base da API |
| `userId` | `user123` | ID do usuário para testes |

### 3. Verificar Configuração

Execute o **Health Check** para verificar se tudo está funcionando:

```
GET {{base_url}}/api/wallets/health
```

Resposta esperada:
```json
{
  "status": "UP",
  "service": "wallet-service",
  "version": "1.0.0",
  "timestamp": 1704067200000
}
```

## 📚 Estrutura da Collection

### 🏥 Health Check
- **Health Check**: Verifica se o serviço está funcionando

### 🔧 Operações Básicas
- **Criar Carteira**: Cria uma nova carteira para um usuário
- **Consultar Carteira**: Consulta os detalhes de uma carteira
- **Realizar Depósito**: Realiza um depósito na carteira
- **Realizar Saque**: Realiza um saque da carteira
- **Transferir Entre Carteiras**: Transfere dinheiro entre carteiras
- **Consultar Histórico de Saldo**: Consulta o histórico de movimentações

### 🧪 Testes de Validação
- **userId inválido**: Testa validação de userId vazio
- **currency inválida**: Testa validação de moeda inválida
- **amount inválido**: Testa validação de valor negativo
- **saldo insuficiente**: Testa saque com saldo insuficiente
- **mesma carteira**: Testa transferência para a mesma carteira

### 🎯 Cenários de Teste
- **Fluxo Completo - Usuário 1**: Criação, depósito e consulta
- **Fluxo Completo - Usuário 2**: Criação e depósito
- **Transferência Entre Usuários**: Teste completo de transferência

## 🎮 Como Executar os Testes

### 1. Teste Básico - Criar e Consultar Carteira

1. **Execute "Criar Carteira"**:
   ```json
   {
     "userId": "user123",
     "currency": "BRL"
   }
   ```
   
   Resposta esperada (201):
   ```json
   {
     "id": 1,
     "userId": "user123",
     "balance": 0.00,
     "currency": "BRL",
     "status": "ACTIVE",
     "message": "Carteira criada com sucesso",
     "createdAt": "2024-01-01 10:00:00"
   }
   ```

2. **Execute "Consultar Carteira"**:
   - URL: `{{base_url}}/api/wallets/user123`
   
   Resposta esperada (200):
   ```json
   {
     "id": 1,
     "userId": "user123",
     "balance": 0.00,
     "currency": "BRL",
     "status": "ACTIVE",
     "createdAt": "2024-01-01 10:00:00",
     "updatedAt": "2024-01-01 10:00:00"
   }
   ```

### 2. Teste de Depósito

1. **Execute "Realizar Depósito"**:
   ```json
   {
     "amount": 100.50,
     "currency": "BRL",
     "description": "Depósito inicial"
   }
   ```
   
   Resposta esperada (200):
   ```json
   {
     "message": "Depósito realizado com sucesso",
     "amount": 100.50,
     "currency": "BRL",
     "newBalance": 100.50,
     "transactionId": "uuid-gerado"
   }
   ```

### 3. Teste de Saque

1. **Execute "Realizar Saque"**:
   ```json
   {
     "amount": 50.25,
     "currency": "BRL",
     "description": "Saque para pagamento"
   }
   ```
   
   Resposta esperada (200):
   ```json
   {
     "message": "Saque realizado com sucesso",
     "amount": 50.25,
     "currency": "BRL",
     "newBalance": 50.25,
     "transactionId": "uuid-gerado"
   }
   ```

### 4. Teste de Transferência

1. **Primeiro, crie uma segunda carteira**:
   ```json
   {
     "userId": "user456",
     "currency": "BRL"
   }
   ```

2. **Execute "Transferir Entre Carteiras"**:
   ```json
   {
     "targetUserId": "user456",
     "amount": 25.00,
     "currency": "BRL",
     "description": "Transferência para pagamento"
   }
   ```
   
   Resposta esperada (200):
   ```json
   {
     "message": "Transferência realizada com sucesso",
     "amount": 25.00,
     "currency": "BRL",
     "sourceUserId": "user123",
     "targetUserId": "user456",
     "sourceNewBalance": 25.25,
     "targetNewBalance": 25.00,
     "transactionId": "uuid-gerado"
   }
   ```

### 5. Teste de Histórico

1. **Execute "Consultar Histórico de Saldo"**:
   - URL: `{{base_url}}/api/wallets/user123/balance-history`
   
   Resposta esperada (200):
   ```json
   {
     "userId": "user123",
     "currentBalance": 25.25,
     "currency": "BRL",
     "history": [
       {
         "balance": 0.00,
         "currency": "BRL",
         "description": "Criação da carteira",
         "recordedAt": "2024-01-01 10:00:00"
       },
       {
         "balance": 100.50,
         "currency": "BRL",
         "description": "Depósito inicial",
         "recordedAt": "2024-01-01 10:01:00"
       },
       {
         "balance": 50.25,
         "currency": "BRL",
         "description": "Saque para pagamento",
         "recordedAt": "2024-01-01 10:02:00"
       },
       {
         "balance": 25.25,
         "currency": "BRL",
         "description": "Transferência enviada de 25.00 BRL para user456",
         "recordedAt": "2024-01-01 10:03:00"
       }
     ]
   }
   ```

## 🧪 Testes de Validação

### 1. Teste de userId inválido
```json
{
  "userId": "",
  "currency": "BRL"
}
```
**Resposta esperada**: 400 Bad Request com mensagem de validação

### 2. Teste de currency inválida
```json
{
  "userId": "user123",
  "currency": "INVALID"
}
```
**Resposta esperada**: 400 Bad Request com mensagem de validação

### 3. Teste de amount negativo
```json
{
  "amount": -10.00,
  "currency": "BRL"
}
```
**Resposta esperada**: 400 Bad Request com mensagem de validação

### 4. Teste de saldo insuficiente
```json
{
  "amount": 1000.00,
  "currency": "BRL"
}
```
**Resposta esperada**: 500 Internal Server Error com mensagem "Saldo insuficiente"

### 5. Teste de transferência para mesma carteira
```json
{
  "targetUserId": "user123",
  "amount": 25.00,
  "currency": "BRL"
}
```
**Resposta esperada**: 500 Internal Server Error com mensagem "Não é possível transferir para a mesma carteira"

## 🎯 Cenários Completos

### Cenário 1: Fluxo Completo - Usuário 1

1. **Criar carteira user1**
2. **Depositar 500 BRL**
3. **Consultar saldo**

### Cenário 2: Fluxo Completo - Usuário 2

1. **Criar carteira user2**
2. **Depositar 300 BRL**

### Cenário 3: Transferência Entre Usuários

1. **Transferir 100 BRL de user1 para user2**
2. **Consultar saldo user1**
3. **Consultar saldo user2**
4. **Consultar histórico user1**
5. **Consultar histórico user2**

## 🔍 Dicas de Debugging

### 1. Verificar Logs
```bash
docker-compose logs -f wallet-service
```

### 2. Verificar Banco de Dados
```bash
docker-compose exec postgres psql -U wallet_user -d wallet_db
```

### 3. Verificar Status dos Serviços
```bash
docker-compose ps
```

### 4. Verificar Métricas
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000

## 📊 Códigos de Resposta

| Código | Descrição |
|--------|-----------|
| 200 | Sucesso |
| 201 | Criado com sucesso |
| 400 | Dados inválidos |
| 404 | Carteira não encontrada |
| 409 | Carteira já existe |
| 500 | Erro interno do servidor |

## 🚨 Problemas Comuns

### 1. Serviço não responde
- Verifique se o Docker Compose está rodando
- Execute: `docker-compose up -d`

### 2. Erro de conexão
- Verifique se a porta 8080 está livre
- Verifique se o `base_url` está correto

### 3. Validações falhando
- Verifique o formato dos dados enviados
- Consulte as mensagens de erro retornadas

### 4. Banco de dados não conecta
- Verifique se o PostgreSQL está rodando
- Execute: `docker-compose logs postgres`

## 📈 Próximos Passos

1. **Execute todos os testes básicos**
2. **Teste os cenários de validação**
3. **Execute os cenários completos**
4. **Explore as métricas no Grafana**
5. **Analise os logs para auditoria**

---

**🎉 Parabéns! Você agora tem uma suite completa de testes para o microserviço de carteira digital!** 