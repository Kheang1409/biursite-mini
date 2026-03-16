package com.biursite.infrastructure.web;

import com.biursite.application.user.dto.CreateUserRequest;
import com.biursite.application.user.dto.CreateUserCommand;
import com.biursite.application.user.dto.UpdateUserRequest;
import com.biursite.application.user.dto.UserDto;
import com.biursite.application.user.usecase.CreateUserUseCase;
import com.biursite.application.user.usecase.GetUserByIdUseCase;
import com.biursite.application.user.usecase.GetAllUsersUseCase;
import com.biursite.application.user.usecase.UpdateUserUseCase;
import com.biursite.application.user.usecase.BanUnbanDeleteUserUseCases;
import com.biursite.application.shared.security.CurrentUserPort;
import com.biursite.application.shared.exception.UnauthorizedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserControllerAdapter {
    private final CreateUserUseCase createUserUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final GetAllUsersUseCase getAllUsersUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final BanUnbanDeleteUserUseCases banUnbanDeleteUserUseCases;
    private final CurrentUserPort currentUserPort;

    public UserControllerAdapter(CreateUserUseCase createUserUseCase,
                                 GetUserByIdUseCase getUserByIdUseCase,
                                 GetAllUsersUseCase getAllUsersUseCase,
                                 UpdateUserUseCase updateUserUseCase,
                                 BanUnbanDeleteUserUseCases banUnbanDeleteUserUseCases,
                                 CurrentUserPort currentUserPort) {
        this.createUserUseCase = createUserUseCase;
        this.getUserByIdUseCase = getUserByIdUseCase;
        this.getAllUsersUseCase = getAllUsersUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.banUnbanDeleteUserUseCases = banUnbanDeleteUserUseCases;
        this.currentUserPort = currentUserPort;
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> all() {
        if (!currentUserPort.currentUserHasRole("ROLE_ADMIN")) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(getAllUsersUseCase.execute());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable Long id) {
        boolean isAdmin = currentUserPort.currentUserHasRole("ROLE_ADMIN");
        var dto = getUserByIdUseCase.execute(id);

        if (!isAdmin) {
            var current = currentUserPort.getCurrentUser().orElseThrow(() -> new UnauthorizedException("Not authenticated"));
            if (!dto.getUsername().equals(current.getUsername())) {
                return ResponseEntity.status(403).build();
            }
        }
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@Valid @RequestBody CreateUserRequest req) {
        if (!currentUserPort.currentUserHasRole("ROLE_ADMIN")) {
            return ResponseEntity.status(403).build();
        }
        var created = createUserUseCase.execute(new CreateUserCommand(req.getUsername(), req.getEmail(), req.getPassword()));
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest req) {
        boolean isAdmin = currentUserPort.currentUserHasRole("ROLE_ADMIN");

        if (!isAdmin) {
            var current = currentUserPort.getCurrentUser().orElseThrow(() -> new UnauthorizedException("Not authenticated"));
            if (!current.getId().equals(id)) {
                return ResponseEntity.status(403).build();
            }
        }

        var updated = updateUserUseCase.execute(id, req);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean isAdmin = currentUserPort.currentUserHasRole("ROLE_ADMIN");
        if (!isAdmin) {
            return ResponseEntity.status(403).build();
        }
        banUnbanDeleteUserUseCases.delete(id);
        return ResponseEntity.noContent().build();
    }
}
