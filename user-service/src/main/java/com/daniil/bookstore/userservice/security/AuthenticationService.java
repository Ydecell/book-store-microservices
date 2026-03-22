package com.daniil.bookstore.userservice.security;

import com.daniil.bookstore.commonsecurity.security.JwtUtil;
import com.daniil.bookstore.userservice.dto.UserLoginRequestDto;
import com.daniil.bookstore.userservice.dto.UserLoginResponseDto;
import com.daniil.bookstore.userservice.model.Role;
import com.daniil.bookstore.userservice.model.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthenticationService {
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public UserLoginResponseDto authenticate(UserLoginRequestDto request) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = (User) authentication.getPrincipal();

        List<String> roles = user.getRoles().stream()
                .map(Role::getAuthority)
                .toList();

        String token = jwtUtil.generateJwtToken(user, user.getId(), roles);
        return new UserLoginResponseDto(token);
    }
}
