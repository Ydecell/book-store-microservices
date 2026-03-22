package com.daniil.bookstore.bookservice.service;

import com.daniil.bookstore.bookservice.dto.category.CategoryDto;
import java.util.List;

public interface CategoryService {
    List<CategoryDto> findAll();

    CategoryDto getById(Long id);

    CategoryDto save(CategoryDto categoryDto);

    CategoryDto update(Long id, CategoryDto categoryDto);

    void deleteById(Long id);
}
