-- Optional materialized view for post summaries.
-- Apply manually or via a migration tool if enabled later.

CREATE MATERIALIZED VIEW IF NOT EXISTS post_summary_view AS
SELECT
    p.id,
    p.title,
    SUBSTRING(COALESCE(p.content, ''), 1, 200) AS excerpt,
    u.username AS author_name,
    p.created_at
FROM posts p
JOIN users u ON u.id = p.author_id
WHERE p.banned = false AND u.deactivated = false
ORDER BY p.created_at DESC, p.id DESC;

-- Optional refresh statement:
-- REFRESH MATERIALIZED VIEW CONCURRENTLY post_summary_view;
