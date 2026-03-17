# System Architecture – BiUrSite Knowledge Sharing Platform

## 1. Author Information

- Full Name: Hang Kheang Taing
- Student ID: 618055
- GitHub Repository URL: https://github.com/Kheang1409/biursite-mini

---

## Architectural Analysis

### 1. Architectural Drivers

- Security and moderation: enforce roles (admin/user) and ownership on post actions.
- Simplicity: single Spring Boot deployable with clear layers and ports/adapters.
- Data integrity: durable relational storage for users/posts; prevent unauthorized edits.
- Observability: structured logs and health endpoints for operators.
- Maintainability: testable use-case services, separated web/adapters from domain logic.

### 2. Constraints

- Single-node monolith for the lab; no distributed messaging required.
- Runs on Java 21, Spring Boot 3.5.x; PostgreSQL in runtime, H2 in tests.
- JWT secret and admin seeding handled via configuration/bootstrap.

### 3. Key Design Decisions

- Hexagonal layering: domain + use cases; web adapters (REST + MVC); persistence adapters (Spring Data JPA); security adapter (JWT + form login).
- Dual security mode: stateless JWT for `/api/**`; stateful form login with CSRF for pages.
- JPA/Hibernate for persistence; Actuator for health/info; Logback JSON encoder for structured logs.

---

## System Overview (High-Level Architecture)

Core components (single app):

- Web Adapters: REST controllers for `/api/auth`, `/api/posts`, `/api/users`; MVC controllers for pages (posts, profile, admin, auth).
- Security: Spring Security config (JWT filter, form login, CSRF for pages), custom user details, password hashing.
- Application Layer: use-case services for auth, post CRUD, profile management, admin moderation.
- Domain Layer: entities/value objects for User and Post plus domain events.
- Persistence Adapters: JPA repositories and adapters implementing domain ports; PostgreSQL database (H2 for tests).
- Observability: Actuator health/info, structured logging via Logback encoder.

---

## PlantUML Architecture Diagram

```
@startuml
actor Visitor
actor User
actor Admin

rectangle "Web Adapters" {
	[REST Controllers]
	[MVC Controllers]
}

rectangle "Security" {
	[JWT Filter]
	[Form Login + CSRF]
	[CustomUserDetailsService]
}

rectangle "Application Layer" {
	[Auth Services]
	[Post Use Cases]
	[Profile/Moderation Use Cases]
}

rectangle "Domain Layer" {
	[User Entity]
	[Post Entity]
}

rectangle "Persistence Adapters" {
	[JPA Repositories]
	[UserRepositoryAdapter]
	[PostRepositoryAdapter]
}

database "PostgreSQL / H2 (tests)" as DB

Visitor --> [MVC Controllers]
User --> [REST Controllers]
Admin --> [REST Controllers]
[REST Controllers] --> [Security]
[MVC Controllers] --> [Security]
[Security] --> [Application Layer]
[Application Layer] --> [Domain Layer]
[Application Layer] --> [Persistence Adapters]
[Persistence Adapters] --> DB
@enduml
```

---

## Component Breakdown

### Web Adapters

- REST controllers for auth, posts, users (JWT-protected); MVC controllers for pages with CSRF/session.

### Security

- Spring Security config with JWT filter chain for APIs; form login for pages; BCrypt password hashing; role/ownership checks.

### Application Layer

- Use-case services implementing register/login, post CRUD, profile update/deactivate, admin ban/unban/delete users.

### Domain Layer

- Entities `User` and `Post`, domain rules (ownership, banned/deactivated visibility), simple domain events.

### Persistence Adapters

- Spring Data JPA repositories wrapped by adapters to enforce domain ports; PostgreSQL runtime, H2 for tests.

### Observability

- Actuator health/info endpoints; structured logging via Logstash encoder.

---

## End-to-End Flow (Post CRUD with Auth)

1. User registers (form/API) → password hashed → user persisted with ROLE_USER.
2. User authenticates → session (pages) or JWT (API) issued.
3. Authenticated user creates/edits/deletes own post via REST or page forms → application layer enforces ownership → JPA persists.
4. Visitors/users browse posts; banned users’ content excluded in queries.
5. Admin uses protected endpoints/pages to ban/unban/delete users; banned users cannot authenticate and their content is hidden.
6. Operator checks `/actuator/health` for liveness/readiness; logs captured for moderation/audit context.

---

## How This Architecture Meets NFRs

- Security: Separate API/page security chains; JWT signing; BCrypt; role/ownership checks in controllers/services.
- Availability/Performance: Single-node app with lightweight stack; startup under ~30s; no external queues required.
- Observability: Actuator health/info and structured JSON logs.
- Maintainability: Hexagonal layout keeps domain/use cases isolated from adapters; tests cover services, controllers, and architecture rules.

---

## Technology Mapping

- Framework: Spring Boot 3.5.x, Java 21.
- Security: Spring Security (JWT + form login), jjwt, BCrypt.
- Persistence: Spring Data JPA, PostgreSQL runtime, H2 tests.
- Views: Thymeleaf templates; static assets under resources.
- Build/Test: Maven, JUnit 5, Mockito, ArchUnit.
- Observability: Actuator, Logback JSON encoder.

---

## Deployment & PoC Plan

- Local dev: run `docker-compose up` to start PostgreSQL; run app via `mvn spring-boot:run` or packaged jar.
- CI: run `mvn test` on pushes.
- Optional: Build container with `Dockerfile` and run with env vars for DB/JWT.

---

## Risks & Mitigations

- JWT secret misconfiguration → document env vars and refuse startup if missing.
- Weak moderation controls → enforce role checks in controllers/services; hide banned users’ content in queries.
- Data loss from local DB → recommend external PostgreSQL volume/backups for non-test runs.

---

## Next Steps (Lab 03 Deliverables)

- Export PlantUML diagram to PNG and place under docs/architecture.
- Verify security chains (JWT vs form login) with integration tests across `/api/**` and pages.
- Add admin seeding script/profile for local runs.
- Ensure Docker Compose works with app env vars (DB, JWT secret) and document run commands.

---

## 4. Diagrams

### PlantUML Architecture Diagram

![PlantUML Architecture Diagram](./PlantUML%20Architecture%20Diagram.png)

End of System Architecture (Lab 03).
