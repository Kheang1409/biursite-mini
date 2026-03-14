package com.biursite.service;

import com.biursite.dto.CreatePostRequest;
import com.biursite.dto.PostDTO;
import com.biursite.dto.UpdatePostRequest;
import com.biursite.domain.post.entity.Post;

import java.util.List;

public interface PostService {
    PostDTO toDto(Post post);
    List<PostDTO> getAll(int page, int size);
    PostDTO getById(Long id);
    PostDTO create(CreatePostRequest req, Long currentUserId);
    PostDTO update(Long id, UpdatePostRequest req, Long currentUserId);
    void delete(Long id, Long currentUserId);
}
