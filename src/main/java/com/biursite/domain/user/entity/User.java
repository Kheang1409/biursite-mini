package com.biursite.domain.user.entity;

import lombok.*;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Long id;

    private String username;

    private String email;

    private String password;

    private Role role;

    @Builder.Default
    private Boolean banned = false;

    @Builder.Default
    private Boolean deactivated = false;

    private Instant createdAt;
}
