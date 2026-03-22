package com.daniil.bookstore.cartservice.repository.cartitem;

import com.daniil.bookstore.cartservice.model.CartItem;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByIdAndShoppingCartId(Long id, Long shoppingCartId);

    Optional<CartItem> findByShoppingCartIdAndBookId(Long shoppingCartId, Long bookId);
}
