package com.biursite.application.shared.security;

import java.time.Instant;

public final class CurrentUser {
    private final Long id;
    private final String username;
    private final String email;
    private final String role;
    private final Boolean banned;
    private final Boolean deactivated;
    private final Instant createdAt;

    public CurrentUser(Long id, String username, String email, String role, Boolean banned, Boolean deactivated, Instant createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.banned = banned;
        this.deactivated = deactivated;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() { return email; }

    public String getRole() { return role; }

    public Boolean getBanned() { return banned; }

    public Boolean getDeactivated() { return deactivated; }

    public Instant getCreatedAt() { return createdAt; }
}
