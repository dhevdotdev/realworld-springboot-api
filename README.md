# RealWorld Spring Boot API

Backend implementation of the [RealWorld](https://github.com/realworld-apps/realworld)
spec using Spring Boot 3, Java 21, PostgreSQL, Flyway, Docker, and Hurl.

This repository is built against the RealWorld backend contract. It is not
presented here as an official RealWorld listing.

## Stack

- Java 21
- Spring Boot 3
- PostgreSQL
- Flyway
- Docker Compose
- Hurl
- OpenAPI via springdoc
- Scalar API reference

## What is here

- RealWorld backend endpoints for auth, profiles, articles, comments, tags, favorites, and feed
- Docker-based local stack with PostgreSQL
- Hurl verification suite against the running API
- Static docs site artifact for separate publishing

## API docs

The app serves an interactive reference at `/`, but live request execution is
disabled so the page behaves like hosted documentation instead of an API client.

The repository also includes a static docs site in [`docs-site/`](./docs-site)
that can be published separately, for example on `realworld.dhev.dev`.

## Local development

Run the full stack with Docker Compose:

```sh
docker compose up --build -d postgres app
```

The API will be available at `http://localhost:8080` and the app will use the
Compose-managed PostgreSQL container instead of requiring a host-installed
database.

Run the Hurl verification suite against the Compose stack:

```sh
./scripts/run-hurl.sh
```

Export the OpenAPI document for the static docs site:

```sh
./scripts/export-openapi.sh
```

Stop everything:

```sh
docker compose down
```

If you want to remove the local PostgreSQL volume too:

```sh
docker compose down -v
```

## Local build without Docker

The app can still run against any reachable PostgreSQL instance through env vars.
Without overrides, it defaults to `jdbc:postgresql://localhost:5432/realworld`.

```sh
./gradlew bootJar
```

## CI and docs deploy

GitHub Actions includes:

- `ci.yml` to run Gradle tests, boot the Docker stack, run the Hurl suite, and export the OpenAPI document
- `deploy-docs.yml` to deploy `docs-site/` to Cloudflare Pages

To use the docs deploy workflow, set:

- repository variable `CF_PAGES_PROJECT_NAME`
- repository secrets `CLOUDFLARE_API_TOKEN` and `CLOUDFLARE_ACCOUNT_ID`

Custom domain mapping such as `realworld.dhev.dev` is configured in Cloudflare
Pages, not in this repository.

## Deploy

Use a Docker-capable web host and a managed PostgreSQL database.

Minimal environment:

```text
SPRING_DATASOURCE_URL=jdbc:postgresql://<host>/<database>?sslmode=require
SPRING_DATASOURCE_USERNAME=<username>
SPRING_DATASOURCE_PASSWORD=<password>
JWT_SECRET=<base64-encoded-strong-secret>
```

For a quick public demo, use Render Free for the web service and Neon Free for
PostgreSQL. The app reads the platform `PORT` env var automatically.
