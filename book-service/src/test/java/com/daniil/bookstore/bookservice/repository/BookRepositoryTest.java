package com.daniil.bookstore.bookservice.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.daniil.bookstore.bookservice.model.Book;
import com.daniil.bookstore.bookservice.repository.book.BookRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("findAll returns all non-deleted books")
    @Sql(scripts = "classpath:com/daniil/bookstore/bookservice/database/books/add-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:com/daniil/bookstore/bookservice/database/books/remove-books.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAll_BooksExist_ReturnsAllBooks() {
        List<Book> result = bookRepository.findAll();

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(b -> b.getTitle().equals("Dune")));
        assertTrue(result.stream().anyMatch(b -> b.getTitle().equals("The Hobbit")));
    }

    @Test
    @DisplayName("findAllByCategoryId returns books for given category")
    @Sql(scripts = {
            "classpath:com/daniil/bookstore/bookservice/database/categories/add-categories.sql",
            "classpath:com/daniil/bookstore/bookservice/database/books/add-books.sql",
            "classpath:com/daniil/bookstore/bookservice/database/books/add-book-category.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:com/daniil/bookstore/bookservice/database/books/remove-book-category.sql",
            "classpath:com/daniil/bookstore/bookservice/database/books/remove-books.sql",
            "classpath:com/daniil/bookstore/bookservice/database/categories/remove-categories.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByCategoryId_ValidCategory_ReturnsBooks() {
        List<Book> result = bookRepository.findAllByCategoryId(1L);

        assertEquals(1, result.size());
        assertEquals("Dune", result.get(0).getTitle());
    }

    @Test
    @DisplayName("findAllByCategoryId returns empty list for unknown category")
    void findAllByCategoryId_UnknownCategory_ReturnsEmpty() {
        List<Book> result = bookRepository.findAllByCategoryId(999L);
        assertTrue(result.isEmpty());
    }
}
