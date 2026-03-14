package com.biursite.domain.shared.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;

public final class Username {
    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 50;
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+$");

    private final String value;

    private Username(String value) {
        this.value = value;
    }

    public static Username of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        String trimmed = value.trim();
        if (trimmed.length() < MIN_LENGTH) {
            throw new IllegalArgumentException("Username must be at least " + MIN_LENGTH + " characters");
        }
        if (trimmed.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Username cannot exceed " + MAX_LENGTH + " characters");
        }
        if (!USERNAME_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("Username can only contain letters, numbers, underscores and hyphens");
        }
        return new Username(trimmed);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Username username = (Username) obj;
        return Objects.equals(value.toLowerCase(), username.value.toLowerCase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(value.toLowerCase());
    }

    @Override
    public String toString() {
        return value;
    }
}
