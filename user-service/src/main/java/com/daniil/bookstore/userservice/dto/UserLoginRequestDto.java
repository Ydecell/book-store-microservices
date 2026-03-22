package com.daniil.bookstore.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record UserLoginRequestDto(
        @NotBlank
        @Email(message = "Must be a valid email address")
        String email,
        @NotBlank
        @Length(min = 8, max = 100)
        String password
) {
}
