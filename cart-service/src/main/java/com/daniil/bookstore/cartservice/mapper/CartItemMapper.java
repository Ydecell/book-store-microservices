package com.daniil.bookstore.cartservice.mapper;

import com.daniil.bookstore.cartservice.config.MapperConfig;
import com.daniil.bookstore.cartservice.dto.cartitem.CartItemDto;
import com.daniil.bookstore.cartservice.model.CartItem;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface CartItemMapper {
    CartItemDto toDto(CartItem cartItem);
}
