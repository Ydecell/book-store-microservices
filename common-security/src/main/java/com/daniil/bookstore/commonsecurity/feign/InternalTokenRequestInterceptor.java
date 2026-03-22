package com.daniil.bookstore.commonsecurity.feign;

import com.daniil.bookstore.commonsecurity.security.SecurityConstants;
import feign.RequestInterceptor;
import feign.RequestTemplate;

public class InternalTokenRequestInterceptor implements RequestInterceptor {
    private final String internalToken;

    public InternalTokenRequestInterceptor(String internalToken) {
        this.internalToken = internalToken;
    }

    @Override
    public void apply(RequestTemplate template) {
        template.header(SecurityConstants.INTERNAL_TOKEN_HEADER, internalToken);
    }
}
