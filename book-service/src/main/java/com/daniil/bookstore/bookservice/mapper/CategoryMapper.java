package com.daniil.bookstore.bookservice.mapper;

import com.daniil.bookstore.bookservice.dto.category.CategoryDto;
import com.daniil.bookstore.bookservice.model.Category;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category toEntity(CategoryDto categoryDto);

    List<CategoryDto> toDtoList(List<Category> categories);
}
