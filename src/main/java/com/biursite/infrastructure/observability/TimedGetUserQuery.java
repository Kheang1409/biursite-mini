package com.biursite.infrastructure.observability;

import com.biursite.application.query.GetUserQuery;
import com.biursite.application.query.dto.UserSummaryDto;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import java.util.concurrent.TimeUnit;

public class TimedGetUserQuery implements GetUserQuery {
    private static final Logger log = LoggerFactory.getLogger(TimedGetUserQuery.class);
    private final GetUserQuery delegate;
    private final MeterRegistry meterRegistry;

    public TimedGetUserQuery(GetUserQuery delegate, MeterRegistry meterRegistry) {
        this.delegate = delegate;
        this.meterRegistry = meterRegistry;
    }

    @Override
    @Cacheable(cacheNames = "query.users.byId", key = "#id", sync = true)
    public UserSummaryDto execute(Long id) {
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
                    .tag("queryName", "users.get")
                    .tag("outcome", success ? "success" : "failure")
                    .tag("cacheHit", "false")
                    .register(meterRegistry)
                    .record(duration, TimeUnit.NANOSECONDS);
            log.info("query=users.get userId={} cacheHit=false durationMs={}",
                    id, TimeUnit.NANOSECONDS.toMillis(duration));
        }
    }
}
