# Spring Boot Distributed Monitoring

[![Java CI with Maven](https://github.com/hendisantika/spring-boot-distributed-monitoring/actions/workflows/maven.yml/badge.svg)](https://github.com/hendisantika/spring-boot-distributed-monitoring/actions/workflows/maven.yml)

A sample Spring Boot 4 service on **JDK 25** that shows a full observability stack. It collects traces with
OpenTelemetry and Micrometer Tracing (sent to Tempo), metrics with Prometheus, and logs with Loki, and puts all three
together in Grafana.

## Overview

- **Distributed tracing**: Micrometer Tracing (Brave) and the OpenTelemetry Spring Boot starter, exported to Tempo
  through its Zipkin-compatible endpoint
- **Metrics**: Micrometer with a Prometheus registry, scraped by Prometheus
- **Logs**: Loki via `loki-logback-appender`, with trace and span IDs in every log line
- **Dashboards**: Grafana, with Prometheus, Tempo and Loki datasources pre-provisioned
- **Database monitoring**: OpenTelemetry JDBC instrumentation and `datasource-micrometer`

## Tech Stack

| Component     | Version / Tool                                                              |
|---------------|-----------------------------------------------------------------------------|
| Java          | **25** (Eclipse Temurin in CI and in the Docker image)                      |
| Spring Boot   | 4.1.1                                                                       |
| Build         | Maven (wrapper included: `./mvnw`)                                          |
| Database      | MySQL 9.4 (Docker Compose)                                                  |
| ORM           | Hibernate 7.4 / Spring Data JPA                                             |
| Tracing       | Micrometer Tracing (Brave), Zipkin reporter → Tempo 2.6.1                   |
| OpenTelemetry | Spring Boot starter 2.32.0, JDBC instrumentation, SDK 1.66.0                |
| Metrics       | Micrometer + Prometheus registry (Prometheus v3.2.1), datasource-micrometer |
| Logs          | Loki 3.3.2 (loki-logback-appender 2.1.0)                                    |
| Dashboards    | Grafana 11.5.0                                                              |
| API docs      | springdoc-openapi 3.1.1 (Swagger UI)                                        |

> **OpenTelemetry versions:** keep `opentelemetry.version` in `pom.xml` on the SDK version that
> `opentelemetry-spring-boot-starter` is built against (2.32.0 → 1.66.0). Starter 2.32.0 fails to start with SDK
> 1.67.0 (`NoClassDefFoundError: io/opentelemetry/api/internal/ConfigUtil`). `opentelemetry-bom-alpha` is imported so
> the alpha/incubator artifacts always use the same version.

## Architecture

| Component                  | Host port  | Description                         |
|----------------------------|------------|-------------------------------------|
| `distrimo-backend-service` | 8080       | Spring Boot REST API                |
| `mysql`                    | 3308       | Data persistence                    |
| `prometheus`               | 9090       | Metrics collection and storage      |
| `tempo`                    | 3200, 9411 | Trace storage (Zipkin compatible)   |
| `loki`                     | 3100       | Log aggregation                     |
| `grafana`                  | 3000       | Dashboards (anonymous admin access) |

All services share the `monitoring` Docker network.

## Prerequisites

- JDK 25 (for local development)
- Docker with Docker Compose

## Quick Start

### Run everything with Docker Compose

```shell
docker compose up -d --build
```

This builds the app image from the multi-stage `Dockerfile` (`maven:3.9.16-eclipse-temurin-25-alpine` →
`eclipse-temurin:25-jre-alpine`) and starts MySQL, Prometheus, Tempo, Loki, Grafana and the app.

Then check it:

```shell
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/prometheus
docker compose ps
```

### Run the app locally

```shell
./mvnw spring-boot:run
```

Spring Boot Docker Compose support (`spring-boot-docker-compose`) starts the services from `compose.yaml` and connects
the app to MySQL automatically. The app runs at `http://localhost:8080`.

## Accessing the Services

| Service    | URL                                   |
|------------|---------------------------------------|
| API        | http://localhost:8080/api/users       |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| Actuator   | http://localhost:8080/actuator        |
| Prometheus | http://localhost:9090                 |
| Grafana    | http://localhost:3000                 |
| Tempo      | http://localhost:3200                 |
| Loki       | http://localhost:3100                 |

## API

Base path: `/api/users`

| Method | Path                     | Description         |
|--------|--------------------------|---------------------|
| POST   | `/api/users`             | Create a user       |
| GET    | `/api/users`             | List all users      |
| GET    | `/api/users/{email}`     | Get user by email   |
| DELETE | `/api/users/{id}`        | Delete user by UUID |
| GET    | `/api/users/exists/{id}` | Check user exists   |

Example:

```shell
# Create a user
curl -X POST http://localhost:8080/api/users \
  -H 'Content-Type: application/json' \
  -d '{"email":"john@example.com","password":"secret","firstName":"John","lastName":"Doe"}'

# List users
curl http://localhost:8080/api/users

# Get user by email
curl http://localhost:8080/api/users/john@example.com
```

## Observability

- Actuator endpoints: `/actuator/health`, `/actuator/info`, `/actuator/metrics`, `/actuator/prometheus`
- Traces are sampled at 100% (`management.tracing.sampling.probability: 1`) and sent to
  `http://tempo:9411/api/v2/spans`
- Logs are pushed to Loki at `http://${LOKI_HOST:-localhost}:${LOKI_PORT:-3100}/loki/api/v1/push`
- Each log line includes `[application,traceId,spanId]` so logs can be matched to traces
- Prometheus scrapes `distrimo-backend-service:8080/actuator/prometheus`

### Grafana

1. Open http://localhost:3000
2. Go to **Explore** and pick a datasource:
   - **Prometheus**: HTTP, JVM and HikariCP metrics
   - **Tempo**: traces and service graphs
   - **Loki**: application logs linked to traces

### Useful PromQL

```promql
rate(http_server_requests_seconds_count[5m])
jvm_memory_used_bytes
hikaricp_connections_active
```

## Build and Test

```shell
./mvnw clean package
```

The context-load test needs MySQL on `localhost:3307` (see `application.yml`). Spring Boot skips Docker Compose
support during tests, so start your own MySQL first. If port `3307` is already in use, point the build at another
port:

```shell
docker run -d --rm --name distrimo-test-db \
  -e MYSQL_ROOT_PASSWORD=53cret -e MYSQL_DATABASE=distrimo \
  -e MYSQL_USER=yu71 -e MYSQL_PASSWORD=53cret \
  -p 3399:3306 mysql:9.2.0

SPRING_DATASOURCE_URL='jdbc:mysql://localhost:3399/distrimo?useSSL=true&requireSSL=true&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jakarta' \
./mvnw clean package
```

## CI

GitHub Actions (`.github/workflows/maven.yml`) builds and tests the project on **JDK 25 (Temurin)** for every push
and pull request to `main`. MySQL runs as a service container on port `3307`.

## Project Structure

```
spring-boot-distributed-monitoring/
├── src/main/java/id/my/hendisantika/distributedmonitoring/
│   ├── annotation/   # Custom stereotype annotations
│   ├── aspect/       # AOP aspect for repository tracing
│   ├── config/       # Configuration classes
│   ├── controller/   # REST controller and mapper
│   ├── core/         # Business logic
│   ├── dto/          # Data transfer objects
│   └── infra/        # JPA gateway, repository and entity
├── src/main/resources/
│   ├── application.yml
│   └── logback-log4j2.xml   # Loki appender
├── docker/
│   ├── grafana/datasource.yaml      # Grafana datasources
│   ├── prometheus/prometheus.yaml   # Prometheus scrape config
│   └── tempo/tempo.yaml             # Tempo config
├── Dockerfile
├── compose.yaml
└── pom.xml
```

## Troubleshooting

- **App won't start**: `docker logs distrimo-backend-service --tail 100`
- **Database connection issues**: `docker compose ps mysql` and `docker logs mysql`
- **No metrics in Prometheus**: check http://localhost:9090/targets and http://localhost:8080/actuator/prometheus
- **No traces in Tempo**: make sure `tempo` is running and port 9411 can be reached from the app

## Stopping

```shell
docker compose down      # stop all services
docker compose down -v   # also remove the MySQL volume
```

## License

This project is for educational and demonstration purposes.

## Author

Created by hendisantika

- Email: hendisantika@yahoo.co.id
- Telegram: @hendisantika34
- Link: s.id/hendisantika
