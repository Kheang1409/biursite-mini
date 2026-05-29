package com.biursite.infrastructure.projection;

import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostReadModelRepository extends JpaRepository<PostReadModelEntity, Long> {

    @Query(value = "SELECT p FROM PostReadModelEntity p " +
            "WHERE (:query IS NULL OR :query = '' OR " +
            "LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(p.excerpt) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "ORDER BY p.createdAt DESC, p.id DESC")
    Page<PostReadModelEntity> findSummaries(@Param("query") String query, Pageable pageable);

    @Query(value = "SELECT p FROM PostReadModelEntity p " +
            "WHERE (:query IS NULL OR :query = '' OR " +
            "LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(p.excerpt) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND (:beforeCreatedAt IS NULL OR :beforeId IS NULL " +
            "OR p.createdAt < :beforeCreatedAt " +
            "OR (p.createdAt = :beforeCreatedAt AND p.id < :beforeId)) " +
            "ORDER BY p.createdAt DESC, p.id DESC")
    List<PostReadModelEntity> findSummariesAfter(
            @Param("query") String query,
            @Param("beforeCreatedAt") Instant beforeCreatedAt,
            @Param("beforeId") Long beforeId,
            Pageable pageable);

    default void upsert(Long id,
            String title,
            String excerpt,
            String authorName,
            Instant createdAt,
            Instant updatedAt) {
        save(new PostReadModelEntity(id, title, excerpt, authorName, createdAt, updatedAt));
    }

    @Modifying
    @Query(value = "DELETE FROM post_read_model WHERE id = :id", nativeQuery = true)
    void deleteByIdNative(@Param("id") Long id);

    @Modifying
    @Query(value = "UPDATE post_read_model SET author_name = :authorName WHERE id IN :ids", nativeQuery = true)
    void updateAuthorNameByIds(@Param("authorName") String authorName, @Param("ids") List<Long> ids);
}
