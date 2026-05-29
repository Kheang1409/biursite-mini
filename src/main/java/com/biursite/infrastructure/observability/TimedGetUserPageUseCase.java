package com.biursite.infrastructure.observability;

import com.biursite.application.shared.pagination.Page;
import com.biursite.application.shared.pagination.PageRequest;
import com.biursite.application.user.dto.UserDto;
import com.biursite.application.user.usecase.GetUserPageUseCase;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

public class TimedGetUserPageUseCase implements GetUserPageUseCase {
    private final GetUserPageUseCase delegate;
    private final Timer timer;

    public TimedGetUserPageUseCase(GetUserPageUseCase delegate, MeterRegistry meterRegistry) {
        this.delegate = delegate;
        this.timer = Timer.builder("usecase.users.page").register(meterRegistry);
    }

    @Override
    public Page<UserDto> execute(String query, Boolean banned, PageRequest pageRequest) {
        return timer.record(() -> delegate.execute(query, banned, pageRequest));
    }
}
