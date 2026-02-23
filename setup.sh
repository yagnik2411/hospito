#!/bin/bash

echo "⏳ Waiting for app to start..."
sleep 15

echo "👤 Registering SUPER_ADMIN..."
curl -s -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Yagnik Admin",
    "email": "yagnik@hospito.com",
    "password": "admin123",
    "role": "SUPER_ADMIN"
  }' | python3 -m json.tool

echo "🔑 Saving token..."
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"yagnik@hospito.com","password":"admin123"}' \
  | python3 -c "import sys,json; print(json.load(sys.stdin)['data']['token'])")

echo "🏥 Creating hospital chain..."
curl -s -X POST http://localhost:8080/api/v1/chain \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Hospito Healthcare",
    "registrationNumber": "HH-2024-001",
    "foundedYear": 2024,
    "headOfficeAddress": "123 MG Road, Surat, Gujarat",
    "email": "info@hospito.com",
    "contactPhone": "+91-9876543210",
    "description": "Leading franchise hospital chain"
  }' | python3 -m json.tool

echo "✅ Setup complete! Token: $TOKEN"
echo ""
echo "Run this to save token for testing:"
echo "export TOKEN=$TOKEN"