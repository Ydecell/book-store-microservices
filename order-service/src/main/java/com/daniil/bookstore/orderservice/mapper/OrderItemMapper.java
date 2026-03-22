package com.daniil.bookstore.orderservice.mapper;

import com.daniil.bookstore.orderservice.config.MapperConfig;
import com.daniil.bookstore.orderservice.dto.orderitem.OrderItemDto;
import com.daniil.bookstore.orderservice.model.OrderItem;
import java.util.Set;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface OrderItemMapper {
    OrderItemDto toDto(OrderItem orderItem);

    Set<OrderItemDto> map(Set<OrderItem> orderItems);
}
