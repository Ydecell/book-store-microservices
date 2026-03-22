package com.daniil.bookstore.orderservice.dto.order;

import com.daniil.bookstore.orderservice.model.Order;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOrderRequestDto {
    @NotNull(message = "Status cannot be null")
    private Order.Status status;
}
