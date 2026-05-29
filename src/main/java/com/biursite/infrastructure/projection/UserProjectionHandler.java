package com.biursite.infrastructure.projection;

import com.biursite.domain.user.event.UserUpdatedEvent;
import com.biursite.infrastructure.persistence.PostRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.List;
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
public class UserProjectionHandler {
    private static final Logger log = LoggerFactory.getLogger(UserProjectionHandler.class);
    private static final int RETRY_ATTEMPTS = 3;

    private final PostRepository postRepository;
    private final PostReadModelRepository postReadModelRepository;
    private final CacheManager cacheManager;
    private final MeterRegistry meterRegistry;

    public UserProjectionHandler(PostRepository postRepository,
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
    public void onUserUpdated(UserUpdatedEvent event) {
        applyWithRetry("user.updated", event.getUserId(), () -> updateAuthorName(event.getUserId(), event.getUsername()));
    }

    private void updateAuthorName(Long userId, String username) {
        List<Long> postIds = postRepository.findPostIdsByAuthorId(userId);
        if (postIds.isEmpty()) {
            return;
        }
        postReadModelRepository.updateAuthorNameByIds(username, postIds);
        evictAll("query.posts.list");
    }

    private void evictAll(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
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
                .tag("projection", "user")
                .tag("event", eventName)
                .tag("outcome", success ? "success" : "failure")
                .register(meterRegistry)
                .record(duration, TimeUnit.NANOSECONDS);

        if (success) {
            log.info("projectionEvent={} targetId={} status=success durationMs={}",
                    eventName, targetId, TimeUnit.NANOSECONDS.toMillis(duration));
        } else if (failure != null) {
            log.error("projectionEvent={} targetId={} status=failure durationMs={}",
                    eventName, targetId, TimeUnit.NANOSECONDS.toMillis(duration), failure);
        }
    }
}
