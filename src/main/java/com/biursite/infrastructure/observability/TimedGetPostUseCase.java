package com.biursite.infrastructure.observability;

import com.biursite.application.post.dto.PostView;
import com.biursite.application.post.usecase.GetPostUseCase;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

public class TimedGetPostUseCase implements GetPostUseCase {
    private final GetPostUseCase delegate;
    private final Timer timer;

    public TimedGetPostUseCase(GetPostUseCase delegate, MeterRegistry meterRegistry) {
        this.delegate = delegate;
        this.timer = Timer.builder("usecase.posts.get").register(meterRegistry);
    }

    @Override
    public PostView execute(Long id) {
        return timer.record(() -> delegate.execute(id));
    }
}
