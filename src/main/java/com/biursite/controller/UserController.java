package com.biursite.controller;

import com.biursite.application.user.dto.CreateUserRequest;
import com.biursite.application.user.dto.UpdateUserRequest;
import com.biursite.application.user.dto.UserDto;
import com.biursite.security.SecurityService;
import com.biursite.application.user.usecase.CreateUserUseCase;
import com.biursite.application.user.usecase.GetUserByIdUseCase;
import com.biursite.application.user.usecase.GetAllUsersUseCase;
import com.biursite.application.user.usecase.UpdateUserUseCase;
import com.biursite.application.user.usecase.BanUnbanDeleteUserUseCases;
import com.biursite.application.user.mapper.UserMapper;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final CreateUserUseCase createUserUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final GetAllUsersUseCase getAllUsersUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final BanUnbanDeleteUserUseCases banUnbanDeleteUserUseCases;
    private final SecurityService securityService;

    public UserController(CreateUserUseCase createUserUseCase,
                          GetUserByIdUseCase getUserByIdUseCase,
                          GetAllUsersUseCase getAllUsersUseCase,
                          UpdateUserUseCase updateUserUseCase,
                          BanUnbanDeleteUserUseCases banUnbanDeleteUserUseCases,
                          SecurityService securityService) {
        this.createUserUseCase = createUserUseCase;
        this.getUserByIdUseCase = getUserByIdUseCase;
        this.getAllUsersUseCase = getAllUsersUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.banUnbanDeleteUserUseCases = banUnbanDeleteUserUseCases;
        this.securityService = securityService;
    }

    @GetMapping
    @RolesAllowed({"ADMIN"})
    public ResponseEntity<List<UserDto>> all() {
        return ResponseEntity.ok(
                getAllUsersUseCase.execute().stream().map(UserMapper::toDto).toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable Long id, Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        var user = getUserByIdUseCase.execute(id);
        var dto = UserMapper.toDto(user);

        if (!isAdmin) {
            String currentUsername = auth.getName();
            if (!dto.getUsername().equals(currentUsername)) {
                return ResponseEntity.status(403).build();
            }
        }
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @RolesAllowed({"ADMIN"})
    public ResponseEntity<UserDto> create(@Valid @RequestBody CreateUserRequest req) {
        var cmd = new com.biursite.application.user.dto.CreateUserCommand(req.getUsername(), req.getEmail(), req.getPassword());
        var created = createUserUseCase.execute(cmd);
        return ResponseEntity.status(201).body(UserMapper.toDto(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable Long id, 
                                          @Valid @RequestBody UpdateUserRequest req, 
                                          Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            var current = securityService.getCurrentUser()
                    .orElseThrow(() -> new IllegalStateException("No authenticated user"));

            if (!current.getId().equals(id)) {
                return ResponseEntity.status(403).build();
            }
        }

        var updated = updateUserUseCase.execute(id, req);
        return ResponseEntity.ok(UserMapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    @RolesAllowed({"ADMIN"})
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        banUnbanDeleteUserUseCases.delete(id);
        return ResponseEntity.noContent().build();
    }
}
