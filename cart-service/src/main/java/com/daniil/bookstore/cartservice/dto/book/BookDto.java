package com.daniil.bookstore.cartservice.dto.book;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookDto {
    private Long id;
    private String title;
    private BigDecimal price;
}
