package com.biursite.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import com.biursite.application.query.dto.PostDetailDto;
import com.biursite.application.query.dto.PostSummaryDto;
import com.biursite.infrastructure.persistence.query.PostSummaryViewProjection;


public interface PostRepository extends JpaRepository<PostEntity, Long> {
    List<PostEntity> findByAuthorId(Long authorId);

    @Query("SELECT p.id FROM PostEntity p WHERE p.author.id = :authorId")
    List<Long> findPostIdsByAuthorId(@Param("authorId") Long authorId);

    List<PostEntity> findByAuthorOrderByCreatedAtDesc(UserEntity author);

    @Query("SELECT p FROM PostEntity p JOIN FETCH p.author")
    List<PostEntity> findAllWithAuthor();

    @Query(value = "SELECT p FROM PostEntity p JOIN FETCH p.author",
           countQuery = "SELECT count(p) FROM PostEntity p")
    Page<PostEntity> findAllWithAuthor(Pageable pageable);

        @Query(value = "SELECT p FROM PostEntity p JOIN FETCH p.author WHERE p.banned = false AND p.author.deactivated = false",
            countQuery = "SELECT count(p) FROM PostEntity p WHERE p.banned = false AND p.author.deactivated = false")
        Page<PostEntity> findAllWithAuthorVisible(Pageable pageable);

    @Query("SELECT p FROM PostEntity p JOIN FETCH p.author WHERE p.id = :id")
    Optional<PostEntity> findByIdWithAuthor(@Param("id") Long id);

        @Query(value = "SELECT new com.biursite.application.query.dto.PostSummaryDto(" +
            "p.id, p.title, SUBSTRING(COALESCE(p.content, ''), 1, :excerptLength), a.username, p.createdAt) " +
            "FROM PostEntity p JOIN p.author a " +
            "WHERE p.banned = false AND a.deactivated = false " +
            "AND (:query IS NULL OR :query = '' OR " +
            "LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "ORDER BY p.createdAt DESC, p.id DESC",
            countQuery = "SELECT count(p) FROM PostEntity p JOIN p.author a " +
                "WHERE p.banned = false AND a.deactivated = false " +
                "AND (:query IS NULL OR :query = '' OR " +
                "LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
                "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%'))) ")
        Page<PostSummaryDto> findPostSummaries(@Param("query") String query, @Param("excerptLength") int excerptLength, Pageable pageable);

        @Query("SELECT new com.biursite.application.query.dto.PostSummaryDto(" +
            "p.id, p.title, SUBSTRING(COALESCE(p.content, ''), 1, :excerptLength), a.username, p.createdAt) " +
            "FROM PostEntity p JOIN p.author a " +
            "WHERE p.banned = false AND a.deactivated = false " +
            "AND (:query IS NULL OR :query = '' OR " +
            "LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND (:beforeCreatedAt IS NULL OR :beforeId IS NULL " +
            "OR p.createdAt < :beforeCreatedAt " +
            "OR (p.createdAt = :beforeCreatedAt AND p.id < :beforeId)) " +
            "ORDER BY p.createdAt DESC, p.id DESC")
        List<PostSummaryDto> findPostSummariesAfter(
                @Param("query") String query,
                @Param("beforeCreatedAt") Instant beforeCreatedAt,
                @Param("beforeId") Long beforeId,
                @Param("excerptLength") int excerptLength,
                Pageable pageable);

        @Query("SELECT new com.biursite.application.query.dto.PostDetailDto(" +
            "p.id, p.version, p.title, p.content, a.username, a.id, p.createdAt, p.updatedAt, p.banned, p.banReason) " +
            "FROM PostEntity p JOIN p.author a " +
            "WHERE p.id = :id AND p.banned = false AND a.deactivated = false")
        Optional<PostDetailDto> findPostDetail(@Param("id") Long id);

        @Query(value = "SELECT v.id AS id, v.title AS title, v.excerpt AS excerpt, " +
            "v.author_name AS authorName, v.created_at AS createdAt " +
            "FROM post_summary_view v " +
            "WHERE (:query IS NULL OR :query = '' OR " +
            "LOWER(v.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(v.excerpt) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "ORDER BY v.created_at DESC, v.id DESC",
            countQuery = "SELECT count(1) FROM post_summary_view v " +
                "WHERE (:query IS NULL OR :query = '' OR " +
                "LOWER(v.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
                "OR LOWER(v.excerpt) LIKE LOWER(CONCAT('%', :query, '%'))) ",
            nativeQuery = true)
        Page<PostSummaryViewProjection> findPostSummaryView(@Param("query") String query, Pageable pageable);
}
