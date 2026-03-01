# 🚀 Webhook Engine

A secure, production-grade webhook subscription and delivery engine built with **Spring Boot**.

This service allows external partner systems to:

- Register webhook subscriptions
- Receive signed event notifications
- Verify authenticity using HMAC SHA256
- Handle retries and idempotent delivery

---

## ✨ Features

- 🔐 HMAC SHA256 webhook signing
- 🔁 Exponential backoff retry mechanism
- 🧾 Delivery attempt tracking
- 🛡 Signature + timestamp validation
- 🧩 Idempotent event handling
- 📦 Clean OpenAPI contract
- 🏦 Designed for financial-grade integrations

---

## 🏗 Architecture Overview

Event Occurs
↓
Persist Event
↓
Fetch Active Subscriptions
↓
Generate Signed Payload
↓
POST → Client Callback URL
↓
Store Delivery Result
↓
Retry (if needed)

---

## 📌 Subscription API

### Create Subscription

POST /v1/api/subscription

### Request

{
"clientId": "partner-system-01",
"callBackUrl": "https://partner-domain.com/api/webhook",
"eventType": "payment.completed"
}

### Response (201 Created)

{
"subscriptionId": "uuid",
"clientId": "partner-system-01",
"eventType": "payment.completed",
"status": "ACTIVE"
}

---

## 🔔 Webhook Delivery Format

### Method
POST

### Headers

- X-Webhook-Event-Id
- X-Webhook-Event-Type
- X-Webhook-Timestamp
- X-Webhook-Signature

### Example Body

{
"eventId": "3f6c1e9a-8f01",
"eventType": "payment.completed",
"timestamp": "2026-03-01T18:21:30Z",
"data": {
"transactionId": "TX12345",
"amount": 1500,
"currency": "KES",
"status": "SUCCESS"
}
}

---

## 🔐 Signature Verification

signed_payload = timestamp + "." + raw_request_body
signature = HMAC_SHA256(secret_key, signed_payload)

Header format:

X-Webhook-Signature: sha256=<hash>

---

## 🔁 Retry Policy

Delivery is successful only if HTTP 2xx is returned.

Retry Strategy:

- Exponential backoff
- Maximum 5 attempts
- Marked FAILED after max retries

Timeout expectation: 5 seconds

---

## 🔄 Idempotency

Partners must treat `eventId` as idempotent:

- Store processed event IDs
- Ignore duplicates

---

## 🛠 Local Development

Run:

./mvnw spring-boot:run

or

mvn clean install
java -jar target/app.jar

Default:

http://localhost:4040

---

## ⚙️ Configuration

server.port=4040
webhook.retry.maxAttempts=5
webhook.retry.initialDelay=2000
webhook.timeout.millis=5000

---

## 📞 Support

When reporting webhook issues, include:

- eventId
- Timestamp
- HTTP response code
- Delivery attempt number

---

© Webhook Engine
