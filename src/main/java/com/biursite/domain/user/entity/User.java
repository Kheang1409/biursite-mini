package com.biursite.domain.user.entity;

import lombok.*;
import java.time.Instant;
import com.biursite.domain.shared.valueobject.Email;
import com.biursite.domain.shared.valueobject.Username;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Long id;

    private Long version;

    private String username;

    private String email;

    private String password;

    private Role role;

    @Builder.Default
    private Boolean banned = false;

    @Builder.Default
    private Boolean deactivated = false;

    private Instant createdAt;

    public static User register(String username, String email, String passwordHash, Role role, Instant createdAt) {
        String normalizedUsername = Username.of(username).getValue();
        String normalizedEmail = Email.of(email).getValue();
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        Role resolvedRole = role == null ? Role.ROLE_USER : role;
        Instant created = createdAt == null ? Instant.now() : createdAt;
        return User.builder()
                .username(normalizedUsername)
                .email(normalizedEmail)
                .password(passwordHash)
                .role(resolvedRole)
                .createdAt(created)
                .build();
    }

    public void updateProfile(String username, String email) {
        this.username = Username.of(username).getValue();
        this.email = Email.of(email).getValue();
    }

    public void updatePassword(String passwordHash) {
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        this.password = passwordHash;
    }
}
