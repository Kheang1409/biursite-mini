package com.biursite.service;

import com.biursite.dto.PostDTO;
import com.biursite.entity.Post;
import java.util.List;

public interface PostService {
    PostDTO toDto(Post post);
    List<PostDTO> getAll();
    PostDTO getById(Long id);
    PostDTO create(Post post);
    PostDTO update(Long id, Post post, Long currentUserId);
    void delete(Long id, Long currentUserId, boolean isAdmin);
}
