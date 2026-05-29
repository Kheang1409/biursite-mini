package com.biursite.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.util.List;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        Cache postsList = new CaffeineCache(
                "query.posts.list",
                Caffeine.newBuilder()
                        .expireAfterWrite(Duration.ofSeconds(60))
                        .recordStats()
                        .maximumSize(1000)
                        .build()
        );
        Cache postById = new CaffeineCache(
                "query.posts.detail",
                Caffeine.newBuilder()
                        .expireAfterWrite(Duration.ofMinutes(10))
                        .recordStats()
                        .maximumSize(5000)
                        .build()
        );
        Cache usersList = new CaffeineCache(
                "query.users.list",
                Caffeine.newBuilder()
                        .expireAfterWrite(Duration.ofMinutes(3))
                        .recordStats()
                        .maximumSize(1000)
                        .build()
        );
        Cache userById = new CaffeineCache(
                "query.users.byId",
                Caffeine.newBuilder()
                        .expireAfterWrite(Duration.ofMinutes(5))
                        .recordStats()
                        .maximumSize(5000)
                        .build()
        );

        SimpleCacheManager manager = new SimpleCacheManager();
        manager.setCaches(List.of(postsList, postById, usersList, userById));
        return manager;
    }
}
