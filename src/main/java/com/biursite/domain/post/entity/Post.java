package com.biursite.domain.post.entity;

import lombok.*;
import java.time.Instant;

import com.biursite.domain.post.valueobject.PostContent;
import com.biursite.domain.post.valueobject.PostTitle;
import com.biursite.domain.shared.exception.DomainForbiddenException;
import com.biursite.domain.user.entity.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {
    private Long id;

    private Long version;

    private String title;

    private String content;

    private User author;

    private Instant createdAt;

    private Instant updatedAt;

    @Builder.Default
    private Boolean banned = false;

    private String banReason;

    public static Post create(String title, String content, User author, Instant createdAt) {
        if (author == null) {
            throw new IllegalArgumentException("Author is required");
        }
        String normalizedTitle = PostTitle.of(title).getValue();
        String normalizedContent = PostContent.of(content).getValue();
        Instant created = createdAt == null ? Instant.now() : createdAt;
        return Post.builder()
                .title(normalizedTitle)
                .content(normalizedContent)
                .author(author)
                .createdAt(created)
                .build();
    }

    public void updateBy(Long actorId, String title, String content, Instant updatedAt) {
        requireAuthor(actorId);
        this.title = PostTitle.of(title).getValue();
        this.content = PostContent.of(content).getValue();
        this.updatedAt = updatedAt == null ? Instant.now() : updatedAt;
    }

    public void deleteBy(Long actorId, boolean isAdmin) {
        if (isAdmin) {
            return;
        }
        requireAuthor(actorId);
    }

    private void requireAuthor(Long actorId) {
        if (author == null || actorId == null || author.getId() == null) {
            throw new DomainForbiddenException("Not allowed to modify this post");
        }
        if (!author.getId().equals(actorId)) {
            throw new DomainForbiddenException("Not allowed to modify this post");
        }
    }
}
