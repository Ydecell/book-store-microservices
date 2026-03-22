package com.daniil.bookstore.orderservice.dto.order;

import com.daniil.bookstore.orderservice.dto.orderitem.OrderItemDto;
import com.daniil.bookstore.orderservice.model.Order;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDto {
    private Long id;
    private Long userId;
    private Set<OrderItemDto> orderItems;
    private LocalDateTime orderDate;
    private BigDecimal total;
    private Order.Status status;
}
