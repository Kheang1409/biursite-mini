package com.biursite.infrastructure.persistence;

import com.biursite.domain.user.entity.User;
import com.biursite.domain.user.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(UserRepositoryAdapter.class)
@AutoConfigureTestDatabase(replace = Replace.ANY)
class UserRepositoryAdapterIT {

    @Autowired
    private UserRepositoryAdapter adapter;

    @Autowired
    private UserRepository jpaRepo;

    @BeforeEach
    void cleanup() {
        jpaRepo.deleteAll();
    }

    @Test
    void createAndFindByIdAndUsername() {
        User u = User.builder().username("tester").email("t@x.com").password("p").role(Role.ROLE_USER).build();
        User saved = adapter.save(u);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();

        var byId = adapter.findById(saved.getId());
        assertThat(byId).isPresent();
        assertThat(byId.get().getEmail()).isEqualTo("t@x.com");

        var byUsername = adapter.findByUsername("tester");
        assertThat(byUsername).isPresent();
    }

    @Test
    void updateUser() {
        User u = User.builder().username("updater").email("u@x.com").password("p").role(Role.ROLE_USER).build();
        User saved = adapter.save(u);

        saved.setEmail("new@x.com");
        User updated = adapter.save(saved);

        assertThat(updated.getEmail()).isEqualTo("new@x.com");
    }

    @Test
    void deleteUser() {
        User u = User.builder().username("deleter").email("d@x.com").password("p").role(Role.ROLE_USER).build();
        User saved = adapter.save(u);
        adapter.delete(saved);
        assertThat(adapter.findById(saved.getId())).isEmpty();
    }

    @Test
    void paginationAndFilter() {
        for (int i = 0; i < 7; i++) {
            User u = User.builder().username("user" + i).email("u" + i + "@x.com").password("p").role(Role.ROLE_USER).banned(i % 2 == 0).build();
            adapter.save(u);
        }

        var pageList = adapter.findAll(0, 3);
        assertThat(pageList.size()).isEqualTo(3);

        List<User> banned = adapter.findAllWithFilter(null, true, 0, 10);
        assertThat(banned).allMatch(User::getBanned);
    }
}
