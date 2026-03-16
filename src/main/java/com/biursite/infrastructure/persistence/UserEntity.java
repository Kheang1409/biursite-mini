package com.biursite.infrastructure.persistence;

import com.biursite.domain.user.entity.Role;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Builder.Default
    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean banned = false;

    @Builder.Default
    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean deactivated = false;

    @Column(nullable = false)
    private Instant createdAt;
}
