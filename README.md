## BiUrSite — README

BiUrSite is a Spring Boot web application (Thymeleaf) that demonstrates a simple posts and user-management workflow used for the Software Engineering course.

Key facts

- Java: 25
- Spring Boot: 3.5.11
- Template engine: Thymeleaf
- Database: PostgreSQL (docker-compose service included)
- Build: Maven

Quick start (recommended — Docker)

Prerequisites: Java 25 JDK, Maven, Docker Desktop.

```bash
# build and start app+db
docker compose up --build -d
# visit http://localhost:8080
```

Note: `docker compose` reads `.env` when present. Copy the example `.env.example` to `.env` before running compose.

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

1. Ensure a Postgres instance is available and configure connection properties or environment variables.
2. Build and run the app:

```bash
mvn clean package -DskipTests
java -jar target/biursite-java-0.0.1-SNAPSHOT.jar
# or during development
mvn spring-boot:run
```

Important configuration

- `app.jwt.secret` — JWT secret used by `JwtUtil`. Provide via env `JWT_SECRET` (min 32 characters).
- `SPRING_DATASOURCE_URL` or `DB_*` env vars — database connection (see `.env.example`).
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

- `db` — Postgres 15 (default database `biursite`); configured via `DB_USERNAME`/`DB_PASSWORD` in `.env.example`.
- `app` — application image; uses `SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/biursite` by default when using compose.

Project layout (high level)

- `src/main/java/com/biursite` — application code
- `src/main/resources/templates` — Thymeleaf templates and fragments
- `src/main/resources/static` — CSS, images, JS
- `src/test` — tests

Thymeleaf fragments and layout

Use small, focused fragments for shared pieces:

- `fragments/head.html` — meta, Tailwind CDN, theme-init script, common CSS.
- `fragments/navbar.html` — navigation and theme toggle.
- `fragments/footer.html` — footer.

`fragments/layout.html` remains in the repo as a delegating wrapper for backward compatibility; prefer using the specific fragments directly (e.g., `th:replace="fragments/head :: head(title=${'My Page'})"`).

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

- A GitHub Actions workflow is included at `.github/workflows/build.yml` — it runs `mvn verify` and builds the Docker image on push and PR to `main`/`master`.

Notes

- No production secrets are committed. `application.yml` relies on environment variables and does not include default production secrets.
- Logs are emitted as structured JSON to stdout in production via `logback-spring.xml`.

Docs and internals

See `docs/PROJECT_DOCUMENTATION.md` for a consolidated overview and `docs/archive/` for internal reference docs (security internals, API internals, templates details).

Contributing

1. Fork the repo
2. Create a feature branch
3. Run tests locally
4. Open a PR with a clear description

License

No license file is included. Add one if you plan to publish this project.
