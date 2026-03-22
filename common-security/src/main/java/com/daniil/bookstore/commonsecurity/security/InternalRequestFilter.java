package com.daniil.bookstore.commonsecurity.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

public class InternalRequestFilter extends OncePerRequestFilter {
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    private static final String INTERNAL_PATH_PATTERN = "/api/*/internal/**";

    private final String internalToken;

    public InternalRequestFilter(String internalToken) {
        this.internalToken = internalToken;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String path = request.getRequestURI();
        if (!PATH_MATCHER.match(INTERNAL_PATH_PATTERN, path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String provided = request.getHeader(SecurityConstants.INTERNAL_TOKEN_HEADER);
        if (!Objects.equals(internalToken, provided)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            return;
        }

        filterChain.doFilter(request, response);
    }
}
