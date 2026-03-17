# Sequence Diagrams – BiUrSite Knowledge Sharing Platform

## 1. Author Information

- Full Name: Hang Kheang Taing
- Student ID: 618055
- GitHub Repository URL: https://github.com/Kheang1409/biursite-mini

---

## 2. Overview

This lab provides sequence diagrams for three major use cases of BiUrSite. Each diagram highlights boundary (controllers), control (use-case services), and entity (repositories/entities) participants.

---

## 3. UC-01: Register Account (Visitor → User)

```plantuml
@startuml
actor Visitor
participant "AuthControllerAdapter (Boundary)" as C
participant "RegisterUserService (Control)" as S
participant "UserRepositoryAdapter (Entity)" as R
participant "User (Entity)" as U

Visitor -> C: POST /api/auth/register (username,email,pwd)
C -> S: validate + map to command
S -> R: check username/email uniqueness
R --> S: exists?
S -> U: create user (hashed password, ROLE_USER)
S -> R: save U
R --> S: persisted user
S --> C: registration OK
C --> Visitor: 201 Created / success view
@enduml
```

---

## 4. UC-02: Create Post (Authenticated User)

```plantuml
@startuml
actor User
participant "PostControllerAdapter (Boundary)" as C
participant "CreatePostService (Control)" as S
participant "UserRepositoryAdapter (Entity)" as UR
participant "PostRepositoryAdapter (Entity)" as PR
participant "Post (Entity)" as P

User -> C: POST /api/posts (title,content) with JWT
C -> S: validate + build command (author=username)
S -> UR: load author by username
UR --> S: author entity
S -> P: create post (author, title, content)
S -> PR: save P
PR --> S: persisted post
S --> C: post created
C --> User: 201 Created with post id
@enduml
```

---

## 5. UC-03: Admin Ban User (Moderation)

```plantuml
@startuml
actor Admin
participant "UserControllerAdapter (Boundary)" as C
participant "BanUnbanDeleteUserUseCases (Control)" as S
participant "UserRepositoryAdapter (Entity)" as R
participant "User (Entity)" as U

Admin -> C: POST /api/users/{id}/ban with JWT (ROLE_ADMIN)
C -> S: authorize admin + build command
S -> R: find user by id
R --> S: U
S -> U: mark banned=true
S -> R: save U
R --> S: persisted
S --> C: ban success
C --> Admin: 200 OK
@enduml
```

---

## 6. Notes

- Boundaries map to controllers already present in the codebase.
- Controls correspond to use-case services in the application layer.
- Entities/repositories reflect the JPA adapters and domain entities used at runtime.
- Authentication/authorization is enforced before controller delegates; omitted from arrows for clarity.

---

## 7. Sequence Diagrams

### UC-01: Register Account

![Register Account](<./Register%20Account%20(Visitor%20%E2%86%92%20User).png>)

### UC-02: Create Post

![Create Post](<./Create%20Post%20(Authenticated%20User).png>)

### UC-03: Admin Ban User

![Admin Ban User](<./Admin%20Ban%20User%20(Moderation).png>)
