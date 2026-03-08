package com.biursite.service.impl;

import com.biursite.dto.UserDTO;
import com.biursite.entity.User;
import com.biursite.repository.UserRepository;
import com.biursite.service.UserService;
import com.biursite.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDTO toDto(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Override
    public List<UserDTO> getAll() {
        return userRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public UserDTO getById(Long id) {
        User u = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return toDto(u);
    }

    @Override
    public UserDTO create(User user) {
        user.setCreatedAt(Instant.now());
        User saved = userRepository.save(user);
        return toDto(saved);
    }

    @Override
    public UserDTO update(Long id, User user) {
        User existing = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        existing.setEmail(user.getEmail());
        existing.setUsername(user.getUsername());
        existing.setRole(user.getRole());
        User saved = userRepository.save(existing);
        return toDto(saved);
    }

    @Override
    public void delete(Long id) {
        User existing = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        userRepository.delete(existing);
    }
}
