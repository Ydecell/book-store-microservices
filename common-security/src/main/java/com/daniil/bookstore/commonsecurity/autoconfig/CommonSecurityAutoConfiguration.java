package com.daniil.bookstore.commonsecurity.autoconfig;

import com.daniil.bookstore.commonsecurity.security.InternalRequestFilter;
import com.daniil.bookstore.commonsecurity.security.InternalTokenAuthorizationManager;
import com.daniil.bookstore.commonsecurity.security.JwtAuthenticationFilter;
import com.daniil.bookstore.commonsecurity.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import(com.daniil.bookstore.commonsecurity.exception.GlobalExceptionHandler.class)
public class CommonSecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public JwtUtil jwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration:3600000}") long expiration
    ) {
        return new JwtUtil(secret, expiration);
    }

    @Bean
    @ConditionalOnMissingBean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtil jwtUtil) {
        return new JwtAuthenticationFilter(jwtUtil);
    }

    @Bean
    @ConditionalOnMissingBean
    public InternalRequestFilter internalRequestFilter(@Value("${internal.token}") String internalToken) {
        return new InternalRequestFilter(internalToken);
    }

    @Bean
    @ConditionalOnMissingBean
    public InternalTokenAuthorizationManager internalTokenAuthorizationManager(
            @Value("${internal.token}") String internalToken
    ) {
        return new InternalTokenAuthorizationManager(internalToken);
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(name = "feign.RequestInterceptor")
    public static class FeignConfiguration {

        @Bean
        @ConditionalOnMissingBean(name = "internalTokenRequestInterceptor")
        public feign.RequestInterceptor internalTokenRequestInterceptor(
                @Value("${internal.token}") String internalToken) {
            return new com.daniil.bookstore.commonsecurity.feign.InternalTokenRequestInterceptor(internalToken);
        }
    }
}
