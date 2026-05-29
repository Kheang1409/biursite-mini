-- Optional index strategy for high-scale reads.
-- Apply manually or via a migration tool if enabled later.

CREATE INDEX IF NOT EXISTS idx_posts_created_at_desc ON posts (created_at DESC);
CREATE INDEX IF NOT EXISTS idx_posts_title ON posts (title);
CREATE INDEX IF NOT EXISTS idx_posts_content ON posts (content);
CREATE INDEX IF NOT EXISTS idx_users_name ON users (username);

-- PostgreSQL full-text placeholder (optional):
-- CREATE INDEX IF NOT EXISTS idx_posts_fts ON posts USING GIN (to_tsvector('english', title || ' ' || content));
