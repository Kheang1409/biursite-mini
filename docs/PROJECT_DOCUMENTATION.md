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

BiUrSite is a Spring Boot 3.5.11 application (Java 25) combining server-rendered Thymeleaf pages, JSON API endpoints, PostgreSQL 15 persistence, and Tailwind (CDN) styling.

Key points:

- Dual authentication: session-based form login for web UI; JWT for `/api/**`.
- Layered architecture: controllers call application-layer use cases and queries; infrastructure adapters implement persistence and security concerns.
- Environment-driven config: the app reads `JWT_SECRET` and database settings from environment variables.

---

## System Architecture

High-level flow:

- Clients (browser or API client) → Controllers (MVC or REST) → application-layer use cases/queries → infrastructure adapters/repositories → PostgreSQL
- Security: two filter chains - API chain (stateless JWT) and MVC chain (session-based form login with CSRF cookies).

Design decisions:

- Keep domain logic framework-free where practical; place Spring-specific adapters under `infrastructure`.
- Controllers depend on application-layer interfaces and DTOs; repositories live under `domain.*.repository` and are implemented by infrastructure adapters.
- CQRS-lite: read endpoints use a query layer with projection-based DTOs; write endpoints continue to use command-side use cases.

Technology summary:

- Java 25, Spring Boot 3.5.11, Thymeleaf, Spring Security, Spring Data JPA (Hibernate 6), PostgreSQL 15, Maven, Docker

Caching summary:

- Application-level caches for post lists, single posts, and user lists using Spring Cache with Caffeine TTLs.
- Cache invalidation is event-driven via domain events after write transactions commit.

Consistency and observability:

- Optimistic locking via entity versions to prevent lost updates.
- Actuator endpoints exposed for health and metrics.
- Requests accept `X-Correlation-ID` for log correlation and tracing.
- Query search uses a pluggable search strategy (default LIKE-based).
- Optional async projections populate a `post_read_model` table; reads may lag writes briefly when enabled.

---

## UI Layer (summary)

- Thymeleaf templates use shared fragments (`head`, `navbar`, `footer`); `fragments/layout` remains as a delegating compatibility wrapper.
- Tailwind CSS (via CDN) + Font Awesome for icons. Dark mode is class-based and persisted in `localStorage`.
- Reusable components: post cards, modals (new post), forms with validation. Server-side rendering ensures XSS-safe outputs (Thymeleaf escapes by default).

UX features:

- Responsive layout, theme toggle, modal-based new-post creation, accessible forms (labels, ARIA where needed).

---

## Database Schema (summary)

Core tables:

- `users` (id, version, username unique, email unique, password hash, role, banned, deactivated, created_at)
- `posts` (id, version, title, content, author_id FK -> users(id) ON DELETE CASCADE, created_at, updated_at, banned, ban_reason)

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

- POST /api/auth/register → register user and return `ApiResponse<AuthResponse>` with a JWT
- POST /api/auth/login → login and return `ApiResponse<AuthResponse>` with a JWT

Posts:

- GET /api/posts?page=&size=&q= → list posts (returns `List<PostView>`, optional search)
- GET /api/posts/{id} → get post (returns `PostView`)
- POST /api/posts → create (requires JWT, returns `PostView`)
- PUT /api/posts/{id} → update (requires JWT, returns `PostView`)
- DELETE /api/posts/{id} → delete (requires JWT, no content)

Users:

- GET /api/users?page=&size=&q=&banned= → list users (admin, paginated, optional search)
- GET /api/users/{id} → get user (self/admin)
- POST /api/users → create user (admin)
- PUT /api/users/{id} → update user (self/admin)
- DELETE /api/users/{id} → delete user (admin)

Response format: auth and user endpoints return a standard envelope with `success`, `status`, `error`, `message`, `path`, `timestamp`, `data`, and optional `meta` for pagination and validation errors. Post endpoints return raw `PostView` JSON.
Error format: `success=false` with `error` and `message`. Validation errors include `meta.errors`. See archived `REST_API_DOCUMENTATION.md` for examples and SDK snippets.

---

## Security (high-level)

- Passwords: BCrypt hashing (strength 10).
- API auth: JWT signed using `io.jsonwebtoken` (jjwt) with an HMAC secret key; `JWT_SECRET` (mapped to `app.jwt.secret`) must be set and ≥32 characters.
- MVC auth: Spring Security form login and HttpSession with secure cookies and CSRF protection.
- CSRF: enabled for MVC; disabled for stateless API endpoints.
- Input validation: Bean Validation (`@Valid`) and Thymeleaf escaping guard against injection/XSS.
- Secrets: keep in environment variables or a managed secret store.

---

## Setup & Run (quickstart)

Prerequisites:

- Java 25 (or Java 21+), Maven 3.9+, Docker (optional)

Quick start (development):

```bash
# copy env template if you want to override defaults
# macOS / Linux
cp .env.example .env
# Windows PowerShell
Copy-Item .env.example .env
# build (skip tests for fast local run if desired)
mvn -DskipTests clean package
# run with Docker Compose (uses local defaults out of the box)
docker compose up -d --build
# or run locally
mvn spring-boot:run
```

Environment variables to set for production:

- `JWT_SECRET` (required, ≥32 chars) — mapped to Spring property `app.jwt.secret` used by `JwtUtil`.

- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`
- `SERVER_PORT`, `LOG_LEVEL`

See implementation details in the archived internals:

- `docs/archive/API_INTERNALS.md` — REST controller endpoints and examples.
- `docs/archive/SECURITY_INTERNALS.md` — JWT and security helper internals (`JwtUtil`, `JwtFilter`, `SecurityService`).

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
