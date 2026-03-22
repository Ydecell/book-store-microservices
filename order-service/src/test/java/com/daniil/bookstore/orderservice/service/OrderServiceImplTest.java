package com.daniil.bookstore.orderservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.daniil.bookstore.commonsecurity.exception.EntityNotFoundException;
import com.daniil.bookstore.orderservice.client.CartClient;
import com.daniil.bookstore.orderservice.dto.cartitem.CartItemDto;
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
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderItemMapper orderItemMapper;

    @Mock
    private CartClient cartClient;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order order;
    private OrderDto orderDto;
    private static final Long USER_ID = 100L;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setId(1L);
        order.setUserId(USER_ID);
        order.setStatus(Order.Status.PENDING);
        order.setTotal(new BigDecimal("19.98"));
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress("123 Main St");
        order.setOrderItems(new HashSet<>());

        orderDto = new OrderDto();
        orderDto.setId(1L);
        orderDto.setUserId(USER_ID);
        orderDto.setStatus(Order.Status.PENDING);
        orderDto.setTotal(new BigDecimal("19.98"));
    }

    @Test
    @DisplayName("getOrders returns list of orders for user")
    void getOrders_ValidUserId_ReturnsOrderDtoList() {
        when(orderRepository.findByUserId(USER_ID)).thenReturn(List.of(order));
        when(orderMapper.map(List.of(order))).thenReturn(List.of(orderDto));

        List<OrderDto> result = orderService.getOrders(USER_ID);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(USER_ID, result.get(0).getUserId());
    }

    @Test
    @DisplayName("getOrders returns empty list when user has no orders")
    void getOrders_NoOrders_ReturnsEmptyList() {
        when(orderRepository.findByUserId(USER_ID)).thenReturn(List.of());
        when(orderMapper.map(List.of())).thenReturn(List.of());

        List<OrderDto> result = orderService.getOrders(USER_ID);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("createOrder with valid cart creates order and clears cart")
    void createOrder_ValidCart_CreatesOrderAndClearsCart() {
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setBookId(10L);
        cartItemDto.setBookTitle("Dune");
        cartItemDto.setQuantity(2);
        cartItemDto.setPrice(new BigDecimal("9.99"));

        ShoppingCartDto cartDto = new ShoppingCartDto();
        cartDto.setUserId(USER_ID);
        cartDto.setCartItems(Set.of(cartItemDto));

        CreateOrderRequestDto requestDto = new CreateOrderRequestDto();
        requestDto.setShippingAddress("123 Main St");

        when(cartClient.getShoppingCart(USER_ID)).thenReturn(cartDto);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderItemRepository.saveAll(anyList())).thenAnswer(i -> i.getArgument(0));
        when(orderMapper.toDto(any(Order.class))).thenReturn(orderDto);

        OrderDto result = orderService.createOrder(USER_ID, requestDto);

        assertNotNull(result);
        verify(cartClient, times(1)).clearShoppingCart(USER_ID);
        verify(orderItemRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("createOrder with empty cart throws IllegalStateException")
    void createOrder_EmptyCart_ThrowsIllegalStateException() {
        ShoppingCartDto cartDto = new ShoppingCartDto();
        cartDto.setUserId(USER_ID);
        cartDto.setCartItems(Set.of());

        CreateOrderRequestDto requestDto = new CreateOrderRequestDto();
        requestDto.setShippingAddress("123 Main St");

        when(cartClient.getShoppingCart(USER_ID)).thenReturn(cartDto);

        assertThrows(IllegalStateException.class,
                () -> orderService.createOrder(USER_ID, requestDto));

        verify(orderRepository, times(0)).save(any(Order.class));
    }

    @Test
    @DisplayName("createOrder with null cart items throws IllegalStateException")
    void createOrder_NullCartItems_ThrowsIllegalStateException() {
        ShoppingCartDto cartDto = new ShoppingCartDto();
        cartDto.setUserId(USER_ID);
        cartDto.setCartItems(null);

        CreateOrderRequestDto requestDto = new CreateOrderRequestDto();
        requestDto.setShippingAddress("123 Main St");

        when(cartClient.getShoppingCart(USER_ID)).thenReturn(cartDto);

        assertThrows(IllegalStateException.class,
                () -> orderService.createOrder(USER_ID, requestDto));
    }

    @Test
    @DisplayName("updateOrderStatus valid id updates status")
    void updateOrderStatus_ValidId_UpdatesStatus() {
        OrderDto updatedDto = new OrderDto();
        updatedDto.setId(1L);
        updatedDto.setStatus(Order.Status.SHIPPED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(updatedDto);

        OrderDto result = orderService.updateOrderStatus(1L, Order.Status.SHIPPED);

        assertNotNull(result);
        assertEquals(Order.Status.SHIPPED, result.getStatus());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    @DisplayName("updateOrderStatus invalid id throws EntityNotFoundException")
    void updateOrderStatus_InvalidId_ThrowsEntityNotFoundException() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> orderService.updateOrderStatus(99L, Order.Status.SHIPPED));
    }

    @Test
    @DisplayName("getOrderItems valid order and user returns items")
    void getOrderItems_ValidOrderAndUser_ReturnsOrderItems() {
        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        order.getOrderItems().add(orderItem);

        OrderItemDto itemDto = new OrderItemDto();
        itemDto.setId(1L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderItemMapper.map(order.getOrderItems())).thenReturn(Set.of(itemDto));

        Set<OrderItemDto> result = orderService.getOrderItems(1L, USER_ID);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getOrderItems order belongs to different user throws EntityNotFoundException")
    void getOrderItems_DifferentUser_ThrowsEntityNotFoundException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(EntityNotFoundException.class,
                () -> orderService.getOrderItems(1L, 999L));
    }

    @Test
    @DisplayName("getOrderItem valid ids returns OrderItemDto")
    void getOrderItem_ValidIds_ReturnsOrderItemDto() {
        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        order.getOrderItems().add(orderItem);

        OrderItemDto itemDto = new OrderItemDto();
        itemDto.setId(1L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderItemMapper.toDto(orderItem)).thenReturn(itemDto);

        OrderItemDto result = orderService.getOrderItem(1L, 1L, USER_ID);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("getOrderItem item not in order throws EntityNotFoundException")
    void getOrderItem_ItemNotInOrder_ThrowsEntityNotFoundException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(EntityNotFoundException.class,
                () -> orderService.getOrderItem(1L, 999L, USER_ID));
    }

    @Test
    @DisplayName("getOrderItem order belongs to different user throws EntityNotFoundException")
    void getOrderItem_DifferentUser_ThrowsEntityNotFoundException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(EntityNotFoundException.class,
                () -> orderService.getOrderItem(1L, 1L, 999L));
    }
}
