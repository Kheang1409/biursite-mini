# Docker & Docker Compose

Quick steps to build and run the BiUrSite application with Docker and Docker Compose.

Build the app image (local multi-stage Maven build):

```bash
docker compose build --progress=plain app
```

Start the app and database:

```bash
docker compose up
```

Run in background:

```bash
docker compose up -d
```

Notes:
- App connects to the `db` service using `SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/biursite` (overrides application.yml).
- For production, replace `APP_JWT_SECRET` with a secure secret or use externalized secrets.
- To run Maven tests during build, remove `-DskipTests` from the `Dockerfile` build step.
