package com.daniil.bookstore.userservice.service;

import com.daniil.bookstore.userservice.dto.UserRegistrationRequestDto;
import com.daniil.bookstore.userservice.dto.UserResponseDto;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto request);
}
