# Security Internals

Last updated: 2026-03-14

This document explains the project's security implementation details: JWT handling, request filtering, and helper services used by controllers and services.

## Properties and environment variables

- `app.jwt.secret` — application property for the HMAC secret. In `src/main/resources/application.yml` this is mapped from the environment variable `JWT_SECRET`.
- `app.jwt.expiration-ms` — token lifetime in milliseconds (default 3600000).

Ensure `JWT_SECRET` is at least 32 characters to satisfy the runtime check in `JwtUtil`.

## JwtUtil (com.biursite.security.JwtUtil)

Responsibilities:

- Generate and validate JWT tokens using `io.jsonwebtoken` (jjwt).
- Expose helpers to read username and role from the token.

Important details:

- Uses `@Value("${app.jwt.secret}")` to inject the secret.
- Validates at `@PostConstruct` that the secret is non-null and length >= 32; otherwise the app fails fast.
- Generates tokens with claims: `sub` (username) and `role`.

Example usage:

```java
// generate
String token = jwtUtil.generateToken("alice", "ROLE_USER");

// validate
boolean ok = jwtUtil.validateToken(token);

// read claims
String username = jwtUtil.getUsernameFromToken(token);
String role = jwtUtil.getRoleFromToken(token);
```

## JwtFilter (com.biursite.security.JwtFilter)

Responsibilities:

- A `OncePerRequestFilter` that inspects the `Authorization` header for a `Bearer <token>` value.
- If present and valid, it builds a `UsernamePasswordAuthenticationToken` with the role from the token and sets it in the `SecurityContext`.

Notes:

- The filter does not produce error responses itself; it silently ignores invalid/missing tokens and allows subsequent security machinery to handle access control.
- The filter expects roles to be stored as a string claim (e.g., `ROLE_USER`).

## SecurityService (com.biursite.security.SecurityService)

Responsibilities:

- Convenience component for use in controllers/services to obtain the current authenticated user.
- Uses `SecurityContextHolder` to read the principal name and resolves the `User` via `UserRepositoryPort`.

API:

- `Optional<User> getCurrentUser()` — returns an Optional wrapping the domain `User` when authenticated.
- `Long getCurrentUserId()` — returns the user id or throws `IllegalStateException` when not authenticated.

Example:

```java
Long userId = securityService.getCurrentUserId();
User user = securityService.getCurrentUser().orElseThrow(...);
```

## Integration with SecurityConfig

- `SecurityConfig` registers two filter chains:
  - API chain (order 1): `securityMatcher("/api/**")` — CSRF disabled, `SessionCreationPolicy.STATELESS`, adds `JwtFilter` before `UsernamePasswordAuthenticationFilter`.
  - MVC chain (order 2): session-based form login with CSRF via `CookieCsrfTokenRepository`.

This split ensures the REST API uses stateless JWT auth while server-rendered pages use session cookies and CSRF protection.
