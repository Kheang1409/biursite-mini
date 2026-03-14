package com.biursite.domain.post.event;

import com.biursite.domain.shared.event.DomainEvent;

public final class PostDeletedEvent extends DomainEvent {
    private final Long postId;
    private final String title;
    private final Long deletedByUserId;

    public PostDeletedEvent(Long postId, String title, Long deletedByUserId) {
        super();
        this.postId = postId;
        this.title = title;
        this.deletedByUserId = deletedByUserId;
    }

    public Long getPostId() {
        return postId;
    }

    public String getTitle() {
        return title;
    }

    public Long getDeletedByUserId() {
        return deletedByUserId;
    }
}
