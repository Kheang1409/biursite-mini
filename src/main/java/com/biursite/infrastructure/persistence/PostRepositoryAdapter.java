package com.biursite.infrastructure.persistence;

import com.biursite.domain.post.repository.PostRepositoryPort;
import com.biursite.domain.post.entity.Post;
import com.biursite.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public Page<Post> findAllWithAuthor(Pageable pageable) {
        return PostEntityMapper.toDomainPage(postRepository.findAllWithAuthor(pageable));
    }

    @Override
    public Page<Post> findAllWithAuthorVisible(Pageable pageable) {
        return PostEntityMapper.toDomainPage(postRepository.findAllWithAuthorVisible(pageable));
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
