package com.daniil.bookstore.orderservice.client.fallback;

import com.daniil.bookstore.orderservice.client.CartClient;
import com.daniil.bookstore.orderservice.dto.shoppingcart.ShoppingCartDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CartClientFallback implements FallbackFactory<CartClient> {

    @Override
    public CartClient create(Throwable cause) {
        return new CartClient() {
            @Override
            public ShoppingCartDto getShoppingCart(Long userId) {
                log.error("[FALLBACK] getShoppingCart failed for userId={}, cause: {}",
                        userId, cause.getMessage(), cause);
                throw new RuntimeException("cart-service unavailable", cause);
            }

            @Override
            public void clearShoppingCart(Long userId) {
                log.error("[FALLBACK] clearShoppingCart failed for userId={}, cause: {}",
                        userId, cause.getMessage(), cause);
                    throw new RuntimeException("Failed to clear cart after order, "
                            + "cart-service unavailable ", cause);
            }
        };
    }
}
