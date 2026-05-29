package com.biursite.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.biursite.application.query.dto.UserSummaryDto;

public interface UserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);

    @Query(value = "SELECT new com.biursite.application.query.dto.UserSummaryDto(" +
            "u.id, u.version, u.username, u.email, u.role, u.banned, u.deactivated, u.createdAt) " +
            "FROM UserEntity u " +
            "WHERE (:query IS NULL OR :query = '' OR " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND (:banned IS NULL OR u.banned = :banned) " +
            "ORDER BY u.createdAt DESC, u.id DESC",
            countQuery = "SELECT count(u) FROM UserEntity u " +
                    "WHERE (:query IS NULL OR :query = '' OR " +
                    "LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) " +
                    "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))) " +
                    "AND (:banned IS NULL OR u.banned = :banned)")
    Page<UserSummaryDto> findUserListItems(@Param("query") String query, @Param("banned") Boolean banned, Pageable pageable);

    @Query("SELECT new com.biursite.application.query.dto.UserSummaryDto(" +
            "u.id, u.version, u.username, u.email, u.role, u.banned, u.deactivated, u.createdAt) " +
            "FROM UserEntity u WHERE u.id = :id")
    Optional<UserSummaryDto> findUserListItem(@Param("id") Long id);
}
