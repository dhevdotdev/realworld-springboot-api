# RealWorld Spring Boot API

Backend implementation of the [RealWorld](https://github.com/realworld-apps/realworld)
spec using Spring Boot 3, Java 21, PostgreSQL, Flyway, Docker, and Hurl.

Docs:
- `realworld.dhev.dev`
- `realworld-api-reference.pages.dev`

## Stack

- Java 21
- Spring Boot 3
- PostgreSQL
- Flyway
- Docker Compose
- Hurl
- OpenAPI
- Scalar

## Included

- Auth, profiles, articles, comments, tags, favorites, and feed
- Local Docker stack with PostgreSQL
- Hurl verification suite
- Static docs site for Cloudflare Pages

## Local

```sh
docker compose up --build -d postgres app
./scripts/run-hurl.sh
./scripts/export-openapi.sh
docker compose down
docker compose down -v
```

## CI

- `ci.yml` runs Gradle tests, boots the Docker stack, runs the Hurl suite, and exports OpenAPI
- `deploy-docs.yml` deploys `docs-site/` to Cloudflare Pages

## Deploy

Minimal environment:

```text
SPRING_DATASOURCE_URL=jdbc:postgresql://<host>/<database>?sslmode=require
SPRING_DATASOURCE_USERNAME=<username>
SPRING_DATASOURCE_PASSWORD=<password>
JWT_SECRET=<base64-encoded-strong-secret>
```
