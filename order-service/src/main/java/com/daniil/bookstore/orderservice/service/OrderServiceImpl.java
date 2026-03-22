package com.daniil.bookstore.orderservice.service;

import com.daniil.bookstore.commonsecurity.exception.EntityNotFoundException;
import com.daniil.bookstore.orderservice.client.CartClient;
import com.daniil.bookstore.orderservice.dto.order.CreateOrderRequestDto;
import com.daniil.bookstore.orderservice.dto.order.OrderDto;
import com.daniil.bookstore.orderservice.dto.orderitem.OrderItemDto;
import com.daniil.bookstore.orderservice.dto.shoppingcart.ShoppingCartDto;
import com.daniil.bookstore.orderservice.mapper.OrderItemMapper;
import com.daniil.bookstore.orderservice.mapper.OrderMapper;
import com.daniil.bookstore.orderservice.model.Order;
import com.daniil.bookstore.orderservice.model.OrderItem;
import com.daniil.bookstore.orderservice.repository.order.OrderRepository;
import com.daniil.bookstore.orderservice.repository.orderitem.OrderItemRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final CartClient cartClient;

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getOrders(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orderMapper.map(orders);
    }

    @Override
    @Transactional
    public OrderDto createOrder(Long userId, CreateOrderRequestDto dto) {
        ShoppingCartDto cart = cartClient.getShoppingCart(userId);

        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new IllegalStateException("Cannot create order: shopping cart is empty");
        }

        BigDecimal total = cart.getCartItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(Order.Status.PENDING);
        order.setTotal(total);
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(dto.getShippingAddress());
        Order savedOrder = orderRepository.save(order);

        List<OrderItem> items = cart.getCartItems().stream().map(item -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setBookId(item.getBookId());
            orderItem.setBookTitle(item.getBookTitle());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(item.getPrice());
            return orderItem;
        }).toList();

        orderItemRepository.saveAll(items);
        cartClient.clearShoppingCart(userId);

        savedOrder.setOrderItems(new HashSet<>(items));
        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional
    public OrderDto updateOrderStatus(Long orderId, Order.Status status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: "
                        + orderId));
        order.setStatus(status);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional(readOnly = true)
    public Set<OrderItemDto> getOrderItems(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: "
                        + orderId));
        if (!order.getUserId().equals(userId)) {
            throw new EntityNotFoundException("Order not found with id: " + orderId);
        }
        return orderItemMapper.map(order.getOrderItems());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderItemDto getOrderItem(Long orderId, Long itemId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: "
                        + orderId));
        if (!order.getUserId().equals(userId)) {
            throw new EntityNotFoundException("Order not found with id: " + orderId);
        }
        return order.getOrderItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .map(orderItemMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Item not found with id: " + itemId + " in order: " + orderId));
    }
}
