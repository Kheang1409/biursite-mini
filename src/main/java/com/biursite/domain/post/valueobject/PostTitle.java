package com.biursite.domain.post.valueobject;

import java.util.Objects;

public final class PostTitle {
    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 255;

    private final String value;

    private PostTitle(String value) {
        this.value = value;
    }

    public static PostTitle of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Post title cannot be empty");
        }
        String trimmed = value.trim();
        if (trimmed.length() < MIN_LENGTH) {
            throw new IllegalArgumentException("Post title must have at least " + MIN_LENGTH + " character");
        }
        if (trimmed.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Post title cannot exceed " + MAX_LENGTH + " characters");
        }
        return new PostTitle(trimmed);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PostTitle postTitle = (PostTitle) obj;
        return Objects.equals(value, postTitle.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
