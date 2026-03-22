package com.daniil.bookstore.userservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.daniil.bookstore.commonsecurity.exception.RegistrationException;
import com.daniil.bookstore.userservice.client.CartClient;
import com.daniil.bookstore.userservice.dto.UserRegistrationRequestDto;
import com.daniil.bookstore.userservice.dto.UserResponseDto;
import com.daniil.bookstore.userservice.mapper.UserMapper;
import com.daniil.bookstore.userservice.model.Role;
import com.daniil.bookstore.userservice.model.User;
import com.daniil.bookstore.userservice.repository.role.RoleRepository;
import com.daniil.bookstore.userservice.repository.user.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private CartClient cartClient;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRegistrationRequestDto requestDto;
    private User user;
    private Role defaultRole;

    @BeforeEach
    void setUp() {
        requestDto = new UserRegistrationRequestDto();
        requestDto.setEmail("john@example.com");
        requestDto.setPassword("password1");
        requestDto.setRepeatPassword("password1");
        requestDto.setFirstName("John");
        requestDto.setLastName("Doe");

        user = new User();
        user.setEmail("john@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");

        defaultRole = new Role();
    }

    @Test
    @DisplayName("Register user with valid data returns UserResponseDto")
    void register_ValidRequest_ReturnsUserResponseDto() {
        UserResponseDto expectedDto = new UserResponseDto();
        expectedDto.setId(1L);
        expectedDto.setEmail("john@example.com");

        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(false);
        when(userMapper.toEntity(requestDto)).thenReturn(user);
        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("encodedPassword");
        when(roleRepository.findByRole(Role.RoleName.ROLE_USER)).thenReturn(Optional.of(defaultRole));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(expectedDto);

        UserResponseDto result = userService.register(requestDto);

        assertNotNull(result);
        assertEquals("john@example.com", result.getEmail());
        verify(userRepository, times(1)).save(user);
        verify(passwordEncoder, times(1)).encode("password1");
    }

    @Test
    @DisplayName("Register with existing email throws RegistrationException")
    void register_EmailAlreadyTaken_ThrowsRegistrationException() {
        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(true);

        RegistrationException exception = assertThrows(RegistrationException.class,
                () -> userService.register(requestDto));

        assertEquals("Email is already taken: john@example.com", exception.getMessage());
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    @DisplayName("Register when ROLE_USER is missing throws IllegalStateException")
    void register_DefaultRoleNotFound_ThrowsIllegalStateException() {
        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(false);
        when(userMapper.toEntity(requestDto)).thenReturn(user);
        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("encodedPassword");
        when(roleRepository.findByRole(Role.RoleName.ROLE_USER)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class,
                () -> userService.register(requestDto));

        verify(userRepository, times(0)).save(any(User.class));
    }
}
