package com.daniil.bookstore.orderservice.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.daniil.bookstore.orderservice.model.Order;
import com.daniil.bookstore.orderservice.repository.order.OrderRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("findByUserId returns orders for user")
    @Sql(scripts = "classpath:com/daniil/bookstore/orderservice/database/orders/add-orders.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:com/daniil/bookstore/orderservice/database/orders/remove-orders.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByUserId_ExistingUserId_ReturnsOrders() {
        List<Order> result = orderRepository.findByUserId(100L);

        assertEquals(1, result.size());
        assertEquals(Order.Status.PENDING, result.get(0).getStatus());
        assertEquals("123 Main St", result.get(0).getShippingAddress());
    }

    @Test
    @DisplayName("findByUserId returns empty list for unknown user")
    void findByUserId_UnknownUserId_ReturnsEmpty() {
        List<Order> result = orderRepository.findByUserId(999L);

        assertTrue(result.isEmpty());
    }
}
