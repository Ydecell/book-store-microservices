package com.daniil.bookstore.userservice.client.fallback;

import com.daniil.bookstore.userservice.client.CartClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CartClientFallback implements FallbackFactory<CartClient> {

    @Override
    public CartClient create(Throwable cause) {
        return userId -> {
            log.error("[FALLBACK] createShoppingCart failed for userId={}, cause: {}",
                    userId, cause.getMessage(), cause);
            throw new RuntimeException("Cart service unavailable. "
                    + "Registration rolled back.", cause);
        };
    }
}
