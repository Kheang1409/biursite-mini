package com.biursite.infrastructure.persistence;

import com.biursite.domain.post.entity.Post;
import com.biursite.domain.user.entity.User;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.stream.Collectors;

public final class PostEntityMapper {
    private PostEntityMapper() {}

    public static Post toDomain(PostEntity e) {
        if (e == null) return null;
        User author = UserEntityMapper.toDomain(e.getAuthor());
        return Post.builder()
                .id(e.getId())
                .title(e.getTitle())
                .content(e.getContent())
                .author(author)
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .banned(e.getBanned())
                .banReason(e.getBanReason())
                .build();
    }

    public static PostEntity toEntity(Post p) {
        if (p == null) return null;
        UserEntity authorEntity = UserEntityMapper.toEntity(p.getAuthor());
        Instant created = p.getCreatedAt() == null ? Instant.now() : p.getCreatedAt();
        return PostEntity.builder()
                .id(p.getId())
                .title(p.getTitle())
                .content(p.getContent())
                .author(authorEntity)
                .createdAt(created)
                .updatedAt(p.getUpdatedAt())
                .banned(p.getBanned() == null ? Boolean.FALSE : p.getBanned())
                .banReason(p.getBanReason())
                .build();
    }

    public static List<Post> toDomainList(List<PostEntity> list) {
        return list.stream().map(PostEntityMapper::toDomain).collect(Collectors.toList());
    }

    public static Page<Post> toDomainPage(Page<PostEntity> page) {
        List<Post> content = toDomainList(page.getContent());
        return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
    }
}
