package com.biursite.controller;

import com.biursite.dto.UserDTO;
import com.biursite.entity.User;
import com.biursite.service.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    @RolesAllowed({"ROLE_ADMIN"})
    public ResponseEntity<List<UserDTO>> all() {
        return ResponseEntity.ok(userService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getById(@PathVariable Long id, Authentication auth) {
        // if not admin, ensure own profile
        if (!auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            String current = auth.getName();
            UserDTO dto = userService.getById(id);
            if (!dto.getUsername().equals(current)) {
                return ResponseEntity.status(403).build();
            }
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.ok(userService.getById(id));
    }

    @PostMapping
    @RolesAllowed({"ROLE_ADMIN"})
    public ResponseEntity<UserDTO> create(@Valid @RequestBody User user) {
        return ResponseEntity.status(201).body(userService.create(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable Long id, @Valid @RequestBody User user, Authentication auth) {
        if (!auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            if (!auth.getName().equals(user.getUsername())) {
                return ResponseEntity.status(403).build();
            }
        }
        return ResponseEntity.ok(userService.update(id, user));
    }

    @DeleteMapping("/{id}")
    @RolesAllowed({"ROLE_ADMIN"})
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
