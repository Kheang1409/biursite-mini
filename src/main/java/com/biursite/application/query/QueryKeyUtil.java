package com.biursite.application.query;

public final class QueryKeyUtil {
    private QueryKeyUtil() {}

    public static String normalize(String raw, int maxLength) {
        if (raw == null) {
            return "";
        }
        String trimmed = raw.trim();
        if (trimmed.isEmpty()) {
            return "";
        }
        if (trimmed.length() > maxLength) {
            return trimmed.substring(0, maxLength);
        }
        return trimmed;
    }
}
