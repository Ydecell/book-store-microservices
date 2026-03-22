package com.daniil.bookstore.orderservice.service;

import com.daniil.bookstore.orderservice.dto.order.CreateOrderRequestDto;
import com.daniil.bookstore.orderservice.dto.order.OrderDto;
import com.daniil.bookstore.orderservice.dto.orderitem.OrderItemDto;
import com.daniil.bookstore.orderservice.model.Order;
import java.util.List;
import java.util.Set;

public interface OrderService {
    List<OrderDto> getOrders(Long userId);

    OrderDto createOrder(Long userId, CreateOrderRequestDto dto);

    OrderDto updateOrderStatus(Long orderId, Order.Status status);

    Set<OrderItemDto> getOrderItems(Long orderId, Long userId);

    OrderItemDto getOrderItem(Long orderId, Long itemId, Long userId);
}
