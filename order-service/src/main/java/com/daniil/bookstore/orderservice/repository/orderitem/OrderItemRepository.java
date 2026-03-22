package com.daniil.bookstore.orderservice.repository.orderitem;

import com.daniil.bookstore.orderservice.model.OrderItem;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    Optional<Set<OrderItem>> findAllByOrderId(Long orderId);

    Optional<Set<OrderItem>> findAllByOrderIdAndOrder_UserId(Long orderId, Long userId);

    Optional<OrderItem> findByIdAndOrderIdAndOrder_UserId(Long id, Long orderId, Long userId);
}
