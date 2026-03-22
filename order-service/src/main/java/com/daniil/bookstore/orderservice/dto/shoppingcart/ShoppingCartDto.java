package com.daniil.bookstore.orderservice.dto.shoppingcart;

import com.daniil.bookstore.orderservice.dto.cartitem.CartItemDto;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShoppingCartDto {
    private Long id;
    private Long userId;
    private Set<CartItemDto> cartItems;
}
