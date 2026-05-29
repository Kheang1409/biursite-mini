package com.biursite.infrastructure.observability;

import com.biursite.application.post.dto.PostView;
import com.biursite.application.post.usecase.ListPostsUseCase;
import com.biursite.application.shared.pagination.Page;
import com.biursite.application.shared.pagination.PageRequest;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

public class TimedListPostsUseCase implements ListPostsUseCase {
    private final ListPostsUseCase delegate;
    private final Timer timer;

    public TimedListPostsUseCase(ListPostsUseCase delegate, MeterRegistry meterRegistry) {
        this.delegate = delegate;
        this.timer = Timer.builder("usecase.posts.list").register(meterRegistry);
    }

    @Override
    public Page<PostView> execute(PageRequest pageRequest) {
        return timer.record(() -> delegate.execute(pageRequest));
    }
}
