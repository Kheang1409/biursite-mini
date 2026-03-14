# API Internals

Last updated: 2026-03-14

This document lists the REST controllers, their endpoints, required authentication/authorization, and example payloads.

Controllers of interest (package `com.biursite.controller`):

- `AuthController` — `@RequestMapping("/api/auth")`
  - `POST /api/auth/login` — accepts `AuthRequest` (username, password) and returns `AuthResponse` containing `token`.
  - `POST /api/auth/register` — accepts `RegisterRequest` (username, email, password) and returns `AuthResponse`.

- `PostController` — `@RequestMapping("/api/posts")` (requires authentication for write ops)
  - `GET /api/posts?page=&size=` — list posts (public).
  - `GET /api/posts/{id}` — get single post (public).
  - `POST /api/posts` — create post (authenticated). Body: `CreatePostRequest`.
  - `PUT /api/posts/{id}` — update post (authenticated; service checks ownership or admin).
  - `DELETE /api/posts/{id}` — delete post (authenticated; service checks ownership or admin).

- `UserController` — `@RequestMapping("/api/users")`
  - `GET /api/users` — list users (admin only; `@RolesAllowed({"ADMIN"})`).
  - `GET /api/users/{id}` — get user; returns 403 if requester is not the user and not admin.
  - `POST /api/users` — create user (admin only).
  - `PUT /api/users/{id}` — update user (self or admin).
  - `DELETE /api/users/{id}` — delete user (admin only).

## Auth token usage

- API requests that require authentication must include the header:

```
Authorization: Bearer <JWT_TOKEN>
```

Where the token is produced by `AuthController` on successful login/register and validated via `JwtFilter`.

## Error format

- The API uses a JSON error payload (see `ApiExceptionHandler` in `com.biursite.config`) — typically a top-level `error` message or a map of field validation errors.

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
  "token": "eyJhbGciOi...",
  "expiresIn": 3600000
}
```

Create post example (authenticated):

```json
POST /api/posts
Authorization: Bearer <token>
{
  "title": "My post",
  "content": "Hello world"
}
```
