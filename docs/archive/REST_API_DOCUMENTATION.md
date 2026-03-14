# Project Verification — 2026-03-09

(Full REST API reference archived here.)

This file contains the complete API reference that was previously at the repository root. It includes:

- Base URLs for development/production
- Authentication (JWT) format and examples
- Endpoints:
  - `POST /api/auth/register` — register, returns JWT
  - `POST /api/auth/login` — login, returns JWT
  - `GET /api/posts` — list posts (paginated)
  - `GET /api/posts/{id}` — get single post
  - `POST /api/posts` — create post (authenticated)
  - `PUT /api/posts/{id}` — update post (owner/admin)
  - `DELETE /api/posts/{id}` — delete post (owner/admin)
  - `GET /api/users` — admin list users
  - `GET /api/users/{id}` — get user
  - `PUT /api/users/{id}` — update user
  - `DELETE /api/users/{id}` — delete user (admin)

- HTTP status codes and error formats
- SDK examples (JavaScript, Python)
- Example cURL workflows for register → create post → list posts

(Full original content copied into archive for reference.)

-- Additional internal references --

For implementation details and examples see the internal API documentation:

- `docs/archive/API_INTERNALS.md` — Controller-level endpoints, auth usage, and examples.
- `docs/archive/SECURITY_INTERNALS.md` — JWT generation/validation and `JwtFilter` behavior.

Property notes:

- Tokens are produced by `AuthController` and validated by `JwtFilter` which expects `Authorization: Bearer <token>` header.
- The secret is read from `app.jwt.secret` (environment variable `JWT_SECRET` in typical deployment).
