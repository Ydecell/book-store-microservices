package com.daniil.bookstore.bookservice.service;

import com.daniil.bookstore.bookservice.dto.book.BookDto;
import com.daniil.bookstore.bookservice.dto.book.BookDtoWithoutCategoryIds;
import com.daniil.bookstore.bookservice.dto.book.BookSearchParameters;
import com.daniil.bookstore.bookservice.dto.book.CreateBookRequestDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    List<BookDto> findAll(Pageable pageable);

    BookDto findById(Long id);

    void deleteById(Long id);

    BookDto update(Long id, BookDto bookDto);

    List<BookDto> search(BookSearchParameters params, Pageable pageable);

    List<BookDtoWithoutCategoryIds> findAllByCategoryId(Long id);
}
