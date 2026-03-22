package com.daniil.bookstore.userservice.service;

import com.daniil.bookstore.commonsecurity.exception.RegistrationException;
import com.daniil.bookstore.userservice.client.CartClient;
import com.daniil.bookstore.userservice.dto.UserRegistrationRequestDto;
import com.daniil.bookstore.userservice.dto.UserResponseDto;
import com.daniil.bookstore.userservice.mapper.UserMapper;
import com.daniil.bookstore.userservice.model.Role;
import com.daniil.bookstore.userservice.model.User;
import com.daniil.bookstore.userservice.repository.role.RoleRepository;
import com.daniil.bookstore.userservice.repository.user.UserRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CartClient cartClient;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDto register(UserRegistrationRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RegistrationException("Email is already taken: " + request.getEmail());
        }
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        Role defaultRole = roleRepository.findByRole(Role.RoleName.ROLE_USER)
                .orElseThrow(() -> new IllegalStateException("Default role "
                        + "ROLE_USER not found in database"));
        user.setRoles(Set.of(defaultRole));
        User savedUser = userRepository.save(user);
        cartClient.createShoppingCart(savedUser.getId());
        return userMapper.toDto(savedUser);
    }
}
