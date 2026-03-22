package com.daniil.bookstore.cartservice.client;

import com.daniil.bookstore.cartservice.client.fallback.BookClientFallback;
import com.daniil.bookstore.cartservice.dto.book.BookDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "book-service", fallbackFactory = BookClientFallback.class)
public interface BookClient {
    @GetMapping("/api/books/internal/{id}")
    BookDto getBookById(@PathVariable Long id);
}
