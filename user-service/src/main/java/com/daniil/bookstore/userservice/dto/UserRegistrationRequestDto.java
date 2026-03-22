package com.daniil.bookstore.userservice.dto;

import com.daniil.bookstore.userservice.validation.FieldMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@FieldMatch(first = "password", second = "repeatPassword", message = "Passwords do not match")
public class UserRegistrationRequestDto {
    @NotBlank
    @Email(message = "Must be a valid email address")
    private String email;

    @NotBlank
    @Length(min = 8, max = 100)
    private String password;

    @NotBlank
    @Length(min = 8, max = 100)
    private String repeatPassword;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String shippingAddress;
}
