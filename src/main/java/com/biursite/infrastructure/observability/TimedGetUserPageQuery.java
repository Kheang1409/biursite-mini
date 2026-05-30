package com.biursite.infrastructure.observability;

import com.biursite.application.query.GetUserPageQuery;
import com.biursite.application.query.SearchStrategy;
import com.biursite.application.query.dto.UserSummaryDto;
import com.biursite.application.shared.pagination.Page;
import com.biursite.application.shared.pagination.PageRequest;
import com.biursite.infrastructure.config.QueryProperties;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import java.util.concurrent.TimeUnit;

public class TimedGetUserPageQuery implements GetUserPageQuery {
    private static final Logger log = LoggerFactory.getLogger(TimedGetUserPageQuery.class);
    private final GetUserPageQuery delegate;
    private final MeterRegistry meterRegistry;
    private final SearchStrategy searchStrategy;
    private final QueryProperties queryProperties;

    public TimedGetUserPageQuery(GetUserPageQuery delegate, MeterRegistry meterRegistry, SearchStrategy searchStrategy, QueryProperties queryProperties) {
        this.delegate = delegate;
        this.meterRegistry = meterRegistry;
        this.searchStrategy = searchStrategy;
        this.queryProperties = queryProperties;
    }

    @Override
    @Cacheable(cacheNames = "query.users.list", key = "'p=' + #pageRequest.getPage() + '&s=' + #pageRequest.getSize() + '&q=' + T(com.biursite.application.query.QueryKeyUtil).normalize(#query, 200) + '&b=' + (#banned == null ? '' : #banned)", sync = true)
    public Page<UserSummaryDto> execute(String query, Boolean banned, PageRequest pageRequest) {
        String normalized = searchStrategy.normalize(query);
        int size = Math.min(pageRequest.getSize(), queryProperties.getMaxPageSize());
        PageRequest safeRequest = PageRequest.of(pageRequest.getPage(), size);
        long start = System.nanoTime();
        boolean success = true;
        try {
            return delegate.execute(normalized, banned, safeRequest);
        } catch (RuntimeException ex) {
            success = false;
            throw ex;
        } finally {
            long duration = System.nanoTime() - start;
            Timer.builder("query.execution")
                    .tag("queryName", "users.page")
                    .tag("outcome", success ? "success" : "failure")
                    .tag("cacheHit", "false")
                    .register(meterRegistry)
                    .record(duration, TimeUnit.NANOSECONDS);
            log.info("query=users.page q={} banned={} page={} size={} cacheHit=false durationMs={}",
                    normalized, banned, safeRequest.getPage(), safeRequest.getSize(), TimeUnit.NANOSECONDS.toMillis(duration));
        }
    }
}
