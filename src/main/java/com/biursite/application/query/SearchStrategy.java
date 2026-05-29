package com.biursite.application.query;

public interface SearchStrategy {
    String normalize(String raw);
    boolean supportsFullText();
}
