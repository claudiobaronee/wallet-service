# 🧪 Postman Testing Guide

This guide explains how to use the Postman collection to fully test the digital wallet microservice.

## 📋 Prerequisites

1. **Postman installed** - Download at [postman.com](https://www.postman.com/downloads/)
2. **Service running** - Run `docker-compose up -d`
3. **Collection imported** - Import the `Wallet_Service_API.postman_collection.json` file

## 🚀 Initial Setup

### 1. Import the Collection

1. Open Postman
2. Click **Import**
3. Select the `Wallet_Service_API.postman_collection.json` file
4. The collection will be imported with all requests organized

### 2. Configure Variables

1. Click the **Wallet Service API** collection
2. Go to the **Variables** tab
3. Set the variables:

| Variable   | Value                | Description           |
|------------|----------------------|-----------------------|
| `base_url` | `http://localhost:8080` | API base URL         |
| `userId`   | `user123`            | User ID for testing   |

### 3. Check Setup

Run the **Health Check** to verify everything is working:

```
GET {{base_url}}/api/wallets/health
```

Expected response:
```json
{
  "status": "UP"
}
```

## 📚 Collection Structure

### 🏥 Health Check
- **Health Check**: Checks if the service is running

### 🔧 Basic Operations
- **Create Wallet**: Creates a new wallet for a user
- **Get Wallet**: Gets wallet details
- **Deposit**: Makes a deposit into the wallet
- **Withdraw**: Makes a withdrawal from the wallet
- **Transfer Between Wallets**: Transfers money between wallets
- **Get Balance History**: Gets the transaction history

### 🧪 Validation Tests
- **Invalid userId**: Tests empty userId validation
- **Invalid currency**: Tests invalid currency validation
- **Invalid amount**: Tests negative value validation
- **Insufficient balance**: Tests withdrawal with insufficient funds
- **Same wallet**: Tests transfer to the same wallet

### 🎯 Test Scenarios
- **Full Flow - User 1**: Creation, deposit, and get
- **Full Flow - User 2**: Creation and deposit
- **Transfer Between Users**: Complete transfer test

## 🎮 How to Run the Tests

### 1. Basic Test - Create and Get Wallet

1. **Run "Create Wallet"**:
   ```json
   {
     "userId": "user123",
     "currency": "BRL"
   }
   ```
   
   Expected response (201):
   ```json
   {
     "userId": "user123",
     "currency": "BRL",
     "status": "ACTIVE",
     "message": "Wallet created successfully",
     "createdAt": "2024-01-01 10:00:00"
   }
   ```

2. **Run "Get Wallet"**:
   - URL: `{{base_url}}/api/wallets/user123`
   
   Expected response (200):
   ```json
   {
     "userId": "user123",
     "currency": "BRL",
     "balance": 0.00,
     "status": "ACTIVE"
   }
   ```

### 2. Deposit Test

1. **Run "Deposit"**:
   ```json
   {
     "amount": 100.50,
     "currency": "BRL",
     "description": "Initial deposit"
   }
   ```
   
   Expected response (200):
   ```json
   {
     "message": "Deposit successful",
     "amount": 100.50,
     "currency": "BRL",
     "newBalance": 100.50,
     "transactionId": "generated-uuid"
   }
   ```

### 3. Withdrawal Test

1. **Run "Withdraw"**:
   ```json
   {
     "amount": 50.25,
     "currency": "BRL",
     "description": "Payment withdrawal"
   }
   ```
   
   Expected response (200):
   ```json
   {
     "message": "Withdrawal successful",
     "amount": 50.25,
     "currency": "BRL",
     "newBalance": 50.25,
     "transactionId": "generated-uuid"
   }
   ```

### 4. Transfer Test

1. **First, create a second wallet**:
   ```json
   {
     "userId": "user456",
     "currency": "BRL"
   }
   ```

2. **Run "Transfer Between Wallets"**:
   ```json
   {
     "targetUserId": "user456",
     "amount": 25.00,
     "currency": "BRL",
     "description": "Payment transfer"
   }
   ```
   
   Expected response (200):
   ```json
   {
     "message": "Transfer successful",
     "amount": 25.00,
     "currency": "BRL",
     "sourceNewBalance": 25.25,
     "targetNewBalance": 25.00,
     "transactionId": "generated-uuid"
   }
   ```

### 5. History Test

1. **Run "Get Balance History"**:
   - URL: `{{base_url}}/api/wallets/user123/balance-history`
   
   Expected response (200):
   ```json
   {
     "history": [
       {
         "balance": 0.00,
         "currency": "BRL",
         "description": "Wallet creation",
         "recordedAt": "2024-01-01 10:00:00"
       },
       {
         "balance": 100.50,
         "currency": "BRL",
         "description": "Initial deposit",
         "recordedAt": "2024-01-01 10:01:00"
       },
       {
         "balance": 50.25,
         "currency": "BRL",
         "description": "Payment withdrawal",
         "recordedAt": "2024-01-01 10:02:00"
       },
       {
         "balance": 25.25,
         "currency": "BRL",
         "description": "Transfer sent of 25.00 BRL to user456",
         "recordedAt": "2024-01-01 10:03:00"
       }
     ]
   }
   ```

## 🧪 Validation Tests

### 1. Invalid userId Test
```json
{
  "userId": "",
  "currency": "BRL"
}
```
**Expected response**: 400 Bad Request with validation message

### 2. Invalid currency Test
```json
{
  "userId": "user123",
  "currency": "INVALID"
}
```
**Expected response**: 400 Bad Request with validation message

### 3. Negative amount Test
```json
{
  "amount": -10.00,
  "currency": "BRL"
}
```
**Expected response**: 400 Bad Request with validation message

### 4. Insufficient balance Test
```json
{
  "amount": 1000.00,
  "currency": "BRL"
}
```
**Expected response**: 500 Internal Server Error with message "Insufficient balance"

### 5. Transfer to same wallet Test
```json
{
  "targetUserId": "user123",
  "amount": 25.00,
  "currency": "BRL"
}
```
**Expected response**: 500 Internal Server Error with message "Cannot transfer to the same wallet"

## 🎯 Complete Scenarios

### Scenario 1: Full Flow - User 1

1. **Create wallet user1**
2. **Deposit 500 BRL**
3. **Get balance**

### Scenario 2: Full Flow - User 2

1. **Create wallet user2**
2. **Deposit 300 BRL**

### Scenario 3: Transfer Between Users

1. **Transfer 100 BRL from user1 to user2**
2. **Get balance user1**
3. **Get balance user2**
4. **Get history user1**
5. **Get history user2**

## 🔍 Debugging Tips

### 1. Check Logs
```bash
docker-compose logs -f wallet-service
```

### 2. Check Database
```bash
docker-compose exec postgres psql -U wallet_user -d wallet_db
```

### 3. Check Service Status
```bash
docker-compose ps
```

### 4. Check Metrics
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000

## 📊 Response Codes

| Code | Description |
|------|-------------|
| 200  | Success     |
| 201  | Created successfully |
| 400  | Invalid data |
| 404  | Wallet not found |
| 409  | Wallet already exists |
| 500  | Internal server error |

## 🚨 Common Issues

### 1. Service not responding
- Check if Docker Compose is running
- Run: `docker-compose up -d`

### 2. Connection error
- Check if port 8080 is free
- Check if `base_url` is correct

### 3. Validation failures
- Check the format of the data sent
- Check the returned error messages

### 4. Database not connecting
- Check if PostgreSQL is running
- Run: `docker-compose logs postgres`

## 📈 Next Steps

1. **Run all basic tests**
2. **Test validation scenarios**
3. **Run complete scenarios**
4. **Explore metrics in Grafana**
5. **Analyze logs for auditing**

---

**🎉 Congratulations! You now have a complete test suite for the digital wallet microservice!** 