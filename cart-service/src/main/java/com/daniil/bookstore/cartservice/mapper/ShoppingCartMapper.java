package com.daniil.bookstore.cartservice.mapper;

import com.daniil.bookstore.cartservice.config.MapperConfig;
import com.daniil.bookstore.cartservice.dto.shoppingcart.ShoppingCartDto;
import com.daniil.bookstore.cartservice.model.ShoppingCart;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class, uses = {CartItemMapper.class})
public interface ShoppingCartMapper {
    ShoppingCartDto toDto(ShoppingCart shoppingCart);
}
