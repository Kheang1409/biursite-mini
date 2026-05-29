## BiUrSite — README

BiUrSite is a Spring Boot web application with server-rendered Thymeleaf pages and JSON API endpoints for posts and user management.

Key facts

- Java: 25
- Spring Boot: 3.5.11
- Template engine: Thymeleaf
- Database: PostgreSQL (docker-compose service included)
- Build: Maven

Quick start (recommended - Docker)

Prerequisites: Docker Desktop.

```bash
# build and start app+db
docker compose up --build -d
# visit http://localhost:8080
```

`docker compose` now ships with safe local defaults, so it can start without a local `.env` file. Copy `.env.example` to `.env` only if you want to override those defaults or run against your own database.

Cross-platform copy examples:

```bash
# macOS / Linux
cp .env.example .env
```

```powershell
# Windows PowerShell
Copy-Item .env.example .env
```

Run locally (without Docker)

1. Ensure a Postgres instance is available and configure the database environment variables used by `src/main/resources/application.yml`.
2. Build and run the app:

```bash
mvn clean package -DskipTests
java -jar target/biursite-java-0.0.1-SNAPSHOT.jar
# or during development
mvn spring-boot:run
```

Important configuration

- `app.jwt.secret` — JWT secret used by `JwtUtil`. Provide via env `JWT_SECRET` (min 32 characters).
- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD` — database connection used by `application.yml`.
- `SPRING_DATASOURCE_URL` — optional override if you want to provide a full JDBC URL directly.
- `SERVER_PORT` — overrides `server.port`.

Setting environment variables (examples):

```bash
# macOS / Linux
export JWT_SECRET='your-32-char-secret'
export SPRING_DATASOURCE_URL='jdbc:postgresql://localhost:5432/biursite'
```

```powershell
# Windows PowerShell
$env:JWT_SECRET='your-32-char-secret'
$env:SPRING_DATASOURCE_URL='jdbc:postgresql://localhost:5432/biursite'
```

Docker compose summary

- `db` — Postgres 15 with local compose defaults (`biursite` / `biursite-pass`).
- `app` — application image; uses the local `db` service and `SPRING_PROFILES_ACTIVE=dev` by default.

Project layout (high level)

- `src/main/java/com/biursite` — application code
- `src/main/java/com/biursite/application` — use cases, queries, DTOs, and ports
- `src/main/java/com/biursite/domain` — domain entities, value objects, events, and repository ports
- `src/main/java/com/biursite/infrastructure` — controllers, persistence, security, projections, and adapters
- `src/main/java/com/biursite/config` — application configuration, security, error handling, and web setup
- `src/main/resources/templates` — Thymeleaf templates and fragments
- `src/main/resources/static` — CSS, images, JS
- `src/test` — tests

Thymeleaf fragments and layout

Use small, focused fragments for shared pieces:

- `fragments/head.html` — meta tags, Font Awesome, Tailwind CDN, theme-init script, and common CSS.
- `fragments/navbar.html` — navigation, auth links, and theme toggle.
- `fragments/footer.html` — footer.

`fragments/layout.html` remains in the repo as a delegating wrapper for backward compatibility; the page templates currently replace `head`, `navbar`, and `footer` directly.

Example (Thymeleaf):

```html
<th:block
  th:replace="~{fragments/head :: head(title=${'My Page Title'})}"
></th:block>
<th:block th:replace="~{fragments/navbar :: navbar}"></th:block>
<!-- page content -->
<th:block th:replace="~{fragments/footer :: footer}"></th:block>
```

Testing

Run all tests:

```bash
mvn test
```

API response format

The API uses two response styles:

- Auth and user-management endpoints return the standard `ApiResponse` envelope.
- Post list/detail/create/update endpoints return raw `PostView` JSON values, while delete returns `204 No Content`.

Example envelope response:

```json
{
  "success": true,
  "status": 200,
  "error": null,
  "message": "Posts retrieved",
  "path": "/api/posts",
  "timestamp": "2026-05-26T10:15:30Z",
  "data": [],
  "meta": {
    "pagination": {
      "page": 0,
      "size": 20,
      "totalElements": 120,
      "totalPages": 6,
      "hasNext": true,
      "hasPrevious": false
    }
  }
}
```

Error responses set `success=false` and include `error` and `message`. Validation errors include `meta.errors` with field-level messages.

Production & CI/CD

- Build and run full verification (tests + package):

```bash
mvn -DskipTests=false clean verify
```

- Build the jar:

```bash
mvn -DskipTests=false clean package
```

- Build Docker image (locally):

```bash
docker build -t biursite:latest .
```

- Run with Docker (compose is provided):

```bash
docker compose up --build -d
```

Required environment variables (production)

- `JWT_SECRET` : JWT secret (no default) — provide as env; recommend >=32 chars for HS256.
- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD` : database connection.
- `SERVER_PORT` : optional, default 8080.
- `LOG_LEVEL`, `LOG_LEVEL_APP` : optional logging levels.

CI/CD

- A GitHub Actions workflow is included at `.github/workflows/build.yml` — it runs `mvn verify`, a dependency scan, and a Docker build on push and PR to `production`.

Notes

- No production secrets are committed. `application.yml` relies on environment variables and does not include default production secrets.
- Logs are emitted as structured JSON to stdout in production via `logback-spring.xml`.
- Actuator endpoints are available at `/actuator/health` and `/actuator/metrics`.
- Requests can include `X-Correlation-ID` to correlate logs; the server will generate one if missing.

Docs and internals

See `docs/PROJECT_DOCUMENTATION.md` for a consolidated overview and `docs/archive/` for internal reference docs (security internals, API internals, templates details).

Contributing

1. Fork the repo
2. Create a feature branch
3. Run tests locally
4. Open a PR with a clear description
