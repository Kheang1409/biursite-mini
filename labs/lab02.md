# System Requirements Specification – BiUrSite Knowledge Sharing Platform

## 1. Author Information

- Full Name: Hang Kheang Taing
- Student ID: 618055
- GitHub Repository URL: https://github.com/Kheang1409/biursite-mini

---

## 2. Introduction

### Purpose

Define the functional and non-functional requirements, use-case model, system context, and constraints for BiUrSite, a secure, moderated knowledge-sharing platform with authenticated posting and admin moderation.

### Scope

BiUrSite lets visitors read posts, registered users publish/manage their own posts and profile, and admins moderate users and content. The MVP focuses on authentication, post CRUD with ownership checks, profile management/deactivation, and admin moderation with basic observability.

### Definitions

- Post: A user-authored content entry (title, body, timestamps, flags).
- User: An account with credentials and role (`ROLE_USER`, `ROLE_ADMIN`).
- Ban/Deactivate: Admin can ban a user (content hidden); a user may deactivate their own account.

---

## 3. Overall Description

### Product Perspective

A single Spring Boot application exposing REST APIs (`/api/**`) secured by JWT, and server-rendered pages secured by form login/CSRF. Domain is posts and users; data is stored in PostgreSQL (H2 for tests). Admin moderation is enforced via roles and ownership checks.

### User Classes

- Visitor: Anonymous reader of public posts.
- Registered User: Authenticates, manages own posts and profile.
- Admin: Manages users (ban/unban/delete) and can remove posts.
- System Operator: Deploys/monitors service health.

### Assumptions

- HTTPS termination provided externally; JWT secret configured securely.
- Admin role assigned out-of-band (seed data or DB update).
- Single-region, single-tenant deployment is sufficient for the lab.

---

## 4. Use Case Model

### 4.1 Actors

- Visitor
- Registered User
- Admin
- System (security, persistence, mail optional)

### 4.2 Use Case List

- UC-01: Register account
- UC-02: Authenticate (login, obtain JWT for API)
- UC-03: Browse and view posts
- UC-04: Create/Update/Delete own post
- UC-05: Manage profile and deactivate account
- UC-06: Admin moderate users (ban/unban/delete) and posts
- UC-07: View service health (operator)

### 4.3 Use Case Diagram (PlantUML-style)

```
@startuml
actor Visitor
actor User
actor Admin
actor Operator

Visitor --> (Browse Posts)
Visitor --> (View Post)
User --> (Register Account)
User --> (Authenticate)
User --> (Create/Update/Delete Post)
User --> (Manage Profile / Deactivate)
Admin --> (Moderate Users)
Admin --> (Moderate Posts)
Operator --> (View Health)

(Authenticate) .> (Browse Posts) : obtains JWT / session
(Moderate Users) --> (Moderate Posts)
@enduml
```

### 4.4 Use Case Diagram

![Use Case Diagram](./Use%20Case%20Diagram.png)

---

## 5. Use Case Descriptions

### UC-01: Register Account

- Primary Actor: Visitor
- Description: Create a new account with username, email, and password.
- Preconditions: Username/email are not already in use.
- Postconditions: User stored with `ROLE_USER`; password hashed; JWT can be issued on login.
- Main Flow: submit registration form/JSON → validate → hash password → persist user → return success.
- Alternate: duplicate username/email → return 409/validation error.

### UC-02: Authenticate

- Primary Actor: Registered User
- Description: Log in via form (pages) or obtain JWT token for API.
- Preconditions: Account exists and is not banned; credentials valid.
- Postconditions: Session cookie (pages) or JWT issued (API); user context set.
- Alternate: invalid credentials → 401/redirect with error; banned user → 403.

### UC-03: Browse/View Posts

- Primary Actor: Visitor or Registered User
- Description: List posts and view details of a post.
- Preconditions: Service available; posts exist.
- Postconditions: Posts returned excluding banned users’ content.
- Alternate: Post not found or belongs to banned/deactivated user → 404/hidden.

### UC-04: Create/Update/Delete Post

- Primary Actor: Registered User
- Description: Manage own posts (create, edit title/body, delete).
- Preconditions: Authenticated; user not banned/deactivated.
- Postconditions: Post persisted/updated or removed; ownership enforced.
- Alternate: Editing/deleting another user’s post → 403; validation errors → 400.

### UC-05: Manage Profile / Deactivate

- Primary Actor: Registered User
- Description: Update profile data (email/username/password) and optionally deactivate account.
- Preconditions: Authenticated; uniqueness checks pass.
- Postconditions: Profile updated; deactivation marks account inactive and logs out user.
- Alternate: Duplicate username/email → 409/validation error.

### UC-06: Admin Moderate Users and Posts

- Primary Actor: Admin
- Description: View users, ban/unban or delete users; remove posts if needed.
- Preconditions: Admin authenticated.
- Postconditions: User status updated; banned users’ content hidden; audits logged (via structured logs).
- Alternate: Attempt by non-admin → 403.

### UC-07: View Service Health

- Primary Actor: Operator/Admin
- Description: Check health/info endpoints to verify service readiness.
- Preconditions: Service running.
- Postconditions: Health status returned; metrics/logs available.

---

## 6. Functional Requirements

- FR-01: The system shall allow visitors to browse and view public posts.
- FR-02: The system shall allow visitors to register with unique username/email and hashed passwords.
- FR-03: The system shall allow registered users to log in (session for pages, JWT for APIs).
- FR-04: The system shall enforce role-based access: `ROLE_USER` for regular users, `ROLE_ADMIN` for admins.
- FR-05: The system shall allow authenticated users to create, update, and delete their own posts.
- FR-06: The system shall block editing/deleting posts not owned by the requester (403).
- FR-07: The system shall allow profile updates (email/username/password) and self-deactivation.
- FR-08: The system shall allow admins to view users, ban/unban users, and delete users.
- FR-09: The system shall hide content from banned users in public listings.
- FR-10: The system shall expose REST endpoints for auth (`/api/auth/login`, `/api/auth/register`), posts (`/api/posts/**`), and users (`/api/users/**` for admin).
- FR-11: The system shall provide server-rendered pages for posts, auth, profile, and admin user list with CSRF protection.
- FR-12: The system shall expose health/info endpoints for operators.

---

## 7. Non-Functional Requirements

- NFR-01 (Security): Hash passwords (BCrypt), validate inputs, and require auth for mutations; JWT signed with server secret.
- NFR-02 (Availability): App should remain usable during normal single-node maintenance with fast restarts (<30s startup in lab).
- NFR-03 (Performance): Page/API responses for common requests should return within 500ms under light lab load.
- NFR-04 (Usability): Web pages shall include clear error messages on validation/auth failures.
- NFR-05 (Maintainability): Code follows layered/hexagonal structure; new modules aim for ≥80% test coverage (unit + integration).
- NFR-06 (Observability): Emit structured logs; expose Actuator health/info for operators.
- NFR-07 (Data): Use PostgreSQL for durable storage; use H2 for automated tests.

---

## 8. External Interface Requirements

- REST API (JSON):
  - `POST /api/auth/register`, `POST /api/auth/login`
  - `GET /api/posts`, `GET /api/posts/{id}`, `POST /api/posts`, `PUT /api/posts/{id}`, `DELETE /api/posts/{id}`
  - `GET /api/users` (admin), `PUT /api/users/{id}`, `DELETE /api/users/{id}`, admin ban/unban endpoints
- Web UI (Thymeleaf pages): `/`, `/posts/{id}`, `/login`, `/register`, `/profile`, `/admin/users`
- Actuator: `/actuator/health`, `/actuator/info` (limited exposure)
- Auth: JWT in `Authorization: Bearer <token>` for API; session cookie for pages.

---

## 9. Data Requirements

- User: id, username, email, passwordHash, roles, banned, deactivated, createdAt, updatedAt.
- Post: id, title, content, authorId, banned flag/reason, createdAt, updatedAt.
- JWT claims: subject (username), roles, expiration.
- Logging: structured JSON for key events (auth, moderation actions).

---

## 10. Git + GitHub Setup

- Repository URL: https://github.com/Kheang1409/biursite-mini
- Tools: Git, Maven, JDK 21, IDE (IntelliJ/VS Code).
- Suggested workflow:

```bash
git init
git add .
git commit -m "chore(labs): add lab01-lab03 docs"
git branch -M main
git remote add origin https://github.com/Kheang1409/biursite-mini.git
git push -u origin main
```

- Add CI to run `mvn test`; include `.gitignore` for Java/IDE/Node artifacts.

---

## 11. Presentation Plan (10 minutes)

- Slide 1: Title, Author, Student ID
- Slide 2: Problem & Users
- Slide 3: Vision and MVP scope
- Slide 4: Use-Case diagram and main flows (register/login, post CRUD, moderation)
- Slide 5: Functional & non-functional highlights
- Slide 6: Architecture overview (layers, JWT + form login, JPA, PostgreSQL)
- Slide 7: Demo plan (register, login, create post, admin ban)
- Slide 8: Next steps and backlog

---

## 12. Project Structure Update

- labs/lab01.md, labs/lab02.md, labs/lab03.md
- src/main/java: domain, application use cases, web adapters, persistence, security config
- src/main/resources: application.yml, templates, static assets
- src/test/java: unit, integration, architecture tests
- Dockerfile, docker-compose.yml for local runs

---

## 13. Verification Checklist

- [x] `lab02.md` updated and placed inside `/labs`.
- [x] Use-case diagram and descriptions align to BiUrSite features.
- [x] Functional and non-functional requirements listed.
- [x] GitHub URL included.
- [x] Presentation plan outlined.

---

End of SRS (Lab 02).

## Deliverables Checklist

- [x] Use-case model and descriptions
- [x] Functional and non-functional requirements
- [x] External interface requirements (REST, pages, actuator)
- [x] GitHub repository URL included
