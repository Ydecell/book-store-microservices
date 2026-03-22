package com.daniil.bookstore.commonsecurity.security;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.function.Supplier;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

public class InternalTokenAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
    private final String internalToken;

    public InternalTokenAuthorizationManager(String internalToken) {
        this.internalToken = internalToken;
    }

    @Override
    public AuthorizationDecision authorize(
            Supplier<? extends Authentication> authentication,
            RequestAuthorizationContext context
    ) {
        HttpServletRequest request = context.getRequest();
        String provided = request.getHeader(SecurityConstants.INTERNAL_TOKEN_HEADER);
        return new AuthorizationDecision(Objects.equals(internalToken, provided));
    }
}
