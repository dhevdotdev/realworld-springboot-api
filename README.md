# RealWorld Spring Boot API

Backend implementation of the [RealWorld](https://github.com/realworld-apps/realworld)
spec using Spring Boot 3, Java 21, PostgreSQL, Flyway, Docker, and Hurl.

Docs: `realworld.dhev.dev`

## Stack

- Java 21
- Spring Boot 3
- PostgreSQL
- Flyway
- Docker Compose
- Hurl
- OpenAPI

## Scope

- Auth
- Profiles
- Articles
- Comments
- Tags
- Favorites
- Feed

## Local

```sh
docker compose up --build -d postgres app
./scripts/run-hurl.sh
docker compose down
```

To reset the local database:

```sh
docker compose down -v
```

## Environment

```text
SPRING_DATASOURCE_URL=jdbc:postgresql://<host>/<database>?sslmode=require
SPRING_DATASOURCE_USERNAME=<username>
SPRING_DATASOURCE_PASSWORD=<password>
JWT_SECRET=<base64-encoded-strong-secret>
```
