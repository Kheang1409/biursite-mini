# Vision Document – BiUrSite Knowledge Sharing Platform

## 1. Author Information

- Full Name: Hang Kheang Taing
- Student ID: 618055

---

## Project Choice

Chosen project: BiUrSite — a secure, moderated knowledge-sharing platform where registered users publish posts and admins oversee community health.

---

## 2. Problem Statement

Small teams and student communities often lack a lightweight, secure place to share knowledge. Common issues include scattered content across chat threads, weak moderation, and poor accountability. The goal is to provide a single hub with authentication, role-based controls, and clear ownership of posts.

---

## 3. Stakeholders / Users

- Visitor: Reads public posts.
- Registered User: Creates, edits, and deletes own posts; manages profile.
- Admin: Moderates users and content (ban/unban, delete, view all users).
- System Operator: Deploys and observes the service (logging/health checks).

---

## 4. Problem–Need–Feature Table

| Problem                                  | Need                                       | Feature                                            |
| ---------------------------------------- | ------------------------------------------ | -------------------------------------------------- |
| Knowledge scattered across chats/docs    | Central place to read and publish posts    | Post creation, listing, detail views               |
| No accountability for content authorship | Authenticated posting and ownership checks | JWT/form login, author-only edit/delete            |
| Risk of spam or abuse                    | Moderation controls                        | Admin ban/unban users; hide banned users’ content  |
| Stale or abandoned accounts              | User self-service profile management       | Update profile, deactivate account                 |
| Hard to manage access to admin actions   | Role-based permissions                     | ROLE_ADMIN vs ROLE_USER enforcement                |
| Low visibility into system health        | Basic observability                        | Actuator health/info endpoints, structured logging |
| Inconsistent data storage                | Durable, relational persistence            | Spring Data JPA with PostgreSQL (H2 for tests)     |

---

## 5. Product Vision Statement

Provide a secure, moderated hub for publishing and consuming knowledge, with clear ownership, role-based controls, and reliable persistence to keep community content trustworthy and easy to find.

---

## 6. Key Features / Scope

Core features (MVP)

- User registration and login (JWT for APIs; form login for pages).
- Create, read, update, and delete own posts; list posts (hide banned/deactivated authors).
- Profile update and optional account deactivation.
- Admin capabilities: view users, ban/unban, delete users, remove posts.
- Basic observability: health/info endpoints, structured logging.

Future enhancements (nice-to-have)

- Rich text editing and tagging.
- Post reactions/comments with moderation queues.
- Email/password reset and MFA.
- Search and pagination improvements.
- Audit history for admin actions.

---

## 7. Assumptions

- Users can supply valid email/username and remember credentials.
- Admin role is assigned out-of-band (seeded account or DB flag).
- Running in a trusted network or behind HTTPS termination.
- Single-region deployment is acceptable for the lab; no multi-tenant isolation.

---

## 8. Constraints

- Time-bound academic lab; prioritize core auth/posts/moderation over advanced features.
- Must run on commodity hardware (developer laptop) and standard open-source stack.
- Keep dependencies minimal and compatible with Spring Boot 3.5.x and Java 21.

---

## 9. Technology Stack (Planned / Implemented)

- Backend: Java 21, Spring Boot 3.5.x (Web, Security, Validation, Actuator).
- Security: Spring Security (JWT for `/api/**`, form login + CSRF for pages), BCrypt hashing.
- Persistence: Spring Data JPA, PostgreSQL (runtime), H2 (tests).
- Templating: Thymeleaf for server-rendered pages.
- Build: Maven.
- Testing: JUnit 5, Mockito, Spring Test, ArchUnit.
- Logging/observability: Logback with JSON encoder, Actuator health/info.

---

## 10. Development Tools Setup (used/recommended)

- JDK 21 (Temurin / OpenJDK).
- Maven 3.8+.
- IDE: IntelliJ IDEA or VS Code.
- Git and GitHub for version control.
- Optional: Docker + docker-compose for running PostgreSQL locally.

---

## 11. Project Structure (current, trimmed for lab)

- labs/ — lab deliverables (vision, SRS, architecture).
- src/main/java — domain, application use cases, web adapters, persistence adapters, config.
- src/main/resources — application.yml, templates, static assets.
- src/test/java — unit, integration, and architecture tests.
- Dockerfile, docker-compose.yml — container and local runtime support.

---

## 12. Compliance With Lab Requirements

- Includes author info and problem statement.
- Provides stakeholder list and Problem–Need–Feature table.
- States product vision and scope (MVP vs future).
- Lists assumptions, constraints, and technology stack.
- Document is located at `/labs/lab01.md`.

---

## 13. Next Steps (Lab 01)

- Finalize stakeholder review of this vision.
- Seed an initial admin account and test user flows end-to-end.
- Keep Git commits for vision and initial scaffolding pushed to GitHub.

---

## 11. Diagrams

### Use Case Diagram

![Use Case Diagram](./Use%20Case%20Diagram.png)
