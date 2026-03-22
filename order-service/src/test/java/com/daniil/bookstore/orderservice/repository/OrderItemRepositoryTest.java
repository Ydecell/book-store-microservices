package com.daniil.bookstore.orderservice.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.daniil.bookstore.orderservice.model.OrderItem;
import com.daniil.bookstore.orderservice.repository.orderitem.OrderItemRepository;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {
        "classpath:com/daniil/bookstore/orderservice/database/orders/add-orders.sql",
        "classpath:com/daniil/bookstore/orderservice/database/items/add-order-items.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {
        "classpath:com/daniil/bookstore/orderservice/database/items/remove-order-items.sql",
        "classpath:com/daniil/bookstore/orderservice/database/orders/remove-orders.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class OrderItemRepositoryTest {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Test
    @DisplayName("findAllByOrderId returns items for order")
    void findAllByOrderId_ValidOrderId_ReturnsItems() {
        Optional<Set<OrderItem>> result = orderItemRepository.findAllByOrderId(1L);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());
    }

    @Test
    @DisplayName("findAllByOrderId returns empty for unknown order")
    void findAllByOrderId_UnknownOrderId_ReturnsEmpty() {
        Optional<Set<OrderItem>> result = orderItemRepository.findAllByOrderId(999L);

        assertTrue(result.isEmpty() || result.get().isEmpty());
    }

    @Test
    @DisplayName("findAllByOrderIdAndOrder_UserId returns items for correct user")
    void findAllByOrderIdAndUserId_ValidIds_ReturnsItems() {
        Optional<Set<OrderItem>> result =
                orderItemRepository.findAllByOrderIdAndOrder_UserId(1L, 100L);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());
    }

    @Test
    @DisplayName("findAllByOrderIdAndOrder_UserId returns empty for wrong user")
    void findAllByOrderIdAndUserId_WrongUser_ReturnsEmpty() {
        Optional<Set<OrderItem>> result =
                orderItemRepository.findAllByOrderIdAndOrder_UserId(1L, 999L);

        assertTrue(result.isEmpty() || result.get().isEmpty());
    }

    @Test
    @DisplayName("findByIdAndOrderIdAndOrder_UserId returns item for valid ids")
    void findByIdAndOrderIdAndUserId_ValidIds_ReturnsItem() {
        Optional<OrderItem> result =
                orderItemRepository.findByIdAndOrderIdAndOrder_UserId(1L, 1L, 100L);

        assertTrue(result.isPresent());
        assertEquals(10L, result.get().getBookId());
    }

    @Test
    @DisplayName("findByIdAndOrderIdAndOrder_UserId returns empty for wrong user")
    void findByIdAndOrderIdAndUserId_WrongUser_ReturnsEmpty() {
        Optional<OrderItem> result =
                orderItemRepository.findByIdAndOrderIdAndOrder_UserId(1L, 1L, 999L);

        assertFalse(result.isPresent());
    }
}
