# Project Verification — 2026-03-09

- Java: 25
- Spring Boot: 3.5.11
- Database: PostgreSQL 15 (docker-compose)
- Tailwind: CDN (used in templates)
- Thymeleaf fragments: `fragments/head`, `fragments/navbar`, `fragments/footer`, `fragments/layout` (the `layout` fragment is present and used by templates)
- Verified against: `pom.xml`, `docker-compose.yml`, `src/main/resources/application.yml`

# BiUrSite Database Schema & Design

## Database Overview

**Type**: PostgreSQL 15+  
**Host**: Configurable via environment (`DB_HOST`)  
**Port**: 5432 (default)  
**Tables**: 2 main tables + system tables  
**Indexes**: 3 custom indexes

---

## Entity Relationship Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                          USERS                              │
├─────────────────────────────────────────────────────────────┤
│ id (PK)              │ BIGSERIAL PRIMARY KEY                │
│ username             │ VARCHAR(255) NOT NULL UNIQUE         │
│ email                │ VARCHAR(255) NOT NULL UNIQUE         │
│ password             │ VARCHAR(255) NOT NULL                │
│ role                 │ VARCHAR(50) NOT NULL DEFAULT 'USER'  │
│ created_at           │ TIMESTAMP NOT NULL                   │
└─────────────────────────────────────────────────────────────┘
           ▲
           │ 1:N (one user → many posts)
           │
       author_id (FK)
           │
┌──────────▼──────────────────────────────────────────────────┐
│                          POSTS                              │
├─────────────────────────────────────────────────────────────┤
│ id (PK)              │ BIGSERIAL PRIMARY KEY                │
│ title                │ VARCHAR(255) NOT NULL                │
│ content              │ TEXT                                 │
│ author_id (FK)       │ BIGINT NOT NULL REFERENCES users(id) │
│ created_at           │ TIMESTAMP NOT NULL                   │
│ updated_at           │ TIMESTAMP                            │
└─────────────────────────────────────────────────────────────┘
```

---

## Table Definitions

### USERS Table

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    banned BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT users_pkey PRIMARY KEY (id),
    CONSTRAINT users_username_key UNIQUE (username),
    CONSTRAINT users_email_key UNIQUE (email)
);
```

**Columns**:

- **id**: Auto-incremented primary key (64-bit integer)
- **username**: Unique identifier for login (max 255 chars)
- **email**: Contact email, must be unique (max 255 chars)
- **password**: BCrypt-hashed password (60 chars fixed length)
- **role**: User role - `ROLE_USER` or `ROLE_ADMIN`
- **created_at**: Account creation timestamp (UTC)
- **banned**: Boolean flag indicating whether the user is banned (default false)

**Constraints**:

- ✅ Primary key on `id`
- ✅ Unique constraint on `username`
- ✅ Unique constraint on `email`
- ✅ Not null on all fields

---

### POSTS Table

```sql
CREATE TABLE posts (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    author_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    banned BOOLEAN NOT NULL DEFAULT false,
    ban_reason VARCHAR(1024),
    CONSTRAINT posts_pkey PRIMARY KEY (id),
    CONSTRAINT posts_author_id_fkey FOREIGN KEY (author_id)
        REFERENCES users(id) ON DELETE CASCADE
);
```

**Columns**:

- **id**: Auto-incremented primary key
- **title**: Post title (required, max 255 chars)
- **content**: Post body (optional, unlimited size)
- **author_id**: Foreign key to user who created post
- **created_at**: Post creation timestamp (UTC)
- **updated_at**: Last modification timestamp (nullable, null = never edited)
- **banned**: Boolean flag indicating whether the post is banned (default false)
- **ban_reason**: Optional text explaining why the post was banned

**Constraints**:

- ✅ Primary key on `id`
- ✅ Foreign key on `author_id` → `users.id`
- ✅ Cascade delete: deleting a user deletes all their posts
- ✅ Not null: title, author_id, created_at

---

## Indexes

### Index 1: Author Foreign Key

```sql
CREATE INDEX idx_posts_author_id ON posts(author_id);
```

**Purpose**: Speed up queries filtering by author  
**Usage**: `SELECT * FROM posts WHERE author_id = ?`  
**Cardinality**: Medium (many posts per author)

---

### Index 2: Created At (Descending)

```sql
CREATE INDEX idx_posts_created_at ON posts(created_at DESC);
```

**Purpose**: Speed up feed queries sorted by newest first  
**Usage**: `SELECT * FROM posts ORDER BY created_at DESC LIMIT 10`  
**Cardinality**: High (evenly distributed across time)

---

### Index 3: Updated At (Descending)

```sql
CREATE INDEX idx_posts_updated_at ON posts(updated_at DESC);
```

**Purpose**: Find recently modified posts  
**Usage**: `SELECT * FROM posts WHERE updated_at IS NOT NULL ORDER BY updated_at DESC`  
**Cardinality**: Low (most posts never edited)

---

## Query Patterns & Optimization

### Get Posts with Authors (N+1 Prevention)

**❌ WRONG - N+1 Query Problem:**

```java
List<Post> posts = postRepository.findAll();
// SELECT * FROM posts;  (1 query)

for (Post post : posts) {
    String author = post.getAuthor().getUsername();
    // SELECT * FROM users WHERE id = ?;  (N queries!)
}
```

Result: 1 + N database queries (very slow!)

**✅ CORRECT - JOIN FETCH:**

```java
@Query("SELECT p FROM Post p JOIN FETCH p.author")
List<Post> findAllWithAuthor();
// SELECT posts.*, users.* FROM posts
//   INNER JOIN users ON posts.author_id = users.id;
```

Result: 1 database query (optimal!)

---

### Paginated Feed

```java
@Query(value = "SELECT p FROM Post p JOIN FETCH p.author",
       countQuery = "SELECT COUNT(p) FROM Post p")
Page<Post> findAllWithAuthor(Pageable pageable);

// Usage
Page<Post> page = repository.findAllWithAuthor(
    PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"))
);
```

---

## Data Migration Strategy

### Using Liquibase (Recommended for Production)

```yaml
spring:
  liquibase:
    change-log: classpath:db/changelog/db.changelog-master.yaml
```

**db/changelog/db.changelog-master.yaml**:

```yaml
databaseChangeLog:
  - changeSet:
      id: 1-create-users-table
      author: dev
      changes:
        - createTable:
            tableName: users
            columns:
              - column:
                  name: id
                  type: BIGSERIAL
                  constraints:
                    primaryKey: true
              - column:
                  name: username
                  type: VARCHAR(255)
                  constraints:
                    nullable: false
                    unique: true
              # ... rest of columns

  - changeSet:
      id: 2-create-posts-table
      author: dev
      # ... posts table definition
```

### Using Flyway (Alternative)

```yaml
spring:
  flyway:
    locations: classpath:db/migration
```

**Files**:

- `db/migration/V1__Create_users_table.sql`
- `db/migration/V2__Create_posts_table.sql`
- `db/migration/V3__Create_indexes.sql`

---

## Constraints & Foreign Keys

### Cascade Delete

**Current Implementation:**

```java
@Entity
public class User {
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;
}

// OR in SQL
CONSTRAINT posts_author_id_fkey FOREIGN KEY (author_id)
    REFERENCES users(id) ON DELETE CASCADE
```

**Behavior:**

```
DELETE FROM users WHERE id = 1;
├─ Automatically deletes all posts where author_id = 1
└─ No orphaned posts in database
```

---

## Performance Characteristics

### Query Performance Estimates

| Query                                                      | Time  | Rows | Index Used                    |
| ---------------------------------------------------------- | ----- | ---- | ----------------------------- |
| `SELECT * FROM posts LIMIT 10`                             | ~1ms  | 10   | idx_posts_created_at          |
| `SELECT * FROM posts WHERE author_id = ?`                  | ~5ms  | 50   | idx_posts_author_id           |
| `SELECT * FROM posts ORDER BY created_at DESC`             | ~2ms  | 100  | idx_posts_created_at          |
| `SELECT * FROM posts p JOIN FETCH p.author`                | ~3ms  | 100  | idx_posts_author_id           |
| `SELECT * FROM posts WHERE author_id = ? AND title LIKE ?` | ~10ms | 5    | idx_posts_author_id (partial) |

---

## Backup & Recovery

### PostgreSQL Dump

```bash
# Backup entire database
pg_dump -h localhost -U postgres -d biursite > biursite.sql

# Restore from backup
psql -h localhost -U postgres -d biursite < biursite.sql
```

### Point-in-Time Recovery (Production)

```bash
# Enable WAL archiving in postgresql.conf
wal_level = replica
archive_mode = on
archive_command = 'cp %p /backup/wal_archives/%f'

# Restore to specific time
pg_restore --recovery-target-time='2025-01-18 14:30:00'
  /backup/biursite.sql
```

---

## Capacity Planning

### Storage Estimates

| Item           | Count   | Size       | Total       |
| -------------- | ------- | ---------- | ----------- |
| User record    | 1,000   | ~400 bytes | ~400 KB     |
| Post record    | 100,000 | ~800 bytes | ~80 MB      |
| Total database | -       | -          | **~100 MB** |

Scaling projections:

- 1M users: ~400 MB
- 10M posts: ~8 GB
- Indexes: ~2-3 GB additional

---

## Monitoring Queries

### User Statistics

```sql
SELECT
    role,
    COUNT(*) as user_count,
    MAX(created_at) as latest_user
FROM users
GROUP BY role;
```

### Post Statistics

```sql
SELECT
    author_id,
    COUNT(*) as post_count,
    MAX(created_at) as latest_post
FROM posts
GROUP BY author_id
ORDER BY post_count DESC
LIMIT 10;
```

### Database Size

```sql
SELECT
    pg_database.datname,
    pg_size_pretty(pg_database_size(pg_database.datname))
FROM pg_database;

-- Table sizes
SELECT
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename))
FROM pg_tables
WHERE schemaname != 'pg_catalog'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;
```

---

## Data Integrity Checks

### Orphaned Records

```sql
-- Check for posts with non-existent authors
SELECT p.id, p.author_id
FROM posts p
LEFT JOIN users u ON p.author_id = u.id
WHERE u.id IS NULL;

-- Should return 0 rows (cascade delete prevents this)
```

### Duplicate Users

```sql
-- Check for duplicate usernames (shouldn't exist with unique constraint)
SELECT username, COUNT(*)
FROM users
GROUP BY username
HAVING COUNT(*) > 1;

-- Should return 0 rows
```

---

## Expected Data Distribution

### Development Environment

- 5-10 users
- 50-100 posts
- ~1-5 MB total

### Production Environment (1000 users)

- 1,000 users
- 50,000 posts
- ~50-100 MB total

### Scaling (10M users)

- 10,000,000 users
- 500,000,000 posts
- ~400-500 GB total
- Consider data partitioning at this scale

---

## Future Enhancements

### Addition: Soft Deletes

```java
@Entity
public class Post {
    @Column(nullable = true)
    private Instant deletedAt;

    @Query("SELECT p FROM Post p WHERE p.deletedAt IS NULL")
    List<Post> findAllActive();
}

// Benefit: Recover deleted posts, audit trail
// Trade-off: Filters for deleted_at on every query
```

### Addition: Audit Trail

```java
@Entity
@Table(name = "audit_log")
public class AuditLog {
    private Long id;
    private String entity;      // "Post", "User"
    private Long entityId;
    private String action;      // "CREATE", "UPDATE", "DELETE"
    private String oldValues;   // JSON
    private String newValues;   // JSON
    private Long userId;
    private Instant timestamp;
}
```

### Addition: Tagging System

```java
@Entity
public class Tag {
    private Long id;
    private String name;

    @ManyToMany
    private Set<Post> posts;
}

// Benefit: Categorize posts, search by tag
```

---

## Conclusion

BiUrSite's database design prioritizes:

- ✅ **Simplicity**: 2 main tables + clear relationships
- ✅ **Performance**: Strategic indexes on access patterns
- ✅ **Integrity**: Constraints ensure data consistency
- ✅ **Scalability**: N+1 prevention, pagination support
- ✅ **Maintainability**: Clear schema with foreign key relationships

The schema is ready for production deployment and can scale to millions of records with proper indexing and query optimization.
