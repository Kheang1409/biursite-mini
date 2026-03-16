package com.biursite.infrastructure.config;

import com.biursite.application.post.usecase.CreatePostService;
import com.biursite.application.post.usecase.CreatePostUseCase;
import com.biursite.application.post.usecase.DeletePostService;
import com.biursite.application.post.usecase.DeletePostUseCase;
import com.biursite.application.post.usecase.GetPostService;
import com.biursite.application.post.usecase.GetPostUseCase;
import com.biursite.application.post.usecase.ListPostsService;
import com.biursite.application.post.usecase.ListPostsUseCase;
import com.biursite.application.post.usecase.UpdatePostService;
import com.biursite.application.post.usecase.UpdatePostUseCase;
import com.biursite.application.user.usecase.BanUnbanDeleteUserUseCases;
import com.biursite.application.user.usecase.CreateUserUseCase;
import com.biursite.application.user.usecase.GetAllUsersUseCase;
import com.biursite.application.user.usecase.GetUserByIdUseCase;
import com.biursite.application.user.usecase.GetUserPageService;
import com.biursite.application.user.usecase.GetUserPageUseCase;
import com.biursite.application.user.usecase.GetUserProfilePostsService;
import com.biursite.application.user.usecase.GetUserProfilePostsUseCase;
import com.biursite.application.user.usecase.RegisterUserService;
import com.biursite.application.user.usecase.RegisterUserUseCase;
import com.biursite.application.user.usecase.UpdateUserService;
import com.biursite.application.user.usecase.UpdateUserUseCase;
import com.biursite.application.user.usecase.DeactivateAccountService;
import com.biursite.application.user.usecase.DeactivateAccountUseCase;
import com.biursite.domain.shared.event.DomainEventPublisher;
import com.biursite.domain.post.repository.PostRepositoryPort;
import com.biursite.domain.user.repository.UserRepositoryPort;
import com.biursite.domain.user.service.PasswordHasher;
import com.biursite.application.post.mapper.PostViewMapper;
import com.biursite.application.user.mapper.UserDtoMapper;
import com.biursite.infrastructure.persistence.mapper.PostViewMapperImpl;
import com.biursite.infrastructure.persistence.mapper.UserDtoMapperImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public GetUserPageUseCase getUserPageUseCase(UserRepositoryPort userRepository) {
        return new GetUserPageService(userRepository, userDtoMapper());
    }

    @Bean
    public GetUserByIdUseCase getUserByIdUseCase(UserRepositoryPort userRepository) {
        return new GetUserByIdUseCase(userRepository, userDtoMapper());
    }

    @Bean
    public BanUnbanDeleteUserUseCases banUnbanDeleteUserUseCases(UserRepositoryPort userRepository) {
        return new BanUnbanDeleteUserUseCases(userRepository);
    }

    @Bean
    public GetUserProfilePostsUseCase getUserProfilePostsUseCase(PostRepositoryPort postRepository, UserRepositoryPort userRepository) {
        return new GetUserProfilePostsService(postRepository, userRepository, postViewMapper());
    }

    @Bean
    public DeactivateAccountUseCase deactivateAccountUseCase(UserRepositoryPort userRepository) {
        return new DeactivateAccountService(userRepository);
    }

    @Bean
    public UpdateUserUseCase updateUserUseCase(UserRepositoryPort userRepository, PasswordHasher passwordHasher) {
        return new UpdateUserService(userRepository, passwordHasher, userDtoMapper());
    }

    @Bean
    public RegisterUserUseCase registerUserUseCase(UserRepositoryPort userRepository, PasswordHasher passwordHasher) {
        return new RegisterUserService(userRepository, passwordHasher);
    }

    @Bean
    public CreateUserUseCase createUserUseCase(UserRepositoryPort userRepository, PasswordHasher passwordHasher, DomainEventPublisher eventPublisher) {
        return new CreateUserUseCase(userRepository, passwordHasher, eventPublisher);
    }

    @Bean
    public GetAllUsersUseCase getAllUsersUseCase(UserRepositoryPort userRepository) {
        return new GetAllUsersUseCase(userRepository, userDtoMapper());
    }

    @Bean
    public CreatePostUseCase createPostUseCase(PostRepositoryPort postRepository, UserRepositoryPort userRepository, DomainEventPublisher eventPublisher) {
        return new CreatePostService(postRepository, userRepository, eventPublisher);
    }

    @Bean
    public UpdatePostUseCase updatePostUseCase(PostRepositoryPort postRepository, DomainEventPublisher eventPublisher) {
        return new UpdatePostService(postRepository, eventPublisher);
    }

    @Bean
    public ListPostsUseCase listPostsUseCase(PostRepositoryPort postRepository) {
        return new ListPostsService(postRepository, postViewMapper());
    }

    @Bean
    public GetPostUseCase getPostUseCase(PostRepositoryPort postRepository) {
        return new GetPostService(postRepository, postViewMapper());
    }

    @Bean
    public DeletePostUseCase deletePostUseCase(PostRepositoryPort postRepository, UserRepositoryPort userRepository, DomainEventPublisher eventPublisher) {
        return new DeletePostService(postRepository, userRepository, eventPublisher);
    }

    @Bean
    public PostViewMapper postViewMapper() {
        return new PostViewMapperImpl();
    }

    @Bean
    public UserDtoMapper userDtoMapper() {
        return new UserDtoMapperImpl();
    }
}
