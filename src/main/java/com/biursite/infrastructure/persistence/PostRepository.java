package com.biursite.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface PostRepository extends JpaRepository<PostEntity, Long> {
    List<PostEntity> findByAuthorId(Long authorId);

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
}
