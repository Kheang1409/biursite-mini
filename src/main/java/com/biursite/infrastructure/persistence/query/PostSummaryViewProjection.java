package com.biursite.infrastructure.persistence.query;

import java.time.Instant;

public interface PostSummaryViewProjection {
    Long getId();
    String getTitle();
    String getExcerpt();
    String getAuthorName();
    Instant getCreatedAt();
}
