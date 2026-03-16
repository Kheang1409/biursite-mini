package com.biursite.infrastructure.events;

import com.biursite.domain.post.event.PostCreatedEvent;
import com.biursite.domain.post.event.PostDeletedEvent;
import com.biursite.domain.post.event.PostUpdatedEvent;
import com.biursite.domain.user.event.UserRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class DomainEventListeners {
    private static final Logger log = LoggerFactory.getLogger(DomainEventListeners.class);

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("eventsExecutor")
    public void onUserRegistered(UserRegisteredEvent event) {
        log.info("User registered: id={}, username={}, email={}", 
                event.getUserId(), event.getUsername(), event.getEmail());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("eventsExecutor")
    public void onPostCreated(PostCreatedEvent event) {
        log.info("Post created: id={}, title={}, author={}", 
                event.getPostId(), event.getTitle(), event.getAuthorUsername());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("eventsExecutor")
    public void onPostUpdated(PostUpdatedEvent event) {
        log.info("Post updated: id={}, title={}, updatedBy={}", 
                event.getPostId(), event.getTitle(), event.getUpdatedByUserId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("eventsExecutor")
    public void onPostDeleted(PostDeletedEvent event) {
        log.info("Post deleted: id={}, title={}, deletedBy={}", 
                event.getPostId(), event.getTitle(), event.getDeletedByUserId());
    }
}
