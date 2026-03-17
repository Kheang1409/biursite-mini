# Lab05 – Collaboration & VOPC Diagrams (BiUrSite)

## 1. Author Information

- Full Name: Hang Kheang Taing
- Student ID: 618055
- GitHub Repository URL: https://github.com/Kheang1409/biursite-mini

---

## 2. Overview

This lab provides collaboration (communication) and VOPC (View Of Participating Classes) diagrams for key BiUrSite use cases. The models follow boundary/control/entity stereotypes used in earlier labs.

Use cases covered:

- UC-Register Account
- UC-Create Post
- UC-Admin Ban User

---

## 3. Collaboration Diagrams

### 3.1 UC-Register Account

```plantuml
@startuml
skinparam linetype ortho
actor Visitor
participant "AuthControllerAdapter\n(Boundary)" as C
participant "RegisterUserService\n(Control)" as S
participant "UserRepositoryAdapter\n(Entity)" as R

Visitor -> C : 1: POST /api/auth/register
C -> S : 2: validate + toCommand()
S -> R : 3: existsByUsername/email?
R --> S : 4: exists?
S -> R : 5: save(user)
R --> S : 6: user persisted
S --> C : 7: result
C --> Visitor : 8: 201 Created
@enduml
```

### 3.2 UC-Create Post

```plantuml
@startuml
skinparam linetype ortho
actor User
participant "PostControllerAdapter\n(Boundary)" as C
participant "CreatePostService\n(Control)" as S
participant "UserRepositoryAdapter\n(Entity)" as UR
participant "PostRepositoryAdapter\n(Entity)" as PR

User -> C : 1: POST /api/posts (JWT)
C -> S : 2: validate + toCommand()
S -> UR : 3: loadAuthor(username)
UR --> S : 4: author
S -> PR : 5: save(post)
PR --> S : 6: post persisted
S --> C : 7: result
C --> User : 8: 201 Created
@enduml
```

### 3.3 UC-Admin Ban User

```plantuml
@startuml
skinparam linetype ortho
actor Admin
participant "UserControllerAdapter\n(Boundary)" as C
participant "BanUnbanDeleteUserUseCases\n(Control)" as S
participant "UserRepositoryAdapter\n(Entity)" as R

Admin -> C : 1: POST /api/users/{id}/ban (JWT admin)
C -> S : 2: authorize + toCommand()
S -> R : 3: findById(id)
R --> S : 4: user
S -> R : 5: save(banned user)
R --> S : 6: persisted
S --> C : 7: result
C --> Admin : 8: 200 OK
@enduml
```

---

## 4. VOPC (View Of Participating Classes)

Each diagram highlights participating boundary, control, and entity classes for the use case.

### 4.1 UC-Register Account VOPC

```plantuml
@startuml
skinparam classAttributeIconSize 0
package Boundary {
  class AuthControllerAdapter
}
package Control {
  class RegisterUserService
}
package Entity {
  class UserRepositoryAdapter
  class User
}
AuthControllerAdapter -> RegisterUserService
RegisterUserService -> UserRepositoryAdapter
UserRepositoryAdapter --> User
@enduml
```

### 4.2 UC-Create Post VOPC

```plantuml
@startuml
skinparam classAttributeIconSize 0
package Boundary {
  class PostControllerAdapter
}
package Control {
  class CreatePostService
}
package Entity {
  class UserRepositoryAdapter
  class PostRepositoryAdapter
  class User
  class Post
}
PostControllerAdapter -> CreatePostService
CreatePostService -> UserRepositoryAdapter
CreatePostService -> PostRepositoryAdapter
UserRepositoryAdapter --> User
PostRepositoryAdapter --> Post
@enduml
```

### 4.3 UC-Admin Ban User VOPC

```plantuml
@startuml
skinparam classAttributeIconSize 0
package Boundary {
  class UserControllerAdapter
}
package Control {
  class BanUnbanDeleteUserUseCases
}
package Entity {
  class UserRepositoryAdapter
  class User
}
UserControllerAdapter -> BanUnbanDeleteUserUseCases
BanUnbanDeleteUserUseCases -> UserRepositoryAdapter
UserRepositoryAdapter --> User
@enduml
```

---

## 5. Notes

- Boundary classes map to controllers; Control classes to use-case services; Entity classes to repository adapters and domain entities.
- Numbering in collaboration diagrams follows message order per lesson guidance.
- Security steps (JWT/form auth) happen before controller invocation and are implicit in these diagrams.

---

## 6. Embedded Diagrams

### Collaboration Diagrams

#### UC-Register Account

![UC-Register Account Collaboration](<./Register%20Account%20(Visitor%20%E2%86%92%20User).png>)

#### UC-Create Post

![UC-Create Post Collaboration](<./Create%20Post%20(Authenticated%20User).png>)

#### UC-Admin Ban User

![UC-Admin Ban User Collaboration](<./Admin%20Ban%20User%20(Moderation).png>)

### VOPC Diagrams

#### UC-Register Account

![UC-Register Account VOPC](./UC-Register%20Account%20VOPC.png)

#### UC-Create Post

![UC-Create Post VOPC](./UC-Create%20Post%20VOPC.png)

#### UC-Admin Ban User

![UC-Admin Ban User VOPC](./UC-Admin%20Ban%20User%20VOPC.png)
