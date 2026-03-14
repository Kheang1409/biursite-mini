package com.biursite.domain.post.valueobject;

import java.util.Objects;

public final class PostContent {
    private static final int MAX_LENGTH = 50000;

    private final String value;

    private PostContent(String value) {
        this.value = value;
    }

    public static PostContent of(String value) {
        if (value == null) {
            return new PostContent("");
        }
        String trimmed = value.trim();
        if (trimmed.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Post content cannot exceed " + MAX_LENGTH + " characters");
        }
        return new PostContent(trimmed);
    }

    public static PostContent empty() {
        return new PostContent("");
    }

    public String getValue() {
        return value;
    }

    public boolean isEmpty() {
        return value.isEmpty();
    }

    public String getPreview(int maxLength) {
        if (value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength) + "...";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PostContent that = (PostContent) obj;
        return Objects.equals(value, that.value);
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
