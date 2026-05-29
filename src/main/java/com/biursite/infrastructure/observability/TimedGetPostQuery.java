package com.biursite.infrastructure.observability;

import com.biursite.application.query.GetPostQuery;
import com.biursite.application.query.dto.PostDetailDto;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import java.util.concurrent.TimeUnit;

public class TimedGetPostQuery implements GetPostQuery {
    private static final Logger log = LoggerFactory.getLogger(TimedGetPostQuery.class);
    private final GetPostQuery delegate;
    private final MeterRegistry meterRegistry;

    public TimedGetPostQuery(GetPostQuery delegate, MeterRegistry meterRegistry) {
        this.delegate = delegate;
        this.meterRegistry = meterRegistry;
    }

    @Override
    @Cacheable(cacheNames = "query.posts.detail", key = "#id", sync = true)
    public PostDetailDto execute(Long id) {
        long start = System.nanoTime();
        boolean success = true;
        try {
            return delegate.execute(id);
        } catch (RuntimeException ex) {
            success = false;
            throw ex;
        } finally {
            long duration = System.nanoTime() - start;
            Timer.builder("query.execution")
                    .tag("queryName", "posts.get")
                    .tag("outcome", success ? "success" : "failure")
                    .tag("cacheHit", "false")
                    .register(meterRegistry)
                    .record(duration, TimeUnit.NANOSECONDS);
            log.info("query=posts.get postId={} cacheHit=false durationMs={}",
                    id, TimeUnit.NANOSECONDS.toMillis(duration));
        }
    }
}
