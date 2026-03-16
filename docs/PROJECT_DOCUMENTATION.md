# BiUrSite — Project Documentation (Consolidated)

Last updated: 2026-03-15

This consolidated document replaces the multiple top-level docs and contains the essential information developers and stakeholders need: architecture summary, UI overview, database schema, API reference, security highlights, setup & production instructions, and readiness checklist. Full original documents have been archived to `docs/archive/` for reference.

Contents

- Overview
- System Architecture
- UI Layer
- Database Schema (summary)
- REST API (endpoints summary)
- Security (high-level)
- Setup & Run (quickstart)
- Production Readiness Checklist
- Where to find archived, detailed docs

---

## Overview

BiUrSite is a Spring Boot 3.5.11 monolithic web application (Java 25) combining a server-rendered Thymeleaf UI and a JSON REST API. It uses PostgreSQL 15 for persistence and Tailwind (CDN) for styling.

Key points:

- Dual authentication: session-based for web UI; JWT for API.
- Layered architecture: Controllers → Services → Repositories (infrastructure adapters).
- Environment-driven config: all secrets via environment variables (`JWT_SECRET`, `DB_*`).

---

## System Architecture

High-level flow:

- Clients (browser or API client) → Controllers (MVC or REST) → Services → Repositories → PostgreSQL
- Security: two filter chains — API chain (stateless JWT) and MVC chain (session-based).

Design decisions:

- Keep domain logic framework-free where practical; place Spring-specific adapters under `infrastructure`.
- Controllers depend on `service` interfaces only; repositories live under `repository` and are implemented by infrastructure adapters.

Technology summary:

- Java 25, Spring Boot 3.5.11, Thymeleaf, Spring Security, Spring Data JPA (Hibernate 6), PostgreSQL 15, Maven, Docker

---

## UI Layer (summary)

- Thymeleaf templates with common fragments (`head`, `navbar`, `footer`, `layout`) — `fragments/layout` is used for the shared page layout and theme initialization.
- Tailwind CSS (via CDN) + Font Awesome for icons. Dark mode implemented via `dark` class and persisted in `localStorage`.
- Reusable components: post cards, modals (new post), forms with validation. Server-side rendering ensures XSS-safe outputs (Thymeleaf escapes by default).

UX features:

- Responsive layout, theme toggle, modal-based new-post creation, accessible forms (labels, ARIA where needed).

---

## Database Schema (summary)

Core tables:

- `users` (id BIGSERIAL PK, username unique, email unique, password hash, role, created_at)
- `posts` (id BIGSERIAL PK, title, content, author_id FK -> users(id) ON DELETE CASCADE, created_at, updated_at)

Indexes:

- `idx_posts_author_id` on `posts(author_id)`
- `idx_posts_created_at` on `posts(created_at DESC)`
- Unique indexes on `users.username` and `users.email`

Best practices:

- Use JOIN FETCH queries in repositories to prevent N+1 (examples in archive).
- Use a migration tool (Liquibase recommended) for production migrations.

---

## REST API — Endpoints (summary)

Auth:

- POST /api/auth/register → register user (returns JWT)
- POST /api/auth/login → login (returns JWT)

Posts:

- GET /api/posts?page=&size= → list posts (paginated)
- GET /api/posts/{id} → get post
- POST /api/posts → create (requires JWT)
- PUT /api/posts/{id} → update (owner/admin)
- DELETE /api/posts/{id} → delete (owner/admin)

Users:

- GET /api/users → list users (admin)
- GET /api/users/{id} → get user
- PUT /api/users/{id} → update user (self/admin)
- DELETE /api/users/{id} → delete user (admin)

Error format: JSON errors with either an `error` message or field-level validation map. See archived `REST_API_DOCUMENTATION.md` for examples and SDK snippets.

---

## Security (high-level)

- Passwords: BCrypt hashing (strength 10).
- API auth: JWT signed using `io.jsonwebtoken` (jjwt) with an HMAC secret key; `JWT_SECRET` (mapped to `app.jwt.secret`) must be set and ≥32 characters.
- MVC auth: Spring Security form login and HttpSession with secure, HttpOnly cookies.
- CSRF: enabled for MVC; disabled for stateless API endpoints.
- Input validation: Bean Validation (`@Valid`) and Thymeleaf escaping guard against injection/XSS.
- Secrets: keep in environment variables or a managed secret store.

---

## Setup & Run (quickstart)

Prerequisites:

- Java 25 (or Java 21+), Maven 3.9+, Docker (optional)

Quick start (development):

```bash
# copy env template (cross-platform)
# macOS / Linux
cp .env.example .env
# Windows PowerShell
Copy-Item .env.example .env
# set required env vars in .env or shell
# build (skip tests for fast local run if desired)
mvn -DskipTests clean package
# run with Docker Compose
docker compose up -d --build
# or run locally
mvn spring-boot:run
```

Environment variables to set for production:

- `JWT_SECRET` (required, ≥32 chars) — mapped to Spring property `app.jwt.secret` used by `JwtUtil`.

See implementation details in the archived internals:

- `docs/archive/API_INTERNALS.md` — REST controller endpoints and examples.
- `docs/archive/SECURITY_INTERNALS.md` — JWT and security helper internals (`JwtUtil`, `JwtFilter`, `SecurityService`).
- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`
- `SERVER_PORT`, `LOG_LEVEL`

Troubleshooting tips: check logs (`docker logs` or `mvn spring-boot:run`), verify DB health (pg_isready), ensure `JWT_SECRET` meets length requirement.

---

## Production Readiness Checklist (brief)

- [ ] `mvn clean package` builds successfully (CI runs full tests)
- [ ] All tests pass (`mvn test`) — ArchUnit tests run in CI
- [ ] `JWT_SECRET` and DB credentials provided via environment
- [ ] Database migrations configured (Liquibase)
- [ ] HTTPS configured (SSL certs and `server.ssl.*`)
- [ ] Logging/monitoring/alerts in place (metrics, error tracking)
- [ ] Backup strategy for DB
- [ ] Rate limiting on auth endpoints (recommended)

---

## Archived full documents

Detailed originals have been moved to `docs/archive/` (read for full examples, diagrams, and extended guidance):

- `docs/archive/HIGH_LEVEL_SYSTEM_ARCHITECTURE.md`
- `docs/archive/ARCHITECTURE.md`
- `docs/archive/UI_ARCHITECTURE.md`
- `docs/archive/DATABASE_SCHEMA.md`
- `docs/archive/REST_API_DOCUMENTATION.md`
- `docs/archive/SECURITY_ARCHITECTURE.md`
- `docs/archive/SETUP_AND_RUN.md`
- `docs/archive/PRODUCTION_READINESS_AUDIT.md`
- `docs/archive/RELEASE_VALIDATION_REPORT.md`
- `docs/archive/FINAL_AUDIT_SUMMARY.md`
- `docs/archive/EXECUTIVE_SUMMARY.md`

Use the consolidated document above for quick onboarding and operational steps; consult archive files for deep dives and exact configuration snippets.

---

If you'd like, I can:

- Push these changes to a branch and open a PR.
- Run a quick link-check or spell-check on the consolidated doc.
- Restore an archived file back to top-level if you prefer a different split.
