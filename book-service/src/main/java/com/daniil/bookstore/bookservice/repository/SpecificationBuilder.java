package com.daniil.bookstore.bookservice.repository;

import com.daniil.bookstore.bookservice.dto.book.BookSearchParameters;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T> {
    Specification<T> build(BookSearchParameters searchParameters);
}
