# Cake Delight API Documentation

**Version:** 1.0.2  
**Project:** Cloud Native Microservices Application  
**Architecture:** Spring Boot Microservices with API Gateway

---

## Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [API Gateway Routes](#api-gateway-routes)
3. [Catalog Service API](#catalog-service-api)
4. [Order Service API](#order-service-api)
5. [Rating Service API](#rating-service-api)
6. [Notification Service API](#notification-service-api)
7. [Monitoring Endpoints](#monitoring-endpoints)
8. [RabbitMQ Event Flow](#rabbitmq-event-flow)
9. [Error Responses](#error-responses)

---

## Architecture Overview

### Request Flow Architecture

```text
┌─────────────────┐
│  React Frontend │
└────────┬────────┘
         │ HTTP Requests
         ▼
┌─────────────────┐
│   API Gateway   │ (Port 8080)
│  Spring Cloud   │
│     Gateway     │
└────────┬────────┘
         │
         ├──────────────────┬────────────────┬──────────────────┐
         ▼                  ▼                ▼                  ▼
┌────────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────────┐
│ Catalog Service│  │Order Service │  │Rating Service│  │Notification Svc  │
│   Port 8082    │  │  Port 8083   │  │  Port 8084   │  │    Port 8085     │
└───────┬────────┘  └──────┬───────┘  └──────┬───────┘  └────────┬─────────┘
        │                  │                 │                   │
        ▼                  ▼                 ▼                   ▼
┌────────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────────┐
│  catalog_db    │  │  order_db    │  │  rating_db   │  │ notification_db  │
│  PostgreSQL    │  │  PostgreSQL  │  │  PostgreSQL  │  │   PostgreSQL     │
└────────────────┘  └──────────────┘  └──────────────┘  └──────────────────┘
```

### Service Communication

| Service | Port | Database | Messaging |
|---------|------|----------|-----------|
| API Gateway | 8080 | - | - |
| Catalog Service | 8082 | catalog_db | - |
| Order Service | 8083 | order_db | RabbitMQ (Publisher) |
| Rating Service | 8084 | rating_db | - |
| Notification Service | 8085 | notification_db | RabbitMQ (Consumer) |

---

## API Gateway Routes

The API Gateway acts as a single entry point for all backend services. It routes requests based on path prefixes and strips the prefix before forwarding.

### Routing Configuration

| Frontend Path | Target Service | Target Port | Example |
|---------------|----------------|-------------|---------|
| `/api/catalog/**` | Catalog Service | 8082 | `/api/catalog/cakes` → `/cakes` |
| `/api/orders/**` | Order Service | 8083 | `/api/orders/basket` → `/basket` |
| `/api/ratings/**` | Rating Service | 8084 | `/api/ratings/cake/1` → `/cake/1` |
| `/api/notifications/**` | Notification Service | 8085 | `/api/notifications` → `/` |

### Gateway Behavior

- **StripPrefix=2**: Removes the first two path segments (`/api/<service>`)
- **Example**: `GET /api/catalog/cakes/1` is forwarded as `GET /cakes/1` to Catalog Service
- **Base URL**: `http://localhost:8080` (local) or `http://<minikube-ip>:30080` (Kubernetes)

---

## Catalog Service API

**Base URL**: `http://localhost:8082` (direct) or `http://localhost:8080/api/catalog` (via gateway)

**Purpose**: Manage cake catalog, browse products, and filter by category or price range.

### Endpoints Summary

| Method | Endpoint | Purpose |
|--------|----------|---------|
| GET | `/cakes` | Get all cakes with optional filters |
| GET | `/cakes/{id}` | Get a specific cake by ID |
| POST | `/cakes` | Create a new cake |

---

### 1. Get All Cakes

**Endpoint**: `GET /cakes`

**Purpose**: Retrieve all cakes with optional filtering by category and price range.

**Query Parameters**:
- `category` (optional): Filter by cake category (case-insensitive)
- `minPrice` (optional): Minimum price filter
- `maxPrice` (optional): Maximum price filter

**Request Example**:
```bash
# Get all cakes
curl http://localhost:8080/api/catalog/cakes

# Filter by category
curl http://localhost:8080/api/catalog/cakes?category=Celebration

# Filter by price range
curl http://localhost:8080/api/catalog/cakes?minPrice=300&maxPrice=500

# Combined filters
curl http://localhost:8080/api/catalog/cakes?category=Classic&minPrice=250&maxPrice=400
```

**Response**: `200 OK`
```json
[
  {
    "id": 1,
    "name": "Macarons Cake",
    "description": "Delicious Macarons Cake.",
    "category": "Celebration",
    "price": 250,
    "availability": true,
    "imageUrl": "https://images.unsplash.com/photo-1562440499-64c9a111f713..."
  },
  {
    "id": 2,
    "name": "Raspberry Layer Cake",
    "description": "Delicious Raspberry Layer Cake.",
    "category": "Classic",
    "price": 300,
    "availability": true,
    "imageUrl": "https://plus.unsplash.com/premium_photo-1713447395823..."
  }
]
```

**Status Codes**:
- `200 OK`: Successfully retrieved cakes
- `500 Internal Server Error`: Server error

---

### 2. Get Cake by ID

**Endpoint**: `GET /cakes/{id}`

**Purpose**: Retrieve detailed information about a specific cake.

**Path Parameters**:
- `id` (required): Cake identifier

**Request Example**:
```bash
curl http://localhost:8080/api/catalog/cakes/1
```

**Response**: `200 OK`
```json
{
  "id": 1,
  "name": "Macarons Cake",
  "description": "Delicious Macarons Cake.",
  "category": "Celebration",
  "price": 250,
  "availability": true,
  "imageUrl": "https://images.unsplash.com/photo-1562440499-64c9a111f713..."
}
```

**Status Codes**:
- `200 OK`: Cake found and returned
- `404 Not Found`: Cake with specified ID does not exist
- `500 Internal Server Error`: Server error

---

### 3. Create Cake

**Endpoint**: `POST /cakes`

**Purpose**: Add a new cake to the catalog.

**Request Body**:
```json
{
  "name": "Chocolate Truffle Cake",
  "description": "Rich chocolate cake with truffle filling",
  "category": "Premium",
  "price": 450,
  "availability": true,
  "imageUrl": "https://example.com/chocolate-truffle.jpg"
}
```

**Request Example**:
```bash
curl -X POST http://localhost:8080/api/catalog/cakes \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Chocolate Truffle Cake",
    "description": "Rich chocolate cake with truffle filling",
    "category": "Premium",
    "price": 450,
    "availability": true,
    "imageUrl": "https://example.com/chocolate-truffle.jpg"
  }'
```

**Response**: `201 Created`
```json
{
  "id": 21,
  "name": "Chocolate Truffle Cake",
  "description": "Rich chocolate cake with truffle filling",
  "category": "Premium",
  "price": 450,
  "availability": true,
  "imageUrl": "https://example.com/chocolate-truffle.jpg"
}
```

**Response Headers**:
```
Location: /cakes/21
```

**Status Codes**:
- `201 Created`: Cake successfully created
- `400 Bad Request`: Invalid request body or validation error
- `500 Internal Server Error`: Server error

**Validation Rules**:
- `name`: Required, cannot be blank
- `price`: Required, must be positive
- `category`: Optional
- `description`: Optional
- `availability`: Optional (defaults to `true`)
- `imageUrl`: Optional

---

## Order Service API

**Base URL**: `http://localhost:8083` (direct) or `http://localhost:8080/api/orders` (via gateway)

**Purpose**: Manage shopping basket, process checkout, and create orders.

### Endpoints Summary

| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/basket` | Add item to basket |
| GET | `/basket` | Get all basket items |
| PUT | `/basket/{id}` | Update basket item quantity |
| DELETE | `/basket/{id}` | Remove item from basket |
| POST | `/checkout` | Process checkout and create order |

---

### 1. Add Item to Basket

**Endpoint**: `POST /basket`

**Purpose**: Add a cake to the shopping basket. If the cake already exists in the basket, the quantity is incremented by 1.

**Request Body**:
```json
{
  "cakeId": 1,
  "cakeName": "Macarons Cake",
  "price": 250,
  "quantity": 1
}
```

**Request Example**:
```bash
curl -X POST http://localhost:8080/api/orders/basket \
  -H "Content-Type: application/json" \
  -d '{
    "cakeId": 1,
    "cakeName": "Macarons Cake",
    "price": 250,
    "quantity": 1
  }'
```

**Response**: `201 Created`
```json
{
  "id": 1,
  "cakeId": 1,
  "cakeName": "Macarons Cake",
  "price": 250,
  "quantity": 1
}
```

**Response Headers**:
```
Location: /basket/1
```

**Status Codes**:
- `201 Created`: Item added to basket
- `400 Bad Request`: Invalid request body or validation error
- `500 Internal Server Error`: Server error

**Validation Rules**:
- `cakeId`: Required
- `cakeName`: Required, cannot be blank
- `price`: Required, must be positive
- `quantity`: Required, must be positive

---

### 2. Get All Basket Items

**Endpoint**: `GET /basket`

**Purpose**: Retrieve all items currently in the shopping basket.

**Request Example**:
```bash
curl http://localhost:8080/api/orders/basket
```

**Response**: `200 OK`
```json
[
  {
    "id": 1,
    "cakeId": 1,
    "cakeName": "Macarons Cake",
    "price": 250,
    "quantity": 2
  },
  {
    "id": 2,
    "cakeId": 3,
    "cakeName": "Black Forest Cake",
    "price": 350,
    "quantity": 1
  }
]
```

**Status Codes**:
- `200 OK`: Successfully retrieved basket items
- `500 Internal Server Error`: Server error

---

### 3. Update Basket Item

**Endpoint**: `PUT /basket/{id}`

**Purpose**: Update the quantity of a specific basket item.

**Path Parameters**:
- `id` (required): Basket item identifier

**Request Body**:
```json
{
  "cakeId": 1,
  "cakeName": "Macarons Cake",
  "price": 250,
  "quantity": 3
}
```

**Request Example**:
```bash
curl -X PUT http://localhost:8080/api/orders/basket/1 \
  -H "Content-Type: application/json" \
  -d '{
    "cakeId": 1,
    "cakeName": "Macarons Cake",
    "price": 250,
    "quantity": 3
  }'
```

**Response**: `200 OK`
```json
{
  "id": 1,
  "cakeId": 1,
  "cakeName": "Macarons Cake",
  "price": 250,
  "quantity": 3
}
```

**Status Codes**:
- `200 OK`: Basket item successfully updated
- `400 Bad Request`: Invalid request body or validation error
- `404 Not Found`: Basket item with specified ID does not exist
- `500 Internal Server Error`: Server error

---

### 4. Remove Basket Item

**Endpoint**: `DELETE /basket/{id}`

**Purpose**: Remove a specific item from the shopping basket.

**Path Parameters**:
- `id` (required): Basket item identifier

**Request Example**:
```bash
curl -X DELETE http://localhost:8080/api/orders/basket/1
```

**Response**: `204 No Content`

**Status Codes**:
- `204 No Content`: Item successfully removed from basket
- `404 Not Found`: Basket item with specified ID does not exist
- `500 Internal Server Error`: Server error

---

### 5. Checkout

**Endpoint**: `POST /checkout`

**Purpose**: Process checkout by creating an order from all basket items. This operation:
1. Validates that the basket is not empty
2. Calculates the total amount
3. Creates a new order with status "COMPLETED"
4. Creates order items from basket items
5. Publishes an order completed event to RabbitMQ
6. Clears the basket

**Request Example**:
```bash
curl -X POST http://localhost:8080/api/orders/checkout
```

**Response**: `201 Created`
```json
{
  "id": 1,
  "totalAmount": 850,
  "status": "COMPLETED",
  "createdAt": "2026-08-13T10:30:00"
}
```

**Response Headers**:
```
Location: /orders/1
```

**Status Codes**:
- `201 Created`: Order successfully created
- `400 Bad Request`: Basket is empty
- `500 Internal Server Error`: Server error

**Side Effects**:
- Basket is cleared after successful checkout
- Order completed event is published to RabbitMQ (`order.exchange` with routing key `order.completed`)
- Notification service receives the event and creates a notification

---

## Rating Service API

**Base URL**: `http://localhost:8084` (direct) or `http://localhost:8080/api/ratings` (via gateway)

**Purpose**: Manage cake ratings and reviews, calculate average ratings.

### Endpoints Summary

| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/ratings` | Create a new rating |
| GET | `/ratings/cake/{cakeId}` | Get all ratings for a cake |
| GET | `/ratings/cake/{cakeId}/average` | Get average rating for a cake |

---

### 1. Create Rating

**Endpoint**: `POST /ratings`

**Purpose**: Submit a new rating and optional review for a cake.

**Request Body**:
```json
{
  "cakeId": 1,
  "ratingValue": 5,
  "review": "Absolutely delicious! Best cake I've ever had."
}
```

**Request Example**:
```bash
curl -X POST http://localhost:8080/api/ratings/ratings \
  -H "Content-Type: application/json" \
  -d '{
    "cakeId": 1,
    "ratingValue": 5,
    "review": "Absolutely delicious! Best cake I've ever had."
  }'
```

**Response**: `201 Created`
```json
{
  "id": 1,
  "cakeId": 1,
  "ratingValue": 5,
  "review": "Absolutely delicious! Best cake I've ever had.",
  "createdAt": "2026-08-13T10:30:00"
}
```

**Status Codes**:
- `201 Created`: Rating successfully created
- `400 Bad Request`: Invalid request body or validation error
- `500 Internal Server Error`: Server error

**Validation Rules**:
- `cakeId`: Required
- `ratingValue`: Required, must be between 1 and 5 (inclusive)
- `review`: Optional, max length 1000 characters

---

### 2. Get Ratings by Cake ID

**Endpoint**: `GET /ratings/cake/{cakeId}`

**Purpose**: Retrieve all ratings and reviews for a specific cake.

**Path Parameters**:
- `cakeId` (required): Cake identifier

**Request Example**:
```bash
curl http://localhost:8080/api/ratings/ratings/cake/1
```

**Response**: `200 OK`
```json
[
  {
    "id": 1,
    "cakeId": 1,
    "ratingValue": 5,
    "review": "Absolutely delicious! Best cake I've ever had.",
    "createdAt": "2026-08-13T10:30:00"
  },
  {
    "id": 2,
    "cakeId": 1,
    "ratingValue": 4,
    "review": "Very good, slightly too sweet for my taste.",
    "createdAt": "2026-08-13T11:15:00"
  }
]
```

**Status Codes**:
- `200 OK`: Successfully retrieved ratings
- `500 Internal Server Error`: Server error

---

### 3. Get Average Rating

**Endpoint**: `GET /ratings/cake/{cakeId}/average`

**Purpose**: Calculate and retrieve the average rating for a specific cake.

**Path Parameters**:
- `cakeId` (required): Cake identifier

**Request Example**:
```bash
curl http://localhost:8080/api/ratings/ratings/cake/1/average
```

**Response**: `200 OK`
```json
{
  "cakeId": 1,
  "averageRating": 4.5
}
```

**Status Codes**:
- `200 OK`: Successfully calculated average
- `500 Internal Server Error`: Server error

**Notes**:
- If no ratings exist for the cake, `averageRating` will be `0.0`
- The average is rounded to 2 decimal places

---

## Notification Service API

**Base URL**: `http://localhost:8085` (direct) or `http://localhost:8080/api/notifications` (via gateway)

**Purpose**: Retrieve notifications triggered by order completion events.

### Endpoints Summary

| Method | Endpoint | Purpose |
|--------|----------|---------|
| GET | `/notifications` | Get all notifications |

---

### 1. Get All Notifications

**Endpoint**: `GET /notifications`

**Purpose**: Retrieve all notifications that have been created from order completion events.

**Request Example**:
```bash
curl http://localhost:8080/api/notifications/notifications
```

**Response**: `200 OK`
```json
[
  {
    "id": 1,
    "orderId": 1,
    "message": "Order 1 completed with amount 850",
    "status": "SENT",
    "createdAt": "2026-08-13T10:30:05"
  },
  {
    "id": 2,
    "orderId": 2,
    "message": "Order 2 completed with amount 450",
    "status": "SENT",
    "createdAt": "2026-08-13T11:20:10"
  }
]
```

**Status Codes**:
- `200 OK`: Successfully retrieved notifications
- `500 Internal Server Error`: Server error

**Notes**:
- Notifications are automatically created when the Notification Service consumes order completed events from RabbitMQ
- The `message` field is automatically formatted with order ID and total amount
- All notifications have status "SENT"

---

## Monitoring Endpoints

All microservices expose Spring Boot Actuator endpoints for health monitoring and diagnostics.

### Actuator Endpoints

| Service | Health Endpoint | Info Endpoint |
|---------|----------------|---------------|
| API Gateway | `http://localhost:8080/actuator/health` | `http://localhost:8080/actuator/info` |
| Catalog Service | `http://localhost:8082/actuator/health` | `http://localhost:8082/actuator/info` |
| Order Service | `http://localhost:8083/actuator/health` | `http://localhost:8083/actuator/info` |
| Rating Service | `http://localhost:8084/actuator/health` | `http://localhost:8084/actuator/info` |
| Notification Service | `http://localhost:8085/actuator/health` | `http://localhost:8085/actuator/info` |

### Health Check Example

**Request**:
```bash
curl http://localhost:8082/actuator/health
```

**Response**: `200 OK`
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 499963174912,
        "free": 400000000000,
        "threshold": 10485760,
        "exists": true
      }
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

**Order/Notification Service Health** (includes RabbitMQ):
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL"
      }
    },
    "diskSpace": {
      "status": "UP"
    },
    "ping": {
      "status": "UP"
    },
    "rabbit": {
      "status": "UP",
      "details": {
        "version": "3.13.0"
      }
    }
  }
}
```

### Custom Health Endpoints

Each service also exposes a custom health endpoint:

**Request**:
```bash
curl http://localhost:8082/health
```

**Response**: `200 OK`
```
UP
```

---

## RabbitMQ Event Flow

### Asynchronous Order Event Processing

```text
┌─────────────────┐
│  Order Service  │
│   Port 8083     │
└────────┬────────┘
         │
         │ 1. POST /checkout
         │    Creates order
         ▼
┌─────────────────────────┐
│  OrderEventPublisher    │
└────────┬────────────────┘
         │
         │ 2. publishOrderCompleted()
         │    Routing Key: order.completed
         ▼
┌─────────────────────────┐
│      RabbitMQ           │
│   order.exchange        │
│   (Topic Exchange)      │
└────────┬────────────────┘
         │
         │ 3. Route to notification.queue
         ▼
┌─────────────────────────┐
│  OrderEventListener     │
│ (Notification Service)  │
└────────┬────────────────┘
         │
         │ 4. handleOrderCompleted()
         │    Creates notification
         ▼
┌─────────────────────────┐
│ Notification Service    │
│     Port 8085           │
│  notification_db        │
└─────────────────────────┘
```

### Event Message Structure

**OrderCompletedEvent**:
```json
{
  "orderId": 1,
  "totalAmount": 850,
  "createdAt": "2026-08-13T10:30:00"
}
```

### RabbitMQ Configuration

| Component | Value |
|-----------|-------|
| **Exchange Name** | `order.exchange` |
| **Exchange Type** | Topic Exchange |
| **Routing Key** | `order.completed` |
| **Queue Name** | `notification.queue` |
| **Queue Type** | Durable |
| **Publisher** | Order Service |
| **Consumer** | Notification Service |

### Event Processing Flow

1. **Checkout Initiated**: User calls `POST /checkout` on Order Service
2. **Order Created**: Order Service creates order and order items in database
3. **Event Published**: Order Service publishes `OrderCompletedEvent` to RabbitMQ
4. **Event Consumed**: Notification Service receives event from queue
5. **Notification Created**: Notification Service creates notification record
6. **Basket Cleared**: Order Service clears user's basket

**Note**: Event publishing failures are logged but do not interrupt the checkout process. This ensures order creation always completes even if messaging fails.

---

## Error Responses

### Standard Error Format

All services use consistent error response formats:

**Validation Error** (`400 Bad Request`):
```json
{
  "cakeId": "cakeId is required",
  "ratingValue": "ratingValue must be at least 1"
}
```

**Not Found Error** (`404 Not Found`):
```json
{
  "timestamp": "2026-08-13T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Cake not found: 999",
  "path": "/cakes/999"
}
```

**Server Error** (`500 Internal Server Error`):
```json
{
  "error": "Internal server error message"
}
```

### Common HTTP Status Codes

| Code | Meaning | When Used |
|------|---------|-----------|
| 200 | OK | Successful GET, PUT requests |
| 201 | Created | Successful POST requests |
| 204 | No Content | Successful DELETE requests |
| 400 | Bad Request | Validation errors, empty basket |
| 404 | Not Found | Resource does not exist |
| 500 | Internal Server Error | Unexpected server errors |

---

## Quick Reference - All Endpoints

### Catalog Service

| Method | Path | Purpose |
|--------|------|---------|
| GET | `/cakes` | List all cakes (with optional filters) |
| GET | `/cakes/{id}` | Get cake by ID |
| POST | `/cakes` | Create new cake |

### Order Service

| Method | Path | Purpose |
|--------|------|---------|
| POST | `/basket` | Add item to basket |
| GET | `/basket` | Get all basket items |
| PUT | `/basket/{id}` | Update basket item |
| DELETE | `/basket/{id}` | Remove basket item |
| POST | `/checkout` | Process checkout |

### Rating Service

| Method | Path | Purpose |
|--------|------|---------|
| POST | `/ratings` | Create rating |
| GET | `/ratings/cake/{cakeId}` | Get ratings for cake |
| GET | `/ratings/cake/{cakeId}/average` | Get average rating |

### Notification Service

| Method | Path | Purpose |
|--------|------|---------|
| GET | `/notifications` | Get all notifications |

### Monitoring (All Services)

| Method | Path | Purpose |
|--------|------|---------|
| GET | `/actuator/health` | Service health status |
| GET | `/actuator/info` | Service information |
| GET | `/health` | Custom health check |

---

## Testing with cURL

### Complete User Flow Example

```bash
# 1. Get all cakes
curl http://localhost:8080/api/catalog/cakes

# 2. Get specific cake details
curl http://localhost:8080/api/catalog/cakes/1

# 3. Add cakes to basket
curl -X POST http://localhost:8080/api/orders/basket \
  -H "Content-Type: application/json" \
  -d '{"cakeId": 1, "cakeName": "Macarons Cake", "price": 250, "quantity": 1}'

curl -X POST http://localhost:8080/api/orders/basket \
  -H "Content-Type: application/json" \
  -d '{"cakeId": 3, "cakeName": "Black Forest Cake", "price": 350, "quantity": 2}'

# 4. View basket
curl http://localhost:8080/api/orders/basket

# 5. Update basket item quantity
curl -X PUT http://localhost:8080/api/orders/basket/1 \
  -H "Content-Type: application/json" \
  -d '{"cakeId": 1, "cakeName": "Macarons Cake", "price": 250, "quantity": 3}'

# 6. Process checkout
curl -X POST http://localhost:8080/api/orders/checkout

# 7. Rate a cake
curl -X POST http://localhost:8080/api/ratings/ratings \
  -H "Content-Type: application/json" \
  -d '{"cakeId": 1, "ratingValue": 5, "review": "Excellent cake!"}'

# 8. View notifications
curl http://localhost:8080/api/notifications/notifications

# 9. Check service health
curl http://localhost:8080/actuator/health
```

---

## Kubernetes Deployment URLs

When deployed to Kubernetes with Minikube, use the NodePort service URLs:

- **Frontend**: `http://<minikube-ip>:30081`
- **API Gateway**: `http://<minikube-ip>:30080`

Get your Minikube IP:
```bash
minikube ip
```

Then access the API through the gateway:
```bash
curl http://192.168.49.2:30080/api/catalog/cakes
```

---

**Last Updated**: 2026-08-13  
**API Version**: 1.0.2  
**Documentation Version**: 1.0.0
