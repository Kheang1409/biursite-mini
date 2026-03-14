package com.biursite.domain.post.event;

import com.biursite.domain.shared.event.DomainEvent;

public final class PostCreatedEvent extends DomainEvent {
    private final Long postId;
    private final String title;
    private final Long authorId;
    private final String authorUsername;

    public PostCreatedEvent(Long postId, String title, Long authorId, String authorUsername) {
        super();
        this.postId = postId;
        this.title = title;
        this.authorId = authorId;
        this.authorUsername = authorUsername;
    }

    public Long getPostId() {
        return postId;
    }

    public String getTitle() {
        return title;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }
}
