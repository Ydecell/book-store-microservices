package com.daniil.bookstore.orderservice.mapper;

import com.daniil.bookstore.orderservice.config.MapperConfig;
import com.daniil.bookstore.orderservice.dto.order.OrderDto;
import com.daniil.bookstore.orderservice.model.Order;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class, uses = {OrderItemMapper.class})
public interface OrderMapper {
    OrderDto toDto(Order order);

    List<OrderDto> map(List<Order> orders);
}
