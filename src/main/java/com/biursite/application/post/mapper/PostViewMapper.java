package com.biursite.application.post.mapper;

import com.biursite.application.post.dto.PostView;
import com.biursite.domain.post.entity.Post;

import java.util.List;

public interface PostViewMapper {
    PostView toView(Post post);
    List<PostView> toViewList(List<Post> posts);
}
