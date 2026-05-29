# BiUrSite Architecture Guide

Last verified: 2026-03-09

## Overview

BiUrSite follows a layered Ports & Adapters (Hexagonal) style with clear separation between business rules (domain), orchestration (application), and technical concerns (infrastructure). The web layer (controllers) is an adapter into the application.

## Layers & Responsibilities

- domain
  - Contains business concepts: entities, value objects, domain events, domain services, and repository _ports_ (interfaces).
  - Must be plain Java (no Spring annotations or technical frameworks) so it is testable and independent.

- application layer
  - Orchestration and application use-cases implemented as interfaces and services in `com.biursite.application`.
  - Translates controller inputs into domain operations and coordinates multiple domain operations and events.
  - Depends on domain ports; may define DTOs for boundary translation.

- infrastructure
  - Technical implementations: JPA Spring Data repositories, adapters that implement domain ports, configuration (Spring beans), messaging, persistence, and external clients.
  - Contains code that bridges domain ports to concrete frameworks (e.g., adapters in `infrastructure.persistence`).
  - May include event publishers/subscribers and background configuration.

- controller / web
  - Web adapters: REST controllers, MVC controllers, request/response mapping, HTTP concerns (status codes, validation errors).
  - Must depend only on application-layer interfaces — controllers call use cases and queries, not infrastructure.

## Dependency Direction Rules

- Domain must not depend on Spring, JPA, or infrastructure packages.
- Service (application) layer depends on domain artifacts (entities, ports, events) and provides interfaces used by controllers.
- Infrastructure implements ports defined in domain or service (e.g., `domain.*.repository` → `infrastructure.*.adapter`).
- Controllers depend only on service interfaces (or DTOs) and must not import infrastructure classes directly.

## Where to place new code

- Domain entities and value objects: `com.biursite.domain.*`
- Repository ports (interfaces): `com.biursite.domain.*.repository`
- Application use cases, queries, DTOs, and ports: `com.biursite.application.*`
- Adapters / implementations: `com.biursite.infrastructure.*` (e.g., `infrastructure.persistence`, `infrastructure.events`, `infrastructure.security`, `infrastructure.web`)
- Controllers: `com.biursite.infrastructure.web`
- Web DTOs: `com.biursite.infrastructure.web.dto`

## Typical request flow (example)

1. Controller (`com.biursite.controller`) receives HTTP request and maps to a DTO.
2. Controller calls an application-layer interface (`com.biursite.application.post.usecase.*` or `com.biursite.application.query.*`).
3. Service orchestrates use-case, validating inputs and invoking domain operations (entities, domain services).
4. Service uses a repository _port_ (e.g., `domain.post.repository.PostRepositoryPort`) to persist or load domain objects.
5. Infrastructure adapter (`com.biursite.infrastructure.persistence.PostRepositoryAdapter`) implements the port and delegates to JPA (`Spring Data` repository) to perform DB operations.
6. Domain events produced by the domain are published via an event publisher (port implemented by `infrastructure.events`).

## Small refactor guidance (non-breaking)

- Prefer moving small technical classes into `infrastructure` when they are framework specific (Spring Data interfaces, configuration classes, adapters).
- Keep the domain free from Spring annotations; if entities must carry JPA annotations for pragmatic reasons, avoid adding other Spring-specific concerns to domain types.
- When creating new code, ensure controllers only call service interfaces; test by grepping for imports from `infrastructure` inside `controller` packages.

## Enforcement checklist

- [ ] Controllers import only application-layer DTOs and ports, and should not import infrastructure packages.
- [ ] Domain packages contain no `org.springframework.*` imports.
- [ ] Infrastructure adapters implement the domain/service ports and contain framework-specific annotations.

## Further reading

- Ports & Adapters (Hexagonal) — keep technical details at the perimeter and business logic at the core.

## Architecture Enforcement

This repository includes a lightweight CI check that enforces the high-level layering rules. The check is intentionally simple (grep-based) so it runs fast in CI.

Rules enforced by CI

- Controllers must not import `com.biursite.infrastructure` packages.
- Domain packages (`com.biursite.domain`) must not import `org.springframework` classes.
- Domain packages must not import `com.biursite.infrastructure` packages.

Where the check runs

- Implemented in `scripts/check_architecture.sh` and run in CI when the architecture check is enabled.

How developers can fix violations

- If a controller accidentally imports an infrastructure class, refactor the dependency into a service interface in `com.biursite.service` and inject that into the controller.
- If domain code imports Spring (or references infrastructure), move the framework-specific code into an adapter under `com.biursite.infrastructure` and expose a clean port (interface) in `com.biursite.domain` or `com.biursite.service`.
- Run the local check before pushing:

```bash
./scripts/check_architecture.sh
```

The script exits with non-zero status and prints offending import lines; fix the import or refactor the code to comply with layering rules.
