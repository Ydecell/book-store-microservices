package com.daniil.bookstore.cartservice.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.daniil.bookstore.cartservice.model.CartItem;
import com.daniil.bookstore.cartservice.repository.cartitem.CartItemRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {
        "classpath:com/daniil/bookstore/cartservice/database/carts/add-shopping-carts.sql",
        "classpath:com/daniil/bookstore/cartservice/database/items/add-cart-items.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {
        "classpath:com/daniil/bookstore/cartservice/database/items/remove-cart-items.sql",
        "classpath:com/daniil/bookstore/cartservice/database/carts/remove-shopping-carts.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class CartItemRepositoryTest {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Test
    @DisplayName("findByIdAndShoppingCartId returns item when exists")
    void findByIdAndShoppingCartId_ValidIds_ReturnsCartItem() {
        Optional<CartItem> result = cartItemRepository.findByIdAndShoppingCartId(1L, 1L);

        assertTrue(result.isPresent());
        assertEquals(10L, result.get().getBookId());
    }

    @Test
    @DisplayName("findByIdAndShoppingCartId returns empty for wrong cart")
    void findByIdAndShoppingCartId_WrongCartId_ReturnsEmpty() {
        Optional<CartItem> result = cartItemRepository.findByIdAndShoppingCartId(1L, 999L);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("findByShoppingCartIdAndBookId returns item when exists")
    void findByShoppingCartIdAndBookId_ValidIds_ReturnsCartItem() {
        Optional<CartItem> result = cartItemRepository.findByShoppingCartIdAndBookId(1L, 10L);

        assertTrue(result.isPresent());
        assertEquals(2, result.get().getQuantity());
    }

    @Test
    @DisplayName("findByShoppingCartIdAndBookId returns empty when not exists")
    void findByShoppingCartIdAndBookId_NonExisting_ReturnsEmpty() {
        Optional<CartItem> result = cartItemRepository.findByShoppingCartIdAndBookId(1L, 999L);

        assertFalse(result.isPresent());
    }
}
