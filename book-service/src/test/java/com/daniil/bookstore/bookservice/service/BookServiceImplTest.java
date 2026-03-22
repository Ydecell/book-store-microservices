package com.daniil.bookstore.bookservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book book;
    private BookDto bookDto;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1L);
        book.setTitle("Dune");
        book.setAuthor("Frank Herbert");
        book.setIsbn("1111111111111");
        book.setPrice(new BigDecimal("9.99"));

        bookDto = new BookDto()
                .setId(1L)
                .setTitle("Dune")
                .setAuthor("Frank Herbert")
                .setIsbn("1111111111111")
                .setPrice(new BigDecimal("9.99"));
    }

    @Test
    @DisplayName("save valid book returns BookDto")
    void save_ValidRequest_ReturnsBookDto() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("Dune")
                .setAuthor("Frank Herbert")
                .setIsbn("1111111111111")
                .setPrice(new BigDecimal("9.99"))
                .setCoverImage("dune.jpg");

        when(bookMapper.toEntity(requestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto result = bookService.save(requestDto);

        assertNotNull(result);
        assertEquals("Dune", result.getTitle());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    @DisplayName("findAll returns list of books")
    void findAll_BooksExist_ReturnsBookDtoList() {
        Pageable pageable = mock(Pageable.class);
        Page<Book> page = new PageImpl<>(List.of(book));

        when(bookRepository.findAll(pageable)).thenReturn(page);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        List<BookDto> result = bookService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Dune", result.getFirst().getTitle());
    }

    @Test
    @DisplayName("findById valid id returns BookDto")
    void findById_ValidId_ReturnsBookDto() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto result = bookService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("findById invalid id throws EntityNotFoundException")
    void findById_InvalidId_ThrowsEntityNotFoundException() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> bookService.findById(99L));

        assertEquals("Can't find book by id 99", ex.getMessage());
    }

    @Test
    @DisplayName("deleteById calls repository deleteById")
    void deleteById_ValidId_CallsRepository() {
        bookService.deleteById(1L);
        verify(bookRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("update valid id returns updated BookDto")
    void update_ValidId_ReturnsUpdatedBookDto() {
        BookDto updatedDto = new BookDto().setId(1L).setTitle("Dune Messiah");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(updatedDto);

        BookDto result = bookService.update(1L, updatedDto);

        assertNotNull(result);
        assertEquals("Dune Messiah", result.getTitle());
    }

    @Test
    @DisplayName("update invalid id throws EntityNotFoundException")
    void update_InvalidId_ThrowsEntityNotFoundException() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> bookService.update(99L, new BookDto()));
    }

    @Test
    @DisplayName("search returns matching books")
    void search_ValidParams_ReturnsBooks() {
        Pageable pageable = mock(Pageable.class);
        Page<Book> page = new PageImpl<>(List.of(book));
        Specification<Book> spec = (root, query, cb) -> null;
        BookSearchParameters params = new BookSearchParameters(
                new String[]{"Dune"}, new String[]{"Frank Herbert"});

        when(bookSpecificationBuilder.build(params)).thenReturn(spec);
        when(bookRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        List<BookDto> result = bookService.search(params, pageable);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("save book with categories resolves category entities")
    void save_WithCategoryIds_ResolvesCategoriesFromRepository() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("Dune")
                .setAuthor("Frank Herbert")
                .setIsbn("1111111111111")
                .setPrice(new BigDecimal("9.99"))
                .setCoverImage("dune.jpg")
                .setCategoryIds(Set.of(1L, 2L));

        Category cat1 = new Category();
        cat1.setId(1L);
        Category cat2 = new Category();
        cat2.setId(2L);

        when(bookMapper.toEntity(requestDto)).thenReturn(book);
        when(categoryRepository.findAllById(Set.of(1L, 2L))).thenReturn(List.of(cat1, cat2));
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto result = bookService.save(requestDto);

        assertNotNull(result);
        verify(categoryRepository, times(1)).findAllById(Set.of(1L, 2L));
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    @DisplayName("findAllByCategoryId returns books for category")
    void findAllByCategoryId_ValidId_ReturnsBooks() {
        BookDtoWithoutCategoryIds dto = new BookDtoWithoutCategoryIds(
                1L, "Dune", "Frank Herbert", "1111111111111",
                new BigDecimal("9.99"), "Science Fiction", "dune.jpg");

        when(bookRepository.findAllByCategoryId(1L)).thenReturn(List.of(book));
        when(bookMapper.toDtoWithoutCategoriesList(List.of(book))).thenReturn(List.of(dto));

        List<BookDtoWithoutCategoryIds> result = bookService.findAllByCategoryId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Dune", result.getFirst().title());
    }
}
