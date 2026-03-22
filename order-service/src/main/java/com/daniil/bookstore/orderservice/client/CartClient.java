package com.daniil.bookstore.orderservice.client;

import com.daniil.bookstore.orderservice.client.fallback.CartClientFallback;
import com.daniil.bookstore.orderservice.dto.shoppingcart.ShoppingCartDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "cart-service", fallbackFactory = CartClientFallback.class)
public interface CartClient {
    @GetMapping("/api/cart/internal/{userId}")
    ShoppingCartDto getShoppingCart(@PathVariable Long userId);

    @DeleteMapping("/api/cart/internal/{userId}/clear")
    void clearShoppingCart(@PathVariable Long userId);
}
