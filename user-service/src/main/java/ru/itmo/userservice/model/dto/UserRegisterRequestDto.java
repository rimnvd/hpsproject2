package ru.itmo.userservice.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRegisterRequestDto {
    @NotBlank
    private String username;

    @NotBlank
    @Pattern(regexp = "([a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.[a-zA-Z0-9_-]+)")
    private String email;

    @NotBlank
    private String password;
}
