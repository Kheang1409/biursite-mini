package com.biursite.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

	@Bean("eventsExecutor")
	public Executor eventsExecutor() {
		ThreadPoolTaskExecutor t = new ThreadPoolTaskExecutor();
		t.setCorePoolSize(4);
		t.setMaxPoolSize(20);
		t.setQueueCapacity(100);
		t.setThreadNamePrefix("events-");
		t.initialize();
		return t;
	}
}
