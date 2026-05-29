# API Internals

Last updated: 2026-03-14

This document lists the REST controllers, their endpoints, required authentication/authorization, and example payloads.

Controllers of interest (package `com.biursite.infrastructure.web`):

- `AuthControllerAdapter` — `@RequestMapping("/api/auth")`
  - `POST /api/auth/login` — accepts `AuthRequest` (username, password) and returns `ApiResponse<AuthResponse>` containing a JWT in `data.token`.
  - `POST /api/auth/register` — accepts `RegisterRequest` (username, email, password) and returns `ApiResponse<AuthResponse>` containing a JWT in `data.token`.

- `PostControllerAdapter` — `@RequestMapping("/api/posts")` (JWT required for all endpoints under `/api/posts/**`)
  - `GET /api/posts?page=&size=&q=` — list posts, returns a raw `List<PostView>`.
  - `GET /api/posts/{id}` — get single post, returns a raw `PostView`.
  - `POST /api/posts` — create post. Body: `CreatePostRequest`. Returns `PostView`.
  - `PUT /api/posts/{id}` — update post. Body: `UpdatePostRequest`. Returns `PostView`.
  - `DELETE /api/posts/{id}` — delete post, returns `204 No Content`.

- `UserControllerAdapter` — `@RequestMapping("/api/users")`
  - `GET /api/users?page=&size=&q=&banned=` — list users (admin only) and returns `ApiResponse<List<UserDto>>`.
  - `GET /api/users/{id}` — get user; returns 403 if the requester is not the user and not admin.
  - `POST /api/users` — create user (admin only).
  - `PUT /api/users/{id}` — update user (self or admin).
  - `DELETE /api/users/{id}` — delete user (admin only).

## Auth token usage

- API requests that require authentication must include the header:

```
Authorization: Bearer <JWT_TOKEN>
```

Where the token is produced by `AuthControllerAdapter` on successful login/register and validated via `JwtFilter`.

## Error format

- The API uses `ApiResponse.failure(...)` for JSON errors (see `ApiExceptionHandler`, `ApiAuthenticationEntryPoint`, and `ApiAccessDeniedHandler`). Validation failures include a `meta.errors` map.

## Examples

Login request example:

```json
POST /api/auth/login
{
  "username": "alice",
  "password": "s3cret"
}

Response:
{
  "success": true,
  "status": 200,
  "error": null,
  "message": "Authenticated",
  "path": "/api/auth/login",
  "timestamp": "2026-05-26T10:15:30Z",
  "data": {
    "token": "eyJhbGciOi..."
  },
  "meta": null
}
```

Create post example (authenticated):

````json
POST /api/posts
Authorization: Bearer <token>
{
  "title": "My post",
  "content": "Hello world"
}

Response:

```json
{
  "id": 1,
  "title": "My post",
  "content": "Hello world",
  "authorUsername": "alice"
}
````

```

```
