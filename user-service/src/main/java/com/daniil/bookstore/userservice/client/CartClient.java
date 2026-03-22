package com.daniil.bookstore.userservice.client;

import com.daniil.bookstore.userservice.client.fallback.CartClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "cart-service", fallbackFactory = CartClientFallback.class)
public interface CartClient {
    @PostMapping("/api/cart/internal/{userId}")
    void createShoppingCart(@PathVariable Long userId);
}
