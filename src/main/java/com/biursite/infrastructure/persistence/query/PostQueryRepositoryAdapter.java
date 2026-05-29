package com.biursite.infrastructure.persistence.query;

import com.biursite.application.query.PostQueryRepository;
import com.biursite.application.query.dto.PostDetailDto;
import com.biursite.application.query.dto.PostSummaryDto;
import com.biursite.application.shared.pagination.Page;
import com.biursite.application.shared.pagination.PageImpl;
import com.biursite.application.shared.pagination.PageRequest;
import com.biursite.infrastructure.config.QueryProperties;
import com.biursite.infrastructure.persistence.PostRepository;
import com.biursite.infrastructure.projection.PostReadModelEntity;
import com.biursite.infrastructure.projection.PostReadModelRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class PostQueryRepositoryAdapter implements PostQueryRepository {
    private static final Logger log = LoggerFactory.getLogger(PostQueryRepositoryAdapter.class);
    private final PostRepository postRepository;
    private final QueryProperties queryProperties;
    private final PostReadModelRepository postReadModelRepository;

    public PostQueryRepositoryAdapter(PostRepository postRepository,
                                      QueryProperties queryProperties,
                                      PostReadModelRepository postReadModelRepository) {
        this.postRepository = postRepository;
        this.queryProperties = queryProperties;
        this.postReadModelRepository = postReadModelRepository;
    }

    @Override
    public Page<PostSummaryDto> findPostList(String query, PageRequest pageRequest) {
        org.springframework.data.domain.Pageable spReq = org.springframework.data.domain.PageRequest.of(pageRequest.getPage(), pageRequest.getSize());
        if (queryProperties.getFeatures().isUseAsyncProjections()) {
            org.springframework.data.domain.Page<PostReadModelEntity> springPage = postReadModelRepository.findSummaries(query, spReq);
            List<PostSummaryDto> content = springPage.getContent().stream()
                    .map(this::toSummaryDto)
                    .toList();
            if (content.isEmpty()) {
                log.warn("asyncProjectionEmpty=true page={} size={} q={} fallback=live", spReq.getPageNumber(), spReq.getPageSize(), query);
                return fallbackToLiveProjection(query, spReq);
            }
            return new PageImpl<>(content, springPage.getNumber(), springPage.getSize(), springPage.getTotalElements());
        }
        if (queryProperties.getFeatures().isUseMaterializedView()) {
            // Materialized view can lag; keep a safe fallback to live projections.
            org.springframework.data.domain.Page<PostSummaryViewProjection> springPage = postRepository.findPostSummaryView(query, spReq);
            List<PostSummaryDto> content = springPage.getContent().stream()
                    .map(this::toSummaryDto)
                    .toList();
            return new PageImpl<>(content, springPage.getNumber(), springPage.getSize(), springPage.getTotalElements());
        }
        org.springframework.data.domain.Page<PostSummaryDto> springPage = postRepository.findPostSummaries(
                query, queryProperties.getSearch().getMaxLength(), spReq);
        return new PageImpl<>(springPage.getContent(), springPage.getNumber(), springPage.getSize(), springPage.getTotalElements());
    }

    @Override
    public List<PostSummaryDto> findPostListKeyset(String query, Instant beforeCreatedAt, Long beforeId, int size) {
        int limit = Math.min(size, queryProperties.getMaxPageSize());
        org.springframework.data.domain.Pageable spReq = org.springframework.data.domain.PageRequest.of(0, limit);
        if (queryProperties.getFeatures().isUseAsyncProjections()) {
            List<PostReadModelEntity> rows = postReadModelRepository.findSummariesAfter(query, beforeCreatedAt, beforeId, spReq);
            return rows.stream().map(this::toSummaryDto).toList();
        }
        return postRepository.findPostSummariesAfter(
                query, beforeCreatedAt, beforeId, queryProperties.getSearch().getMaxLength(), spReq);
    }

    @Override
    public PostDetailDto findPostDetail(Long id) {
        Optional<PostDetailDto> view = postRepository.findPostDetail(id);
        return view.orElse(null);
    }

    private PostSummaryDto toSummaryDto(PostSummaryViewProjection projection) {
        return new PostSummaryDto(
                projection.getId(),
                projection.getTitle(),
                projection.getExcerpt(),
                projection.getAuthorName(),
                projection.getCreatedAt()
        );
    }

    private PostSummaryDto toSummaryDto(PostReadModelEntity entity) {
        return new PostSummaryDto(
                entity.getId(),
                entity.getTitle(),
                entity.getExcerpt(),
                entity.getAuthorName(),
                entity.getCreatedAt()
        );
    }

    private Page<PostSummaryDto> fallbackToLiveProjection(String query, org.springframework.data.domain.Pageable spReq) {
        org.springframework.data.domain.Page<PostSummaryDto> springPage = postRepository.findPostSummaries(
                query, queryProperties.getSearch().getMaxLength(), spReq);
        return new PageImpl<>(springPage.getContent(), springPage.getNumber(), springPage.getSize(), springPage.getTotalElements());
    }
}
