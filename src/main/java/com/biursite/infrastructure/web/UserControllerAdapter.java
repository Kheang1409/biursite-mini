package com.biursite.infrastructure.web;

import com.biursite.application.user.dto.CreateUserRequest;
import com.biursite.application.user.dto.CreateUserCommand;
import com.biursite.application.user.dto.UpdateUserRequest;
import com.biursite.application.user.dto.UserDto;
import com.biursite.application.user.usecase.CreateUserUseCase;
import com.biursite.application.query.GetUserQuery;
import com.biursite.application.query.GetUserPageQuery;
import com.biursite.application.user.usecase.UpdateUserUseCase;
import com.biursite.application.user.usecase.BanUnbanDeleteUserUseCases;
import com.biursite.application.shared.security.CurrentUserPort;
import com.biursite.application.shared.exception.UnauthorizedException;
import com.biursite.application.shared.exception.ForbiddenException;
import com.biursite.application.shared.pagination.PageRequest;
import com.biursite.infrastructure.web.dto.ApiResponse;
import com.biursite.infrastructure.web.dto.PaginationMeta;
import com.biursite.infrastructure.web.mapper.QueryDtoMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserControllerAdapter {
    private final CreateUserUseCase createUserUseCase;
    private final GetUserQuery getUserQuery;
    private final GetUserPageQuery getUserPageQuery;
    private final UpdateUserUseCase updateUserUseCase;
    private final BanUnbanDeleteUserUseCases banUnbanDeleteUserUseCases;
    private final CurrentUserPort currentUserPort;

    public UserControllerAdapter(CreateUserUseCase createUserUseCase,
                                 GetUserQuery getUserQuery,
                                 GetUserPageQuery getUserPageQuery,
                                 UpdateUserUseCase updateUserUseCase,
                                 BanUnbanDeleteUserUseCases banUnbanDeleteUserUseCases,
                                 CurrentUserPort currentUserPort) {
        this.createUserUseCase = createUserUseCase;
        this.getUserQuery = getUserQuery;
        this.getUserPageQuery = getUserPageQuery;
        this.updateUserUseCase = updateUserUseCase;
        this.banUnbanDeleteUserUseCases = banUnbanDeleteUserUseCases;
        this.currentUserPort = currentUserPort;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDto>>> all(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "20") int size,
                                                          @RequestParam(required = false) String q,
                                                          @RequestParam(required = false) Boolean banned,
                                                          HttpServletRequest request) {
        if (!currentUserPort.currentUserHasRole("ROLE_ADMIN")) {
            throw new ForbiddenException("Admin access required");
        }
        var result = getUserPageQuery.execute(q, banned, PageRequest.of(page, size));
        Map<String, Object> meta = Map.of("pagination", PaginationMeta.from(result));
        List<UserDto> content = result.getContent().stream().map(QueryDtoMapper::toUserDto).toList();
        var body = ApiResponse.success(200, "Users retrieved", request.getRequestURI(), content, meta);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getById(@PathVariable Long id, HttpServletRequest request) {
        boolean isAdmin = currentUserPort.currentUserHasRole("ROLE_ADMIN");
        var dto = QueryDtoMapper.toUserDto(getUserQuery.execute(id));

        if (!isAdmin) {
            var current = currentUserPort.getCurrentUser().orElseThrow(() -> new UnauthorizedException("Not authenticated"));
            if (!dto.getUsername().equals(current.getUsername())) {
                throw new ForbiddenException("Not allowed to access this user");
            }
        }
        var body = ApiResponse.success(200, "User retrieved", request.getRequestURI(), dto);
        return ResponseEntity.ok(body);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserDto>> create(@Valid @RequestBody CreateUserRequest req, HttpServletRequest request) {
        if (!currentUserPort.currentUserHasRole("ROLE_ADMIN")) {
            throw new ForbiddenException("Admin access required");
        }
        var created = createUserUseCase.execute(new CreateUserCommand(req.getUsername(), req.getEmail(), req.getPassword()));
        var body = ApiResponse.success(201, "User created", request.getRequestURI(), created);
        return ResponseEntity.status(201).body(body);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> update(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest req, HttpServletRequest request) {
        boolean isAdmin = currentUserPort.currentUserHasRole("ROLE_ADMIN");

        if (!isAdmin) {
            var current = currentUserPort.getCurrentUser().orElseThrow(() -> new UnauthorizedException("Not authenticated"));
            if (!current.getId().equals(id)) {
                throw new ForbiddenException("Not allowed to update this user");
            }
        }

        var updated = updateUserUseCase.execute(id, req);
        var body = ApiResponse.success(200, "User updated", request.getRequestURI(), updated);
        return ResponseEntity.ok(body);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, HttpServletRequest request) {
        boolean isAdmin = currentUserPort.currentUserHasRole("ROLE_ADMIN");
        if (!isAdmin) {
            throw new ForbiddenException("Admin access required");
        }
        banUnbanDeleteUserUseCases.delete(id);
        var body = ApiResponse.<Void>success(200, "User deleted", request.getRequestURI(), null);
        return ResponseEntity.ok(body);
    }
}
