package com.biursite.domain.post.event;

import com.biursite.domain.shared.event.DomainEvent;

public final class PostUpdatedEvent extends DomainEvent {
    private final Long postId;
    private final String title;
    private final Long updatedByUserId;

    public PostUpdatedEvent(Long postId, String title, Long updatedByUserId) {
        super();
        this.postId = postId;
        this.title = title;
        this.updatedByUserId = updatedByUserId;
    }

    public Long getPostId() {
        return postId;
    }

    public String getTitle() {
        return title;
    }

    public Long getUpdatedByUserId() {
        return updatedByUserId;
    }
}
