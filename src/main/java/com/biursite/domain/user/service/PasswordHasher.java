package com.biursite.domain.user.service;

public interface PasswordHasher {
    String hash(String raw);
}
