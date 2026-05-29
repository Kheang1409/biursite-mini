package com.biursite.infrastructure.projection;

import com.biursite.domain.post.event.PostCreatedEvent;
import com.biursite.domain.shared.event.DomainEventPublisher;
import com.biursite.domain.user.entity.Role;
import com.biursite.infrastructure.persistence.PostEntity;
import com.biursite.infrastructure.persistence.PostRepository;
import com.biursite.infrastructure.persistence.UserEntity;
import com.biursite.infrastructure.persistence.UserRepository;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

@Component
@Profile("dev")
public class DevProjectionRunner implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DevProjectionRunner.class);

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostReadModelRepository postReadModelRepository;
    private final DomainEventPublisher eventPublisher;
    private final TransactionTemplate transactionTemplate;

    public DevProjectionRunner(UserRepository userRepository,
                               PostRepository postRepository,
                               PostReadModelRepository postReadModelRepository,
                               DomainEventPublisher eventPublisher,
                               TransactionTemplate transactionTemplate) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.postReadModelRepository = postReadModelRepository;
        this.eventPublisher = eventPublisher;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public void run(String... args) {
        if (postRepository.count() > 0) {
            return;
        }

        transactionTemplate.executeWithoutResult(status -> {
            UserEntity author = userRepository.save(UserEntity.builder()
                    .username("dev_author")
                    .email("dev_author@example.com")
                    .password("dev-pass")
                    .role(Role.ROLE_USER)
                    .createdAt(Instant.now())
                    .build());

            createPost(author, "Hello CQRS", "First projection demo post.");
            createPost(author, "Async projections", "This post should appear in the read model.");
            createPost(author, "Read model sync", "Verifying eventual consistency is in place.");
        });

        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        List<PostReadModelEntity> rows = postReadModelRepository.findAll();
        log.info("projectionCheck rows={}", rows.size());
        for (PostReadModelEntity row : rows) {
            log.info("projectionRow id={} title={} author={} createdAt={}",
                    row.getId(), row.getTitle(), row.getAuthorName(), row.getCreatedAt());
        }
    }

    private void createPost(UserEntity author, String title, String content) {
        PostEntity post = postRepository.save(PostEntity.builder()
                .title(title)
                .content(content)
                .author(author)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .banned(false)
                .build());
        eventPublisher.publish(new PostCreatedEvent(post.getId(), post.getTitle(), author.getId(), author.getUsername()));
    }
}
