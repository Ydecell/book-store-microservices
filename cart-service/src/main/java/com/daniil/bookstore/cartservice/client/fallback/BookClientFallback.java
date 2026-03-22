package com.daniil.bookstore.cartservice.client.fallback;

import com.daniil.bookstore.cartservice.client.BookClient;
import com.daniil.bookstore.commonsecurity.exception.EntityNotFoundException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BookClientFallback implements FallbackFactory<BookClient> {

    @Override
    public BookClient create(Throwable cause) {
        return bookId -> {
            if (cause instanceof FeignException.NotFound) {
                throw new EntityNotFoundException("Book not found with id: " + bookId);
            }
            log.error("[FALLBACK] getBookById failed for bookId={}, cause: {}",
                    bookId, cause.getMessage(), cause);
            throw new RuntimeException("book-service unavailable", cause);
        };
    }
}
