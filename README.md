# logistics-events-platform

![CI](https://github.com/josecastrillon/logistics-events-platform/actions/workflows/ci.yml/badge.svg)

An event-driven order and delivery platform built with Java 25 and Spring Boot 4 microservices.

> **Status: work in progress.** Milestone 1 (`order-service` with REST API, PostgreSQL and Docker) is done. Kafka events, the delivery and notification services, and Testcontainers are next — see the [roadmap](#roadmap).

## Architecture (target)

```
                   OrderCreated                      DeliveryAssigned
order-service  ───────────────►  Kafka  ───►  delivery-service  ───────────────►  Kafka  ───►  notification-service
      │                                              │
  PostgreSQL                                     PostgreSQL
```

Each service owns its database. Services communicate asynchronously through Kafka events instead of calling each other directly, so one service being down does not block the others.

## Tech stack

Java 25 · Spring Boot 4 · Spring Data JPA · Flyway · PostgreSQL · Bean Validation · springdoc-openapi · JUnit 5 · Mockito · JaCoCo · Docker Compose · GitHub Actions

## Running locally

**With Docker** (only Docker is required):

```bash
docker compose up --build
```

**From the IDE / Maven** (Java 25 and Maven required):

```bash
docker compose up -d orders-db
mvn -pl order-service spring-boot:run
```

- API docs (Swagger UI): http://localhost:8081/swagger-ui.html
- Health check: http://localhost:8081/actuator/health

### Try it

```bash
curl -i -X POST http://localhost:8081/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerId":"customer-1","deliveryAddress":"Calle 10 #20-30","items":[{"sku":"SKU-1","quantity":2,"unitPrice":10.50}]}'
```

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/orders` | Place an order (returns `201` + `Location`) |
| `GET` | `/api/orders/{id}` | Get an order |
| `GET` | `/api/orders?customerId=...` | List a customer's orders, newest first |
| `POST` | `/api/orders/{id}/cancel` | Cancel an order (`409` if it cannot be cancelled) |

Errors are returned as [RFC 9457 problem details](https://www.rfc-editor.org/rfc/rfc9457).

## Testing

```bash
mvn verify
```

Runs unit tests (domain rules), web-layer tests (`@WebMvcTest`) and integration tests (full Spring context + Flyway migrations). The build fails if line coverage drops below **80%** (JaCoCo).

## Design decisions

- **Rich domain model.** Business rules (total calculation, which state transitions are allowed) live in the `Order` aggregate, not in the service or controller. The service only orchestrates transactions.
- **Order lines as value objects.** `OrderItem` is an immutable Java record mapped as `@Embeddable`: it has no identity of its own and is only reachable through its order.
- **Schema owned by Flyway.** Hibernate runs with `ddl-auto: validate`, so the database schema is versioned in SQL migrations and the app refuses to start if the entities and the schema drift apart.
- **Optimistic locking.** `@Version` on `Order` makes concurrent updates fail fast instead of silently overwriting each other.
- **Injected `Clock`.** Time is a dependency, so tests are deterministic.
- **Separate API DTOs.** Requests and responses are records in the `web` package, so the JPA entity is never exposed directly and the API contract can evolve independently.
- **Open Session in View disabled.** No lazy loading outside transactions.

## Roadmap

- [x] `order-service`: REST API, PostgreSQL, Flyway, Docker Compose, CI with coverage gate
- [ ] Publish `OrderCreated` events to Kafka (transactional outbox)
- [ ] `delivery-service` consuming order events
- [ ] Integration tests with Testcontainers (PostgreSQL + Kafka)
- [ ] Retries, dead letter topic and idempotent consumers
- [ ] `notification-service` + observability (Actuator, structured logs)
- [ ] API gateway and service discovery
- [ ] Container image scanning (Trivy) in CI
- [ ] Angular front end and an MCP server exposing the API to AI assistants
