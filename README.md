# Cake Delight

Cake Delight is a cloud-native cake ordering application built as a Spring Boot microservices system with a React frontend. It demonstrates service separation, API gateway routing, asynchronous event-driven communication, database-per-service persistence, containerization, and deployment to Kubernetes with Minikube.

## Architecture

```text
Browser
  |
  v
React Frontend (NodePort)
  |
  v
API Gateway (NodePort / Spring Cloud Gateway)
  |-------------------> Catalog Service ----> catalog-db (PostgreSQL)
  |-------------------> Order Service ------> order-db (PostgreSQL)
  |                         |
  |                         v
  |                      RabbitMQ
  |                         |
  |                         v
  |-------------------> Notification Service -> notification-db (PostgreSQL)
  |
  \-------------------> Rating Service -----> rating-db (PostgreSQL)
```

Each backend service is independently deployable and owns its own PostgreSQL database. The Order Service publishes completed-order events to RabbitMQ; the Notification Service consumes those events and stores notifications.

## Microservices

| Component | Responsibility | Default Port |
|---|---|---:|
| Frontend | React user interface for catalog, basket, checkout, ratings, and notifications | 80 |
| API Gateway | Single entry point and route forwarding for backend APIs | 8080 |
| Catalog Service | Cake catalog management and browsing | 8082 |
| Order Service | Basket management, checkout, order persistence, and event publishing | 8083 |
| Rating Service | Cake ratings and averages | 8084 |
| Notification Service | Consumes order events and exposes notifications | 8085 |
| RabbitMQ | Event broker between Order and Notification services | 5672 |

## Tech Stack

| Area | Technologies |
|---|---|
| Backend | Java 21, Spring Boot 3.x, Maven |
| Gateway | Spring Cloud Gateway |
| Frontend | React, Vite, Axios |
| Data | PostgreSQL, Spring Data JPA, Hibernate |
| Messaging | RabbitMQ, Spring AMQP |
| Containers | Docker, multi-stage Dockerfiles |
| Orchestration | Kubernetes, Minikube |
| Deployment | Docker Hub images, Kubernetes Deployments, Services, PVCs, ConfigMaps, Secrets |

## Folder Structure

```text
cake-delight/
├── api-gateway/              # Spring Cloud Gateway
├── catalog-service/          # Cake catalog microservice
├── order-service/            # Basket, checkout, and order-event publisher
├── rating-service/           # Rating microservice
├── notification-service/     # Notification-event consumer
├── frontend/                 # React application
├── kubernetes/               # Namespace, configuration, databases, services, and deployments
├── docker-compose.yml        # Local multi-container environment
└── README.md
```

## Features

- Browse the cake catalog.
- Add cakes to a basket and adjust quantities.
- Checkout orders through the Order Service.
- Publish order-completed events through RabbitMQ.
- Consume order events and display notifications.
- Create and view cake ratings.
- Route all backend API requests through a single API Gateway.
- Run locally with Docker Compose or deploy to Kubernetes.

## API Gateway Routing

The frontend communicates with the API Gateway. The gateway forwards requests to internal Kubernetes services:

| Frontend path | Target service |
|---|---|
| `/api/catalog/**` | Catalog Service |
| `/api/orders/**` | Order Service |
| `/api/ratings/**` | Rating Service |
| `/api/notifications/**` | Notification Service |

For example, `GET /api/catalog/cakes` is forwarded by the gateway to the Catalog Service `GET /cakes` endpoint.

## Dockerization

Each application has a multi-stage Dockerfile:

1. Build the Spring Boot JAR or React static bundle.
2. Copy only the runtime artifact into a smaller final image.

Build a service image locally:

```bash
docker build -t akashgupta2233/cake-delight-order-service:v1.0.1 ./order-service
```

Run the full local environment:

```bash
docker compose up --build
```

## Docker Hub Images

| Component | Docker Hub |
|---|---|
| Frontend | [akashgupta2233/cake-delight-frontend](https://hub.docker.com/r/akashgupta2233/cake-delight-frontend) |
| API Gateway | [akashgupta2233/cake-delight-api-gateway](https://hub.docker.com/r/akashgupta2233/cake-delight-api-gateway) |
| Catalog Service | [akashgupta2233/cake-delight-catalog-service](https://hub.docker.com/r/akashgupta2233/cake-delight-catalog-service) |
| Order Service | [akashgupta2233/cake-delight-order-service](https://hub.docker.com/r/akashgupta2233/cake-delight-order-service) |
| Rating Service | [akashgupta2233/cake-delight-rating-service](https://hub.docker.com/r/akashgupta2233/cake-delight-rating-service) |
| Notification Service | [akashgupta2233/cake-delight-notification-service](https://hub.docker.com/r/akashgupta2233/cake-delight-notification-service) |

## Kubernetes Deployment

The Kubernetes manifests deploy to the `cake-delight` namespace and include:

- ConfigMaps for service and database host configuration.
- Secrets for database and RabbitMQ credentials.
- One PostgreSQL Deployment, ClusterIP Service, and 1Gi PersistentVolumeClaim per backend service.
- RabbitMQ Deployment and ClusterIP Service.
- Deployments and internal ClusterIP Services for all application components.
- NodePort Services for external frontend and API Gateway access.

### Prerequisites

- Docker
- Minikube
- kubectl

Start Minikube:

```bash
minikube start
```

### Apply Order

```bash
kubectl apply -f kubernetes/namespace.yaml
kubectl apply -f kubernetes/configmap.yaml
kubectl apply -f kubernetes/secrets.yaml

kubectl apply -f kubernetes/catalog-db-pvc.yaml -f kubernetes/order-db-pvc.yaml -f kubernetes/rating-db-pvc.yaml -f kubernetes/notification-db-pvc.yaml

kubectl apply -f kubernetes/catalog-db-deployment.yaml -f kubernetes/catalog-db-service.yaml
kubectl apply -f kubernetes/order-db-deployment.yaml -f kubernetes/order-db-service.yaml
kubectl apply -f kubernetes/rating-db-deployment.yaml -f kubernetes/rating-db-service.yaml
kubectl apply -f kubernetes/notification-db-deployment.yaml -f kubernetes/notification-db-service.yaml

kubectl apply -f kubernetes/rabbitmq-deployment.yaml -f kubernetes/rabbitmq-service.yaml

kubectl apply -f kubernetes/catalog-service-deployment.yaml -f kubernetes/catalog-service.yaml
kubectl apply -f kubernetes/order-service-deployment.yaml -f kubernetes/order-service.yaml
kubectl apply -f kubernetes/rating-service-deployment.yaml -f kubernetes/rating-service.yaml
kubectl apply -f kubernetes/notification-service-deployment.yaml -f kubernetes/notification-service.yaml
kubectl apply -f kubernetes/api-gateway-deployment.yaml -f kubernetes/api-gateway.yaml
kubectl apply -f kubernetes/frontend-deployment.yaml -f kubernetes/frontend-service.yaml

kubectl apply -f kubernetes/api-gateway-nodeport.yaml
kubectl apply -f kubernetes/frontend-nodeport.yaml
```

### Verify Deployment

```bash
kubectl get pods,pvc,svc -n cake-delight

kubectl rollout status deployment/catalog-service -n cake-delight
kubectl rollout status deployment/order-service -n cake-delight
kubectl rollout status deployment/rating-service -n cake-delight
kubectl rollout status deployment/notification-service -n cake-delight
kubectl rollout status deployment/api-gateway -n cake-delight
kubectl rollout status deployment/frontend -n cake-delight
```

### Access the Application

```bash
minikube ip
```

Use the returned IP address:

| Endpoint | URL |
|---|---|
| Frontend | `http://<minikube-ip>:30081` |
| API Gateway | `http://<minikube-ip>:30080` |

## Deployment Proof

Use the following commands to collect deployment evidence:

```bash
kubectl get deployments -n cake-delight
kubectl get pods -n cake-delight
kubectl get pvc -n cake-delight
kubectl get svc -n cake-delight
kubectl get endpoints -n cake-delight
kubectl logs deployment/order-service -n cake-delight
kubectl logs deployment/notification-service -n cake-delight
```

Successful deployment evidence should show:

- All Deployments at `1/1` ready.
- All Pods in `Running` state.
- All database PVCs in `Bound` state.
- Internal services with endpoints.
- Frontend and API Gateway NodePort services available.
- Order events in Order Service logs and corresponding consumption in Notification Service logs.

## Screenshots

Add project screenshots to an `images/` directory and replace these placeholders.

| Screen | Placeholder |
|---|---|
| Catalog | `![Catalog](images/catalog.png)` |
| Basket | `![Basket](images/basket.png)` |
| Checkout | `![Checkout](images/checkout.png)` |
| Notifications | `![Notifications](images/notifications.png)` |
| Kubernetes Pods | `![Kubernetes Pods](images/kubernetes-pods.png)` |

## Future Enhancements

- Add user authentication and authorization.
- Add an Ingress controller with TLS for production-style routing.
- Add observability with centralized logs, metrics, and distributed tracing.
- Add database migrations with Flyway or Liquibase.
- Add CI/CD pipelines for image builds, tests, and Kubernetes deployment.
- Add autoscaling and production resource tuning.

## Learning Outcomes

- Designing a database-per-service microservices architecture.
- Building synchronous REST communication through an API Gateway.
- Implementing asynchronous communication with RabbitMQ.
- Containerizing Java and React applications with multi-stage Docker builds.
- Managing application configuration with Kubernetes ConfigMaps and Secrets.
- Deploying stateful PostgreSQL workloads with PersistentVolumeClaims.
- Diagnosing service-discovery, networking, CORS, and deployment issues in Minikube.

## Conclusion

Cake Delight demonstrates an end-to-end cloud-native microservices workflow: independently deployable Spring Boot services, asynchronous messaging, isolated data stores, a React frontend, Docker image delivery, and Kubernetes orchestration. It provides a practical foundation for understanding how modern distributed applications are designed, deployed, and operated.
