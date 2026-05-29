package com.biursite.infrastructure.projection;

import com.biursite.domain.post.event.PostCreatedEvent;
import com.biursite.domain.post.event.PostDeletedEvent;
import com.biursite.domain.post.event.PostUpdatedEvent;
import com.biursite.infrastructure.persistence.PostRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

@Component
public class PostProjectionHandler {
    private static final Logger log = LoggerFactory.getLogger(PostProjectionHandler.class);
    private static final int EXCERPT_LENGTH = 200;
    private static final int RETRY_ATTEMPTS = 3;

    private final PostRepository postRepository;
    private final PostReadModelRepository postReadModelRepository;
    private final CacheManager cacheManager;
    private final MeterRegistry meterRegistry;

    public PostProjectionHandler(PostRepository postRepository,
                                 PostReadModelRepository postReadModelRepository,
                                 CacheManager cacheManager,
                                 MeterRegistry meterRegistry) {
        this.postRepository = postRepository;
        this.postReadModelRepository = postReadModelRepository;
        this.cacheManager = cacheManager;
        this.meterRegistry = meterRegistry;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("eventsExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onPostCreated(PostCreatedEvent event) {
        applyWithRetry("post.created", event.getPostId(), () -> upsertProjection(event.getPostId()));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("eventsExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onPostUpdated(PostUpdatedEvent event) {
        applyWithRetry("post.updated", event.getPostId(), () -> upsertProjection(event.getPostId()));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("eventsExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onPostDeleted(PostDeletedEvent event) {
        applyWithRetry("post.deleted", event.getPostId(), () -> deleteProjection(event.getPostId()));
    }

    private void upsertProjection(Long postId) {
        Optional<com.biursite.application.query.dto.PostDetailDto> detail = postRepository.findPostDetail(postId);
        if (detail.isEmpty()) {
            return;
        }
        var dto = detail.get();
        String excerpt = buildExcerpt(dto.content());
        String authorName = dto.authorName();
        postReadModelRepository.upsert(
                dto.id(),
                dto.title(),
                excerpt,
                authorName,
                dto.createdAt(),
                dto.updatedAt()
        );
        log.info("Projection updated: postId={}, title={}", dto.id(), dto.title());
        evictPostCaches(dto.id());
    }

    private void deleteProjection(Long postId) {
        postReadModelRepository.deleteByIdNative(postId);
        evictPostCaches(postId);
    }

    private String buildExcerpt(String content) {
        if (content == null) {
            return "";
        }
        String trimmed = content.trim();
        if (trimmed.length() <= EXCERPT_LENGTH) {
            return trimmed;
        }
        return trimmed.substring(0, EXCERPT_LENGTH);
    }

    private void evictPostCaches(Long postId) {
        evictAll("query.posts.list");
        evictKey("query.posts.detail", postId);
    }

    private void evictAll(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        }
    }

    private void evictKey(String cacheName, Object key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
        }
    }

    private void applyWithRetry(String eventName, Long targetId, Runnable action) {
        long start = System.nanoTime();
        boolean success = false;
        RuntimeException failure = null;
        for (int attempt = 1; attempt <= RETRY_ATTEMPTS; attempt++) {
            try {
                action.run();
                success = true;
                break;
            } catch (RuntimeException ex) {
                failure = ex;
                log.warn("projectionEvent={} targetId={} status=retry attempt={}", eventName, targetId, attempt, ex);
            }
        }

        long duration = System.nanoTime() - start;
        Timer.builder("projection.execution")
                .tag("projection", "post")
                .tag("event", eventName)
                .tag("outcome", success ? "success" : "failure")
                .register(meterRegistry)
                .record(duration, TimeUnit.NANOSECONDS);

        if (success) {
            log.info("projectionEvent={} targetId={} status=success durationMs={}",
                    eventName, targetId, TimeUnit.NANOSECONDS.toMillis(duration));
        } else if (failure != null) {
            log.error("Projection failed: postId={}", targetId, failure);
            log.error("projectionEvent={} targetId={} status=failure durationMs={}",
                    eventName, targetId, TimeUnit.NANOSECONDS.toMillis(duration), failure);
        }
    }
}
