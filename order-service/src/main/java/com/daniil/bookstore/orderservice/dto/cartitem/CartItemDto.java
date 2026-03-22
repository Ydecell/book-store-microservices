package com.daniil.bookstore.orderservice.dto.cartitem;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemDto {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private BigDecimal price;
    private int quantity;
}
