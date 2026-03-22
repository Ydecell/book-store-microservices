package com.daniil.bookstore.orderservice.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.daniil.bookstore.orderservice.client.CartClient;
import com.daniil.bookstore.orderservice.dto.cartitem.CartItemDto;
import com.daniil.bookstore.orderservice.dto.order.CreateOrderRequestDto;
import com.daniil.bookstore.orderservice.dto.order.UpdateOrderRequestDto;
import com.daniil.bookstore.orderservice.dto.shoppingcart.ShoppingCartDto;
import com.daniil.bookstore.orderservice.model.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderControllerTest {

    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CartClient cartClient;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext context) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    void setup(@Autowired DataSource dataSource) throws SQLException {
        executeSqlScript(dataSource,
                "com/daniil/bookstore/orderservice/database/orders/add-orders.sql");
        executeSqlScript(dataSource,
                "com/daniil/bookstore/orderservice/database/items/add-order-items.sql");
    }

    @AfterEach
    void teardown(@Autowired DataSource dataSource) throws SQLException {
        executeSqlScript(dataSource,
                "com/daniil/bookstore/orderservice/database/items/remove-order-items.sql");
        executeSqlScript(dataSource,
                "com/daniil/bookstore/orderservice/database/orders/remove-orders.sql");
    }

    private void executeSqlScript(DataSource dataSource, String path) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(path));
        }
    }

    private RequestPostProcessor userWithId(Long userId) {
        return authentication(new UsernamePasswordAuthenticationToken(
                userId, null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))));
    }

    private RequestPostProcessor adminWithId(Long userId) {
        return authentication(new UsernamePasswordAuthenticationToken(
                userId, null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));
    }

    @Test
    @DisplayName("GET /orders returns 200 and list of orders for current user")
    void getOrders_ValidUser_ReturnsOk() throws Exception {
        mockMvc.perform(get("/orders")
                        .with(userWithId(100L))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].userId").value(100));
    }

    @Test
    @DisplayName("GET /orders returns empty list when user has no orders")
    void getOrders_NoOrders_ReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/orders")
                        .with(userWithId(999L))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("POST /orders creates order and returns 201")
    void createOrder_ValidCart_ReturnsCreated() throws Exception {
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setBookId(10L);
        cartItemDto.setBookTitle("Dune");
        cartItemDto.setQuantity(1);
        cartItemDto.setPrice(new BigDecimal("9.99"));

        ShoppingCartDto cartDto = new ShoppingCartDto();
        cartDto.setUserId(100L);
        cartDto.setCartItems(Set.of(cartItemDto));

        when(cartClient.getShoppingCart(100L)).thenReturn(cartDto);
        doNothing().when(cartClient).clearShoppingCart(100L);

        CreateOrderRequestDto request = new CreateOrderRequestDto();
        request.setShippingAddress("123 Main St");

        mockMvc.perform(post("/orders")
                        .with(userWithId(100L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(100))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("POST /orders with empty cart returns 500")
    void createOrder_EmptyCart_ReturnsServerError() throws Exception {
        ShoppingCartDto cartDto = new ShoppingCartDto();
        cartDto.setUserId(100L);
        cartDto.setCartItems(Set.of());

        when(cartClient.getShoppingCart(100L)).thenReturn(cartDto);

        CreateOrderRequestDto request = new CreateOrderRequestDto();
        request.setShippingAddress("123 Main St");

        mockMvc.perform(post("/orders")
                        .with(userWithId(100L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /orders with missing shippingAddress returns 400")
    void createOrder_MissingShippingAddress_ReturnsBadRequest() throws Exception {
        CreateOrderRequestDto request = new CreateOrderRequestDto();

        mockMvc.perform(post("/orders")
                        .with(userWithId(100L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /orders/{id} with ADMIN role updates status and returns 200")
    void updateOrder_AdminRole_ReturnsOk() throws Exception {
        UpdateOrderRequestDto request = new UpdateOrderRequestDto();
        request.setStatus(Order.Status.SHIPPED);

        mockMvc.perform(patch("/orders/1")
                        .with(adminWithId(100L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SHIPPED"));
    }

    @Test
    @DisplayName("PATCH /orders/{id} with USER role returns 403")
    void updateOrder_UserRole_ReturnsForbidden() throws Exception {
        UpdateOrderRequestDto request = new UpdateOrderRequestDto();
        request.setStatus(Order.Status.SHIPPED);

        mockMvc.perform(patch("/orders/1")
                        .with(userWithId(100L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PATCH /orders/{id} invalid id returns 404")
    void updateOrder_InvalidId_ReturnsNotFound() throws Exception {
        UpdateOrderRequestDto request = new UpdateOrderRequestDto();
        request.setStatus(Order.Status.SHIPPED);

        mockMvc.perform(patch("/orders/999")
                        .with(adminWithId(100L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /orders/{id}/items returns order items for owner")
    void getOrderItems_ValidOwner_ReturnsOk() throws Exception {
        mockMvc.perform(get("/orders/1/items")
                        .with(userWithId(100L))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("GET /orders/{id}/items returns 404 for different user")
    void getOrderItems_DifferentUser_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/orders/1/items")
                        .with(userWithId(999L))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /orders/{orderId}/items/{itemId} returns specific item")
    void getOrderItem_ValidIds_ReturnsOk() throws Exception {
        mockMvc.perform(get("/orders/1/items/1")
                        .with(userWithId(100L))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.bookId").value(10));
    }

    @Test
    @DisplayName("GET /orders/{orderId}/items/{itemId} invalid itemId returns 404")
    void getOrderItem_InvalidItemId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/orders/1/items/999")
                        .with(userWithId(100L))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
