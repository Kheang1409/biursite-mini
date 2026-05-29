package com.biursite.infrastructure.observability;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class QueryBenchmarkLogger {
    private static final Logger log = LoggerFactory.getLogger(QueryBenchmarkLogger.class);

    private QueryBenchmarkLogger() {}

    public static <T> T benchmark(String queryName, Supplier<T> supplier) {
        long coldStart = System.nanoTime();
        T first = supplier.get();
        long coldDuration = System.nanoTime() - coldStart;

        long cachedStart = System.nanoTime();
        supplier.get();
        long cachedDuration = System.nanoTime() - cachedStart;

        log.info("queryBenchmark={} coldMs={} cachedMs={} ratio={}",
                queryName,
                TimeUnit.NANOSECONDS.toMillis(coldDuration),
                TimeUnit.NANOSECONDS.toMillis(cachedDuration),
                cachedDuration == 0 ? "n/a" : String.format("%.2f", (double) coldDuration / cachedDuration));
        return first;
    }
}
