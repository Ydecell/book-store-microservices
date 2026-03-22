package com.daniil.bookstore.cartservice.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.daniil.bookstore.cartservice.model.ShoppingCart;
import com.daniil.bookstore.cartservice.repository.shoppingcart.ShoppingCartRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ShoppingCartRepositoryTest {

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Test
    @DisplayName("findByUserId returns cart when exists")
    @Sql(scripts = "classpath:com/daniil/bookstore/cartservice/database/carts/add-shopping-carts.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:com/daniil/bookstore/cartservice/database/carts/remove-shopping-carts.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByUserId_ExistingUserId_ReturnsCart() {
        Optional<ShoppingCart> result = shoppingCartRepository.findByUserId(100L);

        assertTrue(result.isPresent());
        assertEquals(100L, result.get().getUserId());
    }

    @Test
    @DisplayName("findByUserId returns empty when not exists")
    void findByUserId_NonExistingUserId_ReturnsEmpty() {
        Optional<ShoppingCart> result = shoppingCartRepository.findByUserId(999L);

        assertFalse(result.isPresent());
    }
}
