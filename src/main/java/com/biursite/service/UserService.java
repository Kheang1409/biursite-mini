package com.biursite.service;

import com.biursite.dto.UserDTO;
import com.biursite.entity.User;
import java.util.List;

public interface UserService {
    UserDTO toDto(User user);
    List<UserDTO> getAll();
    UserDTO getById(Long id);
    UserDTO create(User user);
    UserDTO update(Long id, User user);
    void delete(Long id);
}
