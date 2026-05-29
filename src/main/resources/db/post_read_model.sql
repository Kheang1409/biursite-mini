-- Denormalized read model for post summaries.
-- Apply manually or via a migration tool if enabled later.

CREATE TABLE IF NOT EXISTS post_read_model (
    id BIGINT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    excerpt TEXT,
    author_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_post_read_model_created_at_desc ON post_read_model (created_at DESC, id DESC);
CREATE INDEX IF NOT EXISTS idx_post_read_model_title ON post_read_model (title);
CREATE INDEX IF NOT EXISTS idx_post_read_model_excerpt ON post_read_model (excerpt);
