package com.daniil.bookstore.userservice.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.daniil.bookstore.commonsecurity.security.JwtUtil;
import com.daniil.bookstore.userservice.dto.UserLoginRequestDto;
import com.daniil.bookstore.userservice.dto.UserLoginResponseDto;
import com.daniil.bookstore.userservice.model.User;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("Authenticate valid credentials returns JWT token")
    void authenticate_ValidCredentials_ReturnsToken() {
        User user = new User();
        user.setId(1L);
        user.setEmail("john@example.com");
        user.setRoles(Set.of());

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtil.generateJwtToken(user, 1L, java.util.List.of())).thenReturn("mock.jwt.token");

        UserLoginResponseDto result = authenticationService.authenticate(
                new UserLoginRequestDto("john@example.com", "password1")
        );

        assertNotNull(result);
        assertEquals("mock.jwt.token", result.token());
    }
}
