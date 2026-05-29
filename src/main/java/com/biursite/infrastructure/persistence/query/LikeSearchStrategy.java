package com.biursite.infrastructure.persistence.query;

import com.biursite.application.query.SearchStrategy;

public class LikeSearchStrategy implements SearchStrategy {
    private final int maxLength;

    public LikeSearchStrategy(int maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public String normalize(String raw) {
        if (raw == null) {
            return null;
        }
        String trimmed = raw.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        if (trimmed.length() > maxLength) {
            return trimmed.substring(0, maxLength);
        }
        return trimmed;
    }

    @Override
    public boolean supportsFullText() {
        return false;
    }
}
