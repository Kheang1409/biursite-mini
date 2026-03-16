package com.biursite.infrastructure.persistence.mapper;

import com.biursite.application.post.mapper.PostViewMapper;
import com.biursite.application.post.dto.PostView;
import com.biursite.domain.post.entity.Post;

import java.util.List;

public class PostViewMapperImpl implements PostViewMapper {
    @Override
    public PostView toView(Post post) {
        return PostMapper.toView(post);
    }

    @Override
    public List<PostView> toViewList(List<Post> posts) {
        return PostMapper.toViewList(posts);
    }
}
