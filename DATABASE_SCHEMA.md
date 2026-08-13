# Cake Delight Database Schema Documentation

**Project**: Cloud Native Microservices Application  
**Database System**: PostgreSQL 15  
**Architecture**: Database-per-Service Pattern  
**ORM**: Hibernate (JPA) with Spring Data JPA

---

## Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Persistence Strategy](#persistence-strategy)
3. [Database Per Service](#database-per-service)
4. [Catalog Database (catalog_db)](#catalog-database-catalog_db)
5. [Order Database (order_db)](#order-database-order_db)
6. [Rating Database (rating_db)](#rating-database-rating_db)
7. [Notification Database (notification_db)](#notification-database-notification_db)
8. [Entity Relationships](#entity-relationships)
9. [Data Types Reference](#data-types-reference)
10. [Hibernate Configuration](#hibernate-configuration)

---

## Architecture Overview

### Database-per-Service Architecture

```text
┌─────────────────────────────────────────────────────────────────┐
│                    CAKE DELIGHT MICROSERVICES                   │
└─────────────────────────────────────────────────────────────────┘

┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐
│ Catalog Service  │  │  Order Service   │  │  Rating Service  │  │Notification Svc  │
│   Port 8082      │  │   Port 8083      │  │   Port 8084      │  │   Port 8085      │
└────────┬─────────┘  └────────┬─────────┘  └────────┬─────────┘  └────────┬─────────┘
         │                     │                      │                     │
         │ Dedicated           │ Dedicated            │ Dedicated           │ Dedicated
         │ Connection          │ Connection           │ Connection          │ Connection
         │                     │                      │                     │
         ▼                     ▼                      ▼                     ▼
┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
│   catalog_db    │  │    order_db     │  │    rating_db    │  │ notification_db │
│  PostgreSQL 15  │  │  PostgreSQL 15  │  │  PostgreSQL 15  │  │  PostgreSQL 15  │
├─────────────────┤  ├─────────────────┤  ├─────────────────┤  ├─────────────────┤
│ Tables:         │  │ Tables:         │  │ Tables:         │  │ Tables:         │
│ • cakes         │  │ • basket_items  │  │ • ratings       │  │ • notifications │
│                 │  │ • customer_     │  │                 │  │                 │
│                 │  │   orders        │  │                 │  │                 │
│                 │  │ • order_items   │  │                 │  │                 │
└─────────────────┘  └─────────────────┘  └─────────────────┘  └─────────────────┘
```

### Service-Database Mapping

| Microservice | Database | Port | Tables | Purpose |
|--------------|----------|------|--------|---------|
| Catalog Service | `catalog_db` | 8082 | 1 | Product catalog management |
| Order Service | `order_db` | 8083 | 3 | Basket and order management |
| Rating Service | `rating_db` | 8084 | 1 | Customer ratings and reviews |
| Notification Service | `notification_db` | 8085 | 1 | Event-driven notifications |

**Total Databases**: 4  
**Total Tables**: 6  
**Database Engine**: PostgreSQL 15

---

## Persistence Strategy

### Database-per-Service Pattern

Cake Delight implements the **database-per-service** microservices pattern, where each microservice owns and manages its own dedicated database. This architectural decision provides several key benefits:

#### **1. Service Autonomy**

Each microservice has complete control over its data model and can:
- Make schema changes independently without coordinating with other teams
- Choose optimal data structures for its specific use case
- Evolve its database schema at its own pace
- Deploy database migrations independently

#### **2. Technology Flexibility**

While Cake Delight uses PostgreSQL 15 across all services for consistency, the database-per-service pattern allows:
- Each service to potentially use different database technologies (SQL, NoSQL, Graph, etc.)
- Optimization of database choice based on service requirements
- Independent database version upgrades

#### **3. Loose Coupling**

Services are loosely coupled through:
- **No shared database tables**: Each service owns its data
- **API-based communication**: Services interact through REST APIs
- **Event-driven architecture**: Order Service publishes events; Notification Service consumes them
- **Data duplication**: Services store copies of data they need (e.g., Order Service stores `cakeName` and `price`)

#### **4. Fault Isolation**

Database isolation provides resilience:
- Database failure in one service doesn't affect others
- Resource contention (locks, connections) is isolated per service
- Database maintenance can be performed independently
- Recovery from failures is service-specific

#### **5. Scalability**

Independent scaling capabilities:
- Each database can be scaled based on its service's load
- High-traffic services (e.g., Catalog) can have larger database resources
- Different replication strategies per database
- Independent backup and disaster recovery plans

#### **6. Clear Data Ownership**

Data ownership boundaries are explicit:
- Catalog Service owns cake product information
- Order Service owns basket and order data
- Rating Service owns rating and review data
- Notification Service owns notification records

#### **Trade-offs**

This pattern introduces some complexity:
- **Data Duplication**: Services store copies of data (eventual consistency)
- **Distributed Transactions**: No ACID guarantees across services
- **Cross-Service Queries**: Must aggregate data at application level
- **Referential Integrity**: Foreign key constraints only within a service

However, these trade-offs are acceptable for the benefits of service independence, scalability, and resilience in a microservices architecture.

---

## Database Per Service

### Connection Configuration

Each service connects to its dedicated PostgreSQL database using JDBC:

```properties
# Catalog Service
spring.datasource.url=jdbc:postgresql://localhost:5432/catalog_db

# Order Service
spring.datasource.url=jdbc:postgresql://localhost:5432/order_db

# Rating Service
spring.datasource.url=jdbc:postgresql://localhost:5432/rating_db

# Notification Service
spring.datasource.url=jdbc:postgresql://localhost:5432/notification_db
```

### Kubernetes Deployment

In Kubernetes, each database runs as a separate Deployment with:
- **Dedicated Pod**: Isolated PostgreSQL instance
- **ClusterIP Service**: Internal service discovery
- **PersistentVolumeClaim**: 1Gi storage per database
- **Secrets**: Individual credentials management

---

## Catalog Database (catalog_db)

**Service**: Catalog Service (Port 8082)  
**Purpose**: Store and manage cake product catalog  
**Entities**: 1  
**Tables**: 1

### Entity: Cake

**Java Class**: `com.cakedelight.catalog.entity.Cake`  
**Table Name**: `cakes`  
**Primary Key**: `id` (auto-generated)

#### Table Schema: cakes

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique cake identifier |
| `name` | VARCHAR(255) | NOT NULL | Cake name (e.g., "Macarons Cake") |
| `description` | VARCHAR(255) | NULL | Detailed cake description |
| `category` | VARCHAR(255) | NULL | Cake category (e.g., "Celebration", "Classic") |
| `price` | NUMERIC(19,2) | NOT NULL, CHECK (price > 0) | Cake price in currency units |
| `availability` | BOOLEAN | DEFAULT TRUE | Whether cake is available for purchase |
| `image_url` | VARCHAR(1000) | NULL | URL to cake image |

#### SQL DDL (Generated by Hibernate)

```sql
CREATE TABLE cakes (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    category VARCHAR(255),
    price NUMERIC(19,2) NOT NULL CHECK (price > 0),
    availability BOOLEAN DEFAULT TRUE,
    image_url VARCHAR(1000)
);

CREATE INDEX idx_cakes_category ON cakes(category);
CREATE INDEX idx_cakes_price ON cakes(price);
```

#### Validation Rules

- `name`: Required, cannot be blank
- `price`: Required, must be positive (> 0)
- `category`: Optional, case-insensitive searches
- `description`: Optional
- `availability`: Defaults to `TRUE` if not provided
- `imageUrl`: Optional, max length 1000 characters

#### Sample Data

```sql
INSERT INTO cakes (name, description, category, price, availability, image_url) VALUES
('Macarons Cake', 'Delicious Macarons Cake.', 'Celebration', 250, TRUE, 'https://images.unsplash.com/...'),
('Raspberry Layer Cake', 'Delicious Raspberry Layer Cake.', 'Classic', 300, TRUE, 'https://plus.unsplash.com/...'),
('Black Forest Cake', 'Delicious Black Forest Cake.', 'Celebration', 350, TRUE, 'https://images.unsplash.com/...');
```

#### Query Patterns

**Find by Category (case-insensitive)**:
```java
List<Cake> findByCategoryIgnoreCase(String category);
```

**Find by Price Range**:
```java
List<Cake> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
```

**Find by Category and Price Range**:
```java
List<Cake> findByCategoryIgnoreCaseAndPriceBetween(String category, BigDecimal minPrice, BigDecimal maxPrice);
```

---

## Order Database (order_db)

**Service**: Order Service (Port 8083)  
**Purpose**: Manage shopping basket and completed orders  
**Entities**: 3  
**Tables**: 3

---

### Entity: BasketItem

**Java Class**: `com.cakedelight.order.entity.BasketItem`  
**Table Name**: `basket_items`  
**Primary Key**: `id` (auto-generated)

#### Table Schema: basket_items

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique basket item identifier |
| `cake_id` | BIGINT | NOT NULL | Reference to cake in catalog service |
| `cake_name` | VARCHAR(255) | NOT NULL | Cached cake name for display |
| `price` | NUMERIC(19,2) | NOT NULL, CHECK (price > 0) | Cached cake price at time of adding |
| `quantity` | INTEGER | NOT NULL, CHECK (quantity > 0) | Number of cakes in basket |

#### SQL DDL

```sql
CREATE TABLE basket_items (
    id BIGSERIAL PRIMARY KEY,
    cake_id BIGINT NOT NULL,
    cake_name VARCHAR(255) NOT NULL,
    price NUMERIC(19,2) NOT NULL CHECK (price > 0),
    quantity INTEGER NOT NULL CHECK (quantity > 0)
);

CREATE INDEX idx_basket_items_cake_id ON basket_items(cake_id);
```

#### Validation Rules

- `cakeId`: Required
- `cakeName`: Required, cannot be blank
- `price`: Required, must be positive
- `quantity`: Required, must be positive

#### Business Logic

- When adding a cake that already exists in the basket, quantity is incremented by 1
- Basket is cleared after successful checkout
- No persistent user association (simplified for demo)

---

### Entity: CustomerOrder

**Java Class**: `com.cakedelight.order.entity.CustomerOrder`  
**Table Name**: `customer_orders`  
**Primary Key**: `id` (auto-generated)

#### Table Schema: customer_orders

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique order identifier |
| `total_amount` | NUMERIC(19,2) | NOT NULL | Total order amount (sum of all items) |
| `status` | VARCHAR(255) | DEFAULT 'COMPLETED' | Order status |
| `created_at` | TIMESTAMP | NOT NULL | Order creation timestamp |

#### SQL DDL

```sql
CREATE TABLE customer_orders (
    id BIGSERIAL PRIMARY KEY,
    total_amount NUMERIC(19,2) NOT NULL,
    status VARCHAR(255) DEFAULT 'COMPLETED',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_customer_orders_created_at ON customer_orders(created_at DESC);
CREATE INDEX idx_customer_orders_status ON customer_orders(status);
```

#### Business Logic

- `createdAt` is automatically set to current timestamp on insert (`@PrePersist`)
- `status` defaults to "COMPLETED" (all orders are immediately completed)
- `totalAmount` is calculated from basket items during checkout
- Order creation triggers a RabbitMQ event publication

---

### Entity: OrderItem

**Java Class**: `com.cakedelight.order.entity.OrderItem`  
**Table Name**: `order_items`  
**Primary Key**: `id` (auto-generated)

#### Table Schema: order_items

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique order item identifier |
| `order_id` | BIGINT | NOT NULL | Reference to parent customer order |
| `cake_id` | BIGINT | NOT NULL | Reference to cake in catalog service |
| `cake_name` | VARCHAR(255) | NOT NULL | Cached cake name at time of order |
| `price` | NUMERIC(19,2) | NOT NULL, CHECK (price > 0) | Cached cake price at time of order |
| `quantity` | INTEGER | NOT NULL, CHECK (quantity > 0) | Quantity ordered |

#### SQL DDL

```sql
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    cake_id BIGINT NOT NULL,
    cake_name VARCHAR(255) NOT NULL,
    price NUMERIC(19,2) NOT NULL CHECK (price > 0),
    quantity INTEGER NOT NULL CHECK (quantity > 0)
);

CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_cake_id ON order_items(cake_id);
```

#### Relationships

**Logical Relationship** (not enforced via foreign key):
- `order_id` references `customer_orders.id`
- One CustomerOrder has many OrderItems (1:N relationship)

#### Query Patterns

**Find by Order ID**:
```java
List<OrderItem> findByOrderId(Long orderId);
```

---

## Rating Database (rating_db)

**Service**: Rating Service (Port 8084)  
**Purpose**: Store customer ratings and reviews for cakes  
**Entities**: 1  
**Tables**: 1

### Entity: Rating

**Java Class**: `com.cakedelight.rating.entity.Rating`  
**Table Name**: `ratings`  
**Primary Key**: `id` (auto-generated)

#### Table Schema: ratings

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique rating identifier |
| `cake_id` | BIGINT | NOT NULL | Reference to cake in catalog service |
| `rating_value` | INTEGER | NOT NULL, CHECK (rating_value BETWEEN 1 AND 5) | Star rating (1-5) |
| `review` | VARCHAR(1000) | NULL | Optional review text |
| `created_at` | TIMESTAMP | NOT NULL | Rating creation timestamp |

#### SQL DDL

```sql
CREATE TABLE ratings (
    id BIGSERIAL PRIMARY KEY,
    cake_id BIGINT NOT NULL,
    rating_value INTEGER NOT NULL CHECK (rating_value BETWEEN 1 AND 5),
    review VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_ratings_cake_id ON ratings(cake_id);
CREATE INDEX idx_ratings_created_at ON ratings(created_at DESC);
```

#### Validation Rules

- `cakeId`: Required
- `ratingValue`: Required, must be between 1 and 5 (inclusive)
- `review`: Optional, max length 1000 characters
- `createdAt`: Automatically set on insert

#### Query Patterns

**Find by Cake ID**:
```java
List<Rating> findByCakeId(Long cakeId);
```

**Calculate Average Rating**:
```java
// Business logic in service layer
List<Rating> ratings = repository.findByCakeId(cakeId);
OptionalDouble avg = ratings.stream()
    .mapToInt(Rating::getRatingValue)
    .average();
```

#### Sample Data

```sql
INSERT INTO ratings (cake_id, rating_value, review, created_at) VALUES
(1, 5, 'Absolutely delicious! Best cake ever.', '2026-08-13 10:30:00'),
(1, 4, 'Very good, slightly too sweet.', '2026-08-13 11:15:00'),
(2, 5, 'Perfect for celebrations!', '2026-08-13 12:00:00');
```

---

## Notification Database (notification_db)

**Service**: Notification Service (Port 8085)  
**Purpose**: Store notifications triggered by order completion events  
**Entities**: 1  
**Tables**: 1

### Entity: Notification

**Java Class**: `com.cakedelight.notification.entity.Notification`  
**Table Name**: `notifications`  
**Primary Key**: `id` (auto-generated)

#### Table Schema: notifications

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique notification identifier |
| `order_id` | BIGINT | NULL | Reference to order that triggered notification |
| `message` | VARCHAR(1000) | NULL | Notification message text |
| `status` | VARCHAR(255) | NULL | Notification status (e.g., "SENT") |
| `created_at` | TIMESTAMP | NOT NULL | Notification creation timestamp |

#### SQL DDL

```sql
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT,
    message VARCHAR(1000),
    status VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_notifications_order_id ON notifications(order_id);
CREATE INDEX idx_notifications_created_at ON notifications(created_at DESC);
CREATE INDEX idx_notifications_status ON notifications(status);
```

#### Event-Driven Creation

Notifications are created automatically when:
1. Order Service publishes `OrderCompletedEvent` to RabbitMQ
2. Notification Service consumes event from `notification.queue`
3. Notification Service creates notification record with:
   - `orderId`: From event
   - `message`: Formatted as "Order {orderId} completed with amount {totalAmount}"
   - `status`: Set to "SENT"
   - `createdAt`: Auto-generated timestamp

#### Sample Data

```sql
INSERT INTO notifications (order_id, message, status, created_at) VALUES
(1, 'Order 1 completed with amount 850', 'SENT', '2026-08-13 10:30:05'),
(2, 'Order 2 completed with amount 450', 'SENT', '2026-08-13 11:20:10');
```

---

## Entity Relationships

### Logical Relationships (Cross-Service References)

Since each service owns its database, foreign key constraints are **not enforced at the database level**. Instead, relationships are maintained at the application level through service references:

```text
┌──────────────────────────────────────────────────────────────────┐
│                      LOGICAL RELATIONSHIPS                        │
│                    (No FK Constraints Enforced)                   │
└──────────────────────────────────────────────────────────────────┘

catalog_db.cakes
      │
      │ Referenced by (via cake_id)
      │
      ├──────────────┐
      ▼              ▼
order_db.basket_items    rating_db.ratings
order_db.order_items
      │
      │
      ▼
order_db.customer_orders ────────> notification_db.notifications
      (via RabbitMQ event)           (via order_id reference)
```

### Within-Service Relationships

**Order Service Internal Relationship**:

```text
customer_orders (1) ──────< (N) order_items
      ↑                            ↑
      │ order_id                   │ order_id
      │                            │
      └────────────────────────────┘
```

- One `customer_orders` record has many `order_items` records
- Relationship maintained via `order_id` field
- No foreign key constraint (could be added but not required)

### Data Duplication Strategy

To maintain service autonomy, data is duplicated across services:

| Original Data | Owned By | Duplicated In | Reason |
|---------------|----------|---------------|--------|
| `cake.id` | Catalog Service | Order Service (basket_items, order_items) | Reference product |
| `cake.id` | Catalog Service | Rating Service (ratings) | Associate rating with product |
| `cake.name` | Catalog Service | Order Service (basket_items, order_items) | Display without cross-service call |
| `cake.price` | Catalog Service | Order Service (basket_items, order_items) | Historical pricing (price at time of order) |
| `order.id` | Order Service | Notification Service (notifications) | Reference order that triggered notification |
| `order.totalAmount` | Order Service | Notification Service (via event message) | Display in notification |

This duplication provides:
- **Performance**: No cross-service queries needed
- **Resilience**: Services function independently
- **Historical Accuracy**: Order prices reflect the price at purchase time, not current price

---

## Data Types Reference

### JPA to PostgreSQL Type Mapping

| Java Type | JPA Annotation | PostgreSQL Type | Size/Precision |
|-----------|----------------|-----------------|----------------|
| `Long` | `@Id @GeneratedValue` | `BIGSERIAL` | 8 bytes, auto-increment |
| `String` | `@Column` | `VARCHAR(255)` | Default 255 chars |
| `String` | `@Column(length=1000)` | `VARCHAR(1000)` | Custom length |
| `BigDecimal` | `@Column` | `NUMERIC(19,2)` | 19 digits, 2 decimal places |
| `Integer` | `@Column` | `INTEGER` | 4 bytes |
| `Boolean` | `@Column` | `BOOLEAN` | TRUE/FALSE |
| `LocalDateTime` | `@Column` | `TIMESTAMP` | Date and time without timezone |

### Column Naming Convention

Hibernate automatically converts Java camelCase to PostgreSQL snake_case:

| Java Field | Database Column |
|------------|-----------------|
| `cakeId` | `cake_id` |
| `cakeName` | `cake_name` |
| `ratingValue` | `rating_value` |
| `imageUrl` | `image_url` |
| `totalAmount` | `total_amount` |
| `createdAt` | `created_at` |

---

## Hibernate Configuration

### JPA Settings (Common Across All Services)

```properties
# Auto-create/update schema based on entities
spring.jpa.hibernate.ddl-auto=update

# PostgreSQL dialect
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# SQL logging (disabled in production)
spring.jpa.show-sql=false

# Format SQL for readability (when logging enabled)
spring.jpa.properties.hibernate.format_sql=true
```

### DDL Auto Modes

| Mode | Behavior | Use Case |
|------|----------|----------|
| `update` | Updates schema, preserves data | **Used in Cake Delight** - Development/Demo |
| `create` | Drops and recreates schema | Testing only |
| `create-drop` | Creates on startup, drops on shutdown | Integration tests |
| `validate` | Validates schema without changes | Production with managed migrations |
| `none` | No schema management | Production with Flyway/Liquibase |

**Current Configuration**: `spring.jpa.hibernate.ddl-auto=update`

This setting:
- ✅ Automatically creates tables on first startup
- ✅ Adds new columns when entities are modified
- ✅ Preserves existing data
- ⚠️ Does not remove columns (requires manual migration)
- ⚠️ Not recommended for production (use Flyway/Liquibase instead)

---

## Database Summary

### Overview Table

| Database | Service | Port | Tables | Total Columns | Indexes |
|----------|---------|------|--------|---------------|---------|
| `catalog_db` | Catalog Service | 8082 | 1 | 7 | 2 |
| `order_db` | Order Service | 8083 | 3 | 18 | 5 |
| `rating_db` | Rating Service | 8084 | 1 | 5 | 2 |
| `notification_db` | Notification Service | 8085 | 1 | 5 | 3 |
| **TOTAL** | **4 Services** | - | **6** | **35** | **12** |

### Entity Summary

| Entity | Table | Primary Key | Columns | Service |
|--------|-------|-------------|---------|---------|
| Cake | cakes | id (BIGSERIAL) | 7 | Catalog |
| BasketItem | basket_items | id (BIGSERIAL) | 5 | Order |
| CustomerOrder | customer_orders | id (BIGSERIAL) | 4 | Order |
| OrderItem | order_items | id (BIGSERIAL) | 6 | Order |
| Rating | ratings | id (BIGSERIAL) | 5 | Rating |
| Notification | notifications | id (BIGSERIAL) | 5 | Notification |

### Storage Requirements (Estimated)

| Database | Rows (Sample) | Estimated Size | Growth Pattern |
|----------|---------------|----------------|----------------|
| `catalog_db` | 20 cakes | <1 MB | Slow (new products) |
| `order_db` | Variable | Moderate | High (per order) |
| `rating_db` | Variable | Moderate | Medium (per rating) |
| `notification_db` | Variable | Moderate | High (per order) |

---

## Kubernetes Persistent Volumes

Each database is deployed with a dedicated PersistentVolumeClaim:

```yaml
# Example: catalog-db-pvc.yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: catalog-db-pvc
  namespace: cake-delight
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 1Gi
```

**PVC Configuration**:
- **Access Mode**: ReadWriteOnce (single node mounting)
- **Storage**: 1Gi per database
- **StorageClass**: Default (Minikube hostPath)
- **Reclaim Policy**: Retain (data persists after pod deletion)

---

## Connection Pooling

Spring Boot uses HikariCP (default connection pool) with optimal settings:

```properties
# Default HikariCP configuration (auto-configured)
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
```

---

## Database Initialization

### Data Seeding

**Catalog Service** includes a `DataSeeder` component that populates the catalog with 20 sample cakes on first startup:

```java
@Component
public class DataSeeder implements CommandLineRunner {
    @Override
    public void run(String... args) {
        if (cakeRepository.count() > 0) return; // Skip if data exists
        
        // Seed 20 sample cakes
        cakeRepository.saveAll(sampleCakes);
    }
}
```

**Other services** start with empty databases and populate via API calls and events.

---

## Security Considerations

### Database Credentials

Credentials are managed via:
- **Kubernetes Secrets**: `database-credentials` secret in cluster
- **Environment Variables**: Configurable per environment
- **Default Credentials**: `postgres`/`postgres` for local development

```yaml
# kubernetes/secrets.yaml
apiVersion: v1
kind: Secret
metadata:
  name: database-credentials
type: Opaque
data:
  DB_USERNAME: <base64-encoded>
  DB_PASSWORD: <base64-encoded>
```

### Best Practices Implemented

- ✅ Each service has dedicated database
- ✅ No shared database connections
- ✅ Credentials stored in Kubernetes Secrets
- ✅ Connection pooling with HikariCP
- ✅ Prepared statements (JPA default)
- ⚠️ Database-level encryption not implemented (consider for production)
- ⚠️ Audit logging not implemented (consider for production)

---

**Last Updated**: 2026-08-13  
**Schema Version**: 1.0.0  
**PostgreSQL Version**: 15  
**Hibernate Version**: 6.x (Spring Boot 3.x)
