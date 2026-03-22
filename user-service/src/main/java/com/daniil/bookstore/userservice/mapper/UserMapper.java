package com.daniil.bookstore.userservice.mapper;

import com.daniil.bookstore.userservice.config.MapperConfig;
import com.daniil.bookstore.userservice.dto.UserRegistrationRequestDto;
import com.daniil.bookstore.userservice.dto.UserResponseDto;
import com.daniil.bookstore.userservice.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    User toEntity(UserRegistrationRequestDto requestDto);

    UserResponseDto toDto(User user);
}
