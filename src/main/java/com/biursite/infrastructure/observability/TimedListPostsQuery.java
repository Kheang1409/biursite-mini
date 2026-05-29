package com.biursite.infrastructure.observability;

import com.biursite.application.query.ListPostsQuery;
import com.biursite.application.query.SearchStrategy;
import com.biursite.application.query.dto.PostSummaryDto;
import com.biursite.application.shared.pagination.Page;
import com.biursite.application.shared.pagination.PageRequest;
import com.biursite.infrastructure.config.QueryProperties;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.TimeUnit;

public class TimedListPostsQuery implements ListPostsQuery {
    private static final Logger log = LoggerFactory.getLogger(TimedListPostsQuery.class);
    private final ListPostsQuery delegate;
    private final MeterRegistry meterRegistry;
    private final SearchStrategy searchStrategy;
    private final QueryProperties queryProperties;

    public TimedListPostsQuery(ListPostsQuery delegate, MeterRegistry meterRegistry, SearchStrategy searchStrategy, QueryProperties queryProperties) {
        this.delegate = delegate;
        this.meterRegistry = meterRegistry;
        this.searchStrategy = searchStrategy;
        this.queryProperties = queryProperties;
    }

    @Override
    public Page<PostSummaryDto> execute(String query, PageRequest pageRequest) {
        String normalized = searchStrategy.normalize(query);
        int size = Math.min(pageRequest.getSize(), queryProperties.getMaxPageSize());
        PageRequest safeRequest = PageRequest.of(pageRequest.getPage(), size);
        long start = System.nanoTime();
        boolean success = true;
        try {
            Page<PostSummaryDto> result = delegate.execute(normalized, safeRequest);
            return result;
        } catch (RuntimeException ex) {
            success = false;
            throw ex;
        } finally {
            long duration = System.nanoTime() - start;
            Timer.builder("query.execution")
                    .tag("queryName", "posts.list")
                    .tag("outcome", success ? "success" : "failure")
                    .tag("cacheHit", "false")
                    .register(meterRegistry)
                    .record(duration, TimeUnit.NANOSECONDS);
            log.info("query=posts.list q={} page={} size={} cacheHit=false durationMs={}",
                    normalized, safeRequest.getPage(), safeRequest.getSize(), TimeUnit.NANOSECONDS.toMillis(duration));
        }
    }
}
