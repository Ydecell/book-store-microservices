package com.daniil.bookstore.bookservice.service;

import com.daniil.bookstore.bookservice.dto.book.BookDto;
import com.daniil.bookstore.bookservice.dto.book.BookDtoWithoutCategoryIds;
import com.daniil.bookstore.bookservice.dto.book.BookSearchParameters;
import com.daniil.bookstore.bookservice.dto.book.CreateBookRequestDto;
import com.daniil.bookstore.bookservice.mapper.BookMapper;
import com.daniil.bookstore.bookservice.model.Book;
import com.daniil.bookstore.bookservice.model.Category;
import com.daniil.bookstore.bookservice.repository.book.BookRepository;
import com.daniil.bookstore.bookservice.repository.book.BookSpecificationBuilder;
import com.daniil.bookstore.bookservice.repository.category.CategoryRepository;
import com.daniil.bookstore.commonsecurity.exception.EntityNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder bookSpecificationBuilder;
    private final CategoryRepository categoryRepository;

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toEntity(requestDto);
        book.setCategories(getCategoriesFromIds(requestDto.getCategoryIds()));
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public List<BookDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public BookDto findById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find book by id " + id)
        );
        return bookMapper.toDto(book);
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    @Transactional
    public BookDto update(Long id, BookDto bookDto) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find book by id " + id));

        bookMapper.updateModelFromDto(bookDto, existingBook);
        existingBook.setCategories(getCategoriesFromIds(bookDto.getCategoryIds()));
        Book updatedBook = bookRepository.save(existingBook);
        return bookMapper.toDto(updatedBook);
    }

    @Override
    public List<BookDto> search(BookSearchParameters params, Pageable pageable) {
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(params);
        return bookRepository.findAll(bookSpecification, pageable)
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public List<BookDtoWithoutCategoryIds> findAllByCategoryId(Long id) {
        List<Book> books = bookRepository.findAllByCategoryId(id);
        return bookMapper.toDtoWithoutCategoriesList(books);
    }

    private Set<Category> getCategoriesFromIds(Set<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return new HashSet<>();
        }
        Set<Category> found = new HashSet<>(categoryRepository.findAllById(categoryIds));
        if (found.size() != categoryIds.size()) {
            Set<Long> foundIds = found.stream()
                    .map(Category::getId)
                    .collect(Collectors.toSet());
            Set<Long> missing = new HashSet<>(categoryIds);
            missing.removeAll(foundIds);
            throw new EntityNotFoundException("Categories not found with ids: " + missing);
        }
        return found;
    }
}
