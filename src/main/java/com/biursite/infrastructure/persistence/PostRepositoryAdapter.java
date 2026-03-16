package com.biursite.infrastructure.persistence;

import com.biursite.domain.post.repository.PostRepositoryPort;
import com.biursite.domain.post.entity.Post;
import com.biursite.domain.user.entity.User;
import org.springframework.stereotype.Repository;

import com.biursite.application.shared.pagination.Page;
import com.biursite.application.shared.pagination.PageImpl;
import java.util.List;
import java.util.Optional;

@Repository
public class PostRepositoryAdapter implements PostRepositoryPort {
    private final PostRepository postRepository;

    public PostRepositoryAdapter(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public Optional<Post> findById(Long id) {
        return postRepository.findById(id).map(PostEntityMapper::toDomain);
    }

    @Override
    public Optional<Post> findByIdWithAuthor(Long id) {
        return postRepository.findByIdWithAuthor(id).map(PostEntityMapper::toDomain);
    }

    @Override
    public List<Post> findAll() {
        return PostEntityMapper.toDomainList(postRepository.findAll());
    }
    @Override
    public List<Post> findAllWithAuthor(int page, int size) {
        org.springframework.data.domain.Pageable spReq = org.springframework.data.domain.PageRequest.of(page, size);
        org.springframework.data.domain.Page<PostEntity> springPage = postRepository.findAllWithAuthor(spReq);
        return PostEntityMapper.toDomainList(springPage.getContent());
    }

    // Adapter-level convenience: return application Page<Post>
    public Page<Post> findAllWithAuthorPage(int page, int size) {
        org.springframework.data.domain.Pageable spReq = org.springframework.data.domain.PageRequest.of(page, size);
        org.springframework.data.domain.Page<PostEntity> springPage = postRepository.findAllWithAuthor(spReq);
        List<Post> content = PostEntityMapper.toDomainList(springPage.getContent());
        return new PageImpl<>(content, springPage.getNumber(), springPage.getSize(), springPage.getTotalElements());
    }

    @Override
    public long countAllWithAuthor() {
        // Use a count query via Spring Data page request to get total elements
        org.springframework.data.domain.Pageable spReq = org.springframework.data.domain.PageRequest.of(0, 1);
        org.springframework.data.domain.Page<PostEntity> springPage = postRepository.findAllWithAuthor(spReq);
        return springPage.getTotalElements();
    }

    @Override
    public List<Post> findAllWithAuthorVisible(int page, int size) {
        org.springframework.data.domain.Pageable spReq = org.springframework.data.domain.PageRequest.of(page, size);
        org.springframework.data.domain.Page<PostEntity> springPage = postRepository.findAllWithAuthorVisible(spReq);
        return PostEntityMapper.toDomainList(springPage.getContent());
    }

    // Adapter-level convenience: return application Page<Post>
    public Page<Post> findAllWithAuthorVisiblePage(int page, int size) {
        org.springframework.data.domain.Pageable spReq = org.springframework.data.domain.PageRequest.of(page, size);
        org.springframework.data.domain.Page<PostEntity> springPage = postRepository.findAllWithAuthorVisible(spReq);
        List<Post> content = PostEntityMapper.toDomainList(springPage.getContent());
        return new PageImpl<>(content, springPage.getNumber(), springPage.getSize(), springPage.getTotalElements());
    }

    @Override
    public long countAllWithAuthorVisible() {
        org.springframework.data.domain.Pageable spReq = org.springframework.data.domain.PageRequest.of(0, 1);
        org.springframework.data.domain.Page<PostEntity> springPage = postRepository.findAllWithAuthorVisible(spReq);
        return springPage.getTotalElements();
    }

    @Override
    public List<Post> findByAuthorOrderByCreatedAtDesc(User author) {
        var authorEntity = UserEntityMapper.toEntity(author);
        return PostEntityMapper.toDomainList(postRepository.findByAuthorOrderByCreatedAtDesc(authorEntity));
    }

    @Override
    public Post save(Post post) {
        var ent = PostEntityMapper.toEntity(post);
        var saved = postRepository.save(ent);
        return PostEntityMapper.toDomain(saved);
    }

    @Override
    public void delete(Post post) {
        var ent = PostEntityMapper.toEntity(post);
        postRepository.delete(ent);
    }
}
