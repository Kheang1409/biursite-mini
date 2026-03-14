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

Docs and internals

See `docs/PROJECT_DOCUMENTATION.md` for a consolidated overview and `docs/archive/` for internal reference docs (security internals, API internals, templates details).

Contributing

1. Fork the repo
2. Create a feature branch
3. Run tests locally
4. Open a PR with a clear description

License

No license file is included. Add one if you plan to publish this project.
