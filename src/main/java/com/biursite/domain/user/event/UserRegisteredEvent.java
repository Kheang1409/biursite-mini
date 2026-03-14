package com.biursite.domain.user.event;

import com.biursite.domain.shared.event.DomainEvent;

public final class UserRegisteredEvent extends DomainEvent {
    private final Long userId;
    private final String username;
    private final String email;

    public UserRegisteredEvent(Long userId, String username, String email) {
        super();
        this.userId = userId;
        this.username = username;
        this.email = email;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}
