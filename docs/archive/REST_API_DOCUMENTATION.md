# Project Verification — 2026-03-09

(Full REST API reference archived here.)

This file contains the complete API reference that was previously at the repository root. It includes:

- Base URLs for development/production
- Authentication (JWT) format and examples
- Endpoints:
  - `POST /api/auth/register` — register, returns `ApiResponse<AuthResponse>` with a JWT
  - `POST /api/auth/login` — login, returns `ApiResponse<AuthResponse>` with a JWT
  - `GET /api/posts` — list posts (returns raw `List<PostView>`)
  - `GET /api/posts/{id}` — get single post (returns raw `PostView`)
  - `POST /api/posts` — create post (authenticated, returns `PostView`)
  - `PUT /api/posts/{id}` — update post (JWT required, returns `PostView`)
  - `DELETE /api/posts/{id}` — delete post (JWT required, returns `204 No Content`)
  - `GET /api/users` — admin list users (`ApiResponse<List<UserDto>>`)
  - `GET /api/users/{id}` — get user (`ApiResponse<UserDto>`)
  - `POST /api/users` — create user (admin, `ApiResponse<UserDto>`)
  - `PUT /api/users/{id}` — update user (self/admin, `ApiResponse<UserDto>`)
  - `DELETE /api/users/{id}` — delete user (admin, `ApiResponse<Void>`)

- HTTP status codes and error formats
- SDK examples (JavaScript, Python)
- Example cURL workflows for register → create post → list posts

Response envelope (applies to auth and user endpoints):

```json
{
  "success": true,
  "status": 200,
  "error": null,
  "message": "Posts retrieved",
  "path": "/api/posts",
  "timestamp": "2026-05-26T10:15:30Z",
  "data": [],
  "meta": {
    "pagination": {
      "page": 0,
      "size": 20,
      "totalElements": 120,
      "totalPages": 6,
      "hasNext": true,
      "hasPrevious": false
    }
  }
}
```

Error responses set `success=false` and include `error` and `message`. Validation errors include `meta.errors` with field-level messages.

Post endpoints return raw `PostView` JSON rather than the `ApiResponse` envelope.

(Full original content copied into archive for reference.)

-- Additional internal references --

For implementation details and examples see the internal API documentation:

- `docs/archive/API_INTERNALS.md` — Controller-level endpoints, auth usage, and examples.
- `docs/archive/SECURITY_INTERNALS.md` — JWT generation/validation and `JwtFilter` behavior.

Property notes:

- Tokens are produced by `AuthControllerAdapter` and validated by `JwtFilter` which expects `Authorization: Bearer <token>` header.
- The secret is read from `app.jwt.secret` (environment variable `JWT_SECRET` in typical deployment).
