package com.daniil.bookstore.bookservice.service;

import com.daniil.bookstore.bookservice.dto.category.CategoryDto;
import com.daniil.bookstore.bookservice.mapper.CategoryMapper;
import com.daniil.bookstore.bookservice.model.Category;
import com.daniil.bookstore.bookservice.repository.category.CategoryRepository;
import com.daniil.bookstore.commonsecurity.exception.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> findAll() {
        List<Category> categories = categoryRepository.findAll();
        return categoryMapper.toDtoList(categories);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find category by id " + id));
        return categoryMapper.toDto(category);
    }

    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        Category category = categoryMapper.toEntity(categoryDto);
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryDto update(Long id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find category by id " + id));
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }
}
