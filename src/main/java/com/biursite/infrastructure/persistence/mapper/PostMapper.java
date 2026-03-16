package com.biursite.infrastructure.persistence.mapper;

import com.biursite.application.post.dto.PostView;
import com.biursite.domain.post.entity.Post;

import java.util.List;
import java.util.stream.Collectors;

public final class PostMapper {
    private PostMapper() {}

    public static PostView toView(Post p) {
        if (p == null) return null;
        return new PostView(
                p.getId(),
                p.getTitle(),
                p.getContent(),
            p.getAuthor() == null ? null : p.getAuthor().getUsername(),
            p.getAuthor() == null ? null : p.getAuthor().getId(),
            p.getCreatedAt(),
            p.getUpdatedAt()
        );
    }

    public static List<PostView> toViewList(List<Post> posts) {
        return posts.stream().map(PostMapper::toView).collect(Collectors.toList());
    }
}
