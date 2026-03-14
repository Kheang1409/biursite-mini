package com.biursite.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRequest {
    @NotBlank
    private String username;

    @Email
    @NotBlank
    private String email;

    // password optional for updates
    private String password;
}
