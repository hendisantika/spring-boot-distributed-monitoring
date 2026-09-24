# spring-boot-distributed-monitoring

[![Java CI with Maven](https://github.com/hendisantika/spring-boot-distributed-monitoring/actions/workflows/maven.yml/badge.svg)](https://github.com/hendisantika/spring-boot-distributed-monitoring/actions/workflows/maven.yml)

A sample Spring Boot service for distributed monitoring. It collects traces with OpenTelemetry/Micrometer Tracing,
metrics with Prometheus, and logs with Loki, and shows all three in Grafana.

## Tech Stack

| Component       | Version / Tool                                             |
|-----------------|------------------------------------------------------------|
| Java            | **25** (Temurin in CI)                                     |
| Spring Boot     | 4.1.x                                                      |
| Build           | Maven (wrapper included: `./mvnw`)                         |
| Database        | MySQL 9.2 (started via Spring Boot Docker Compose support) |
| ORM             | Hibernate 7 / Spring Data JPA                              |
| Tracing         | Micrometer Tracing (Brave), Zipkin reporter → Tempo, OpenTelemetry Spring Boot starter + JDBC instrumentation |
| Metrics         | Micrometer + Prometheus registry, datasource-micrometer    |
| Logs            | Loki (loki-logback-appender)                               |
| API docs        | springdoc-openapi (Swagger UI)                             |

## Prerequisites

- JDK 25
- Docker (for MySQL via `compose.yaml`)

## Running

```shell
./mvnw spring-boot:run
```

Spring Boot Docker Compose support starts MySQL from `compose.yaml` on port `3307` by itself. The app runs at
`http://localhost:8080`.

### Build and test

```shell
./mvnw clean package
```

The context-load test needs a running MySQL on `localhost:3307`. Spring Boot skips Docker Compose support during
tests, so start it first with `docker compose up -d mysql`. If port `3307` or the `mysql` container name is already
in use, run your own MySQL and point the build at it instead:

```shell
SPRING_DOCKER_COMPOSE_ENABLED=false \
SPRING_DATASOURCE_URL='jdbc:mysql://localhost:<port>/distrimo?allowPublicKeyRetrieval=true' \
./mvnw clean package
```

## API

Base path: `/api/users`

| Method | Path                  | Description         |
|--------|-----------------------|---------------------|
| POST   | `/api/users`          | Create a user       |
| GET    | `/api/users`          | List all users      |
| GET    | `/api/users/{email}`  | Get user by email   |
| DELETE | `/api/users/{id}`     | Delete user by UUID |
| GET    | `/api/users/exists/{id}` | Check user exists |

Example:

```shell
curl -X POST http://localhost:8080/api/users \
  -H 'Content-Type: application/json' \
  -d '{"email":"john@example.com","password":"secret","firstName":"John","lastName":"Doe"}'
```

Swagger UI: `http://localhost:8080/swagger-ui.html`

## Observability

- Actuator: `/actuator/health`, `/actuator/info`, `/actuator/metrics`, `/actuator/prometheus`
- Traces are sampled at 100% and sent to Tempo (`http://tempo:9411/api/v2/spans`)
- Logs are pushed to Loki (`LOKI_HOST` / `LOKI_PORT`, default `localhost:3100`)
- Config files for the monitoring stack live in `docker/`:
  - `docker/prometheus/prometheus.yaml`: scrape config
  - `docker/tempo/tempo.yaml`: Tempo config
  - `docker/grafana/datasource.yaml`: Grafana datasources (Prometheus, Tempo, Loki)

## CI

GitHub Actions (`.github/workflows/maven.yml`) builds and tests the project on JDK 25 for every push and pull
request to `main`. MySQL runs as a GitHub Actions service container.
