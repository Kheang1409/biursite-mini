package com.biursite.domain.post.entity;

import lombok.*;
import java.time.Instant;

import com.biursite.domain.user.entity.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {
    private Long id;

    private String title;

    private String content;

    private User author;

    private Instant createdAt;

    private Instant updatedAt;

    @Builder.Default
    private Boolean banned = false;

    private String banReason;
}
