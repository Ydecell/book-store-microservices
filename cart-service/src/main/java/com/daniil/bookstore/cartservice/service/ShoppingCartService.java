package com.daniil.bookstore.cartservice.service;

import com.daniil.bookstore.cartservice.dto.cartitem.CreateCartItemRequestDto;
import com.daniil.bookstore.cartservice.dto.cartitem.UpdateCartItemRequestDto;
import com.daniil.bookstore.cartservice.dto.shoppingcart.ShoppingCartDto;

public interface ShoppingCartService {
    ShoppingCartDto getShoppingCartForUser(Long userId);

    ShoppingCartDto addItemToCart(CreateCartItemRequestDto requestDto, Long userId);

    ShoppingCartDto updateCartItem(Long cartItemId, UpdateCartItemRequestDto requestDto,
                                   Long userId);

    void removeCartItem(Long cartItemId, Long userId);

    void createShoppingCart(Long userId);

    void clearShoppingCart(Long userId);
}
