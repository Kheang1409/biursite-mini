package com.biursite.infrastructure.config;

import com.biursite.application.query.GetPostQuery;
import com.biursite.application.query.GetPostQueryService;
import com.biursite.application.query.GetUserPageQuery;
import com.biursite.application.query.GetUserPageQueryService;
import com.biursite.application.query.GetUserQuery;
import com.biursite.application.query.GetUserQueryService;
import com.biursite.application.query.ListPostsQuery;
import com.biursite.application.query.ListPostsQueryService;
import com.biursite.application.query.PostQueryRepository;
import com.biursite.application.query.SearchStrategy;
import com.biursite.application.query.UserQueryRepository;
import com.biursite.infrastructure.observability.TimedGetPostQuery;
import com.biursite.infrastructure.observability.TimedGetUserPageQuery;
import com.biursite.infrastructure.observability.TimedGetUserQuery;
import com.biursite.infrastructure.observability.TimedListPostsQuery;
import com.biursite.infrastructure.persistence.query.LikeSearchStrategy;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(QueryProperties.class)
public class QueryConfig {

    @Bean
    public ListPostsQuery listPostsQuery(PostQueryRepository postQueryRepository, MeterRegistry meterRegistry, QueryProperties queryProperties) {
        ListPostsQuery core = new ListPostsQueryService(postQueryRepository);
        return new TimedListPostsQuery(core, meterRegistry, searchStrategy(queryProperties), queryProperties);
    }

    @Bean
    public GetPostQuery getPostQuery(PostQueryRepository postQueryRepository, MeterRegistry meterRegistry) {
        GetPostQuery core = new GetPostQueryService(postQueryRepository);
        return new TimedGetPostQuery(core, meterRegistry);
    }

    @Bean
    public GetUserPageQuery getUserPageQuery(UserQueryRepository userQueryRepository, MeterRegistry meterRegistry, QueryProperties queryProperties) {
        GetUserPageQuery core = new GetUserPageQueryService(userQueryRepository);
        return new TimedGetUserPageQuery(core, meterRegistry, searchStrategy(queryProperties), queryProperties);
    }

    @Bean
    public GetUserQuery getUserQuery(UserQueryRepository userQueryRepository, MeterRegistry meterRegistry) {
        GetUserQuery core = new GetUserQueryService(userQueryRepository);
        return new TimedGetUserQuery(core, meterRegistry);
    }

    @Bean
    public SearchStrategy searchStrategy(QueryProperties queryProperties) {
        return new LikeSearchStrategy(queryProperties.getSearch().getMaxLength());
    }
}
