package com.daniil.bookstore.bookservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.daniil.bookstore.bookservice.dto.category.CategoryDto;
import com.daniil.bookstore.bookservice.mapper.CategoryMapper;
import com.daniil.bookstore.bookservice.model.Category;
import com.daniil.bookstore.bookservice.repository.category.CategoryRepository;
import com.daniil.bookstore.commonsecurity.exception.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;
    private CategoryDto categoryDto;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Science Fiction");

        categoryDto = new CategoryDto().setId(1L).setName("Science Fiction");
    }

    @Test
    @DisplayName("findAll returns all categories")
    void findAll_CategoriesExist_ReturnsDtoList() {
        when(categoryRepository.findAll()).thenReturn(List.of(category));
        when(categoryMapper.toDtoList(List.of(category))).thenReturn(List.of(categoryDto));

        List<CategoryDto> result = categoryService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getById valid id returns CategoryDto")
    void getById_ValidId_ReturnsCategoryDto() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto result = categoryService.getById(1L);

        assertNotNull(result);
        assertEquals("Science Fiction", result.getName());
    }

    @Test
    @DisplayName("getById invalid id throws EntityNotFoundException")
    void getById_InvalidId_ThrowsEntityNotFoundException() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> categoryService.getById(99L));

        assertEquals("Can't find category by id 99", ex.getMessage());
    }

    @Test
    @DisplayName("save valid dto returns saved CategoryDto")
    void save_ValidDto_ReturnsCategoryDto() {
        when(categoryMapper.toEntity(categoryDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto result = categoryService.save(categoryDto);

        assertNotNull(result);
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    @DisplayName("update valid id returns updated CategoryDto")
    void update_ValidId_ReturnsUpdatedCategoryDto() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto result = categoryService.update(1L, categoryDto);

        assertNotNull(result);
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    @DisplayName("update invalid id throws EntityNotFoundException")
    void update_InvalidId_ThrowsEntityNotFoundException() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> categoryService.update(99L, categoryDto));
    }

    @Test
    @DisplayName("deleteById calls repository")
    void deleteById_ValidId_CallsRepository() {
        categoryService.deleteById(1L);
        verify(categoryRepository, times(1)).deleteById(1L);
    }
}
