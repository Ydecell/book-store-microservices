package com.daniil.bookstore.bookservice.mapper;

import com.daniil.bookstore.bookservice.config.MapperConfig;
import com.daniil.bookstore.bookservice.dto.book.BookDto;
import com.daniil.bookstore.bookservice.dto.book.BookDtoWithoutCategoryIds;
import com.daniil.bookstore.bookservice.dto.book.CreateBookRequestDto;
import com.daniil.bookstore.bookservice.model.Book;
import com.daniil.bookstore.bookservice.model.Category;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    Book toEntity(CreateBookRequestDto requestDto);

    @Mapping(target = "id", ignore = true)
    void updateModelFromDto(BookDto dto, @MappingTarget Book entity);

    List<BookDto> map(List<Book> books);

    BookDtoWithoutCategoryIds toDtoWithoutCategories(Book book);

    List<BookDtoWithoutCategoryIds> toDtoWithoutCategoriesList(List<Book> books);

    @AfterMapping
    default void setCategoryIds(@MappingTarget BookDto bookDto, Book book) {
        if (book.getCategories() != null) {
            bookDto.setCategoryIds(book.getCategories().stream()
                    .map(Category::getId)
                    .collect(Collectors.toSet()));
        }
    }
}
