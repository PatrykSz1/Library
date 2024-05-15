package com.testlibrary.testlibrary.service;

import com.testlibrary.testlibrary.mapper.CategoryMapper;
import com.testlibrary.testlibrary.model.category.Category;
import com.testlibrary.testlibrary.model.category.CategoryCommand;
import com.testlibrary.testlibrary.model.category.CategoryDto;
import com.testlibrary.testlibrary.repository.BookRepository;
import com.testlibrary.testlibrary.repository.CategoryRepository;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    public void testFindById() {
        Category category = Category.builder()
                .id(1)
                .name("Test Category")
                .build();
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));

        Category result = categoryService.findById(1);

        assertEquals(1, result.getId());
        assertEquals("Test Category", result.getName());
    }

    @Test
    public void testSaveCategory() {
        CategoryCommand categoryCommand = CategoryCommand.builder()
                .name("Test Category")
                .build();

        Category category = Category.builder()
                .id(1)
                .name("Test Category")
                .build();

        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        Category savedCategory = categoryService.save(categoryCommand);

        verify(categoryRepository, times(1)).save(any(Category.class));


        assertNotNull(savedCategory);
        assertEquals(1, savedCategory.getId());
        assertEquals("Test Category", savedCategory.getName());
    }

    @Test
    public void testSaveCategoryWithDuplicateName() {
        CategoryCommand categoryCommand = CategoryCommand.builder()
                .name("Test Category")
                .build();

        when(categoryRepository.save(any(Category.class))).thenThrow(DataIntegrityViolationException.class);

        assertThrows(EntityExistsException.class, () -> categoryService.save(categoryCommand));
    }

    @Test
    public void testFindCategoryByName() {
        Category category = Category.builder()
                .id(1)
                .name("Test Category")
                .build();
        when(categoryRepository.findCategoryByName("Test Category")).thenReturn(Optional.of(category));

        Category result = categoryService.findCategoryByName("Test Category");

        assertEquals(1, result.getId());
        assertEquals("Test Category", result.getName());
    }


    @Test
    public void testFindAll() {
        Category category1 = Category.builder()
                .id(1)
                .name("Test Category")
                .build();
        Category category2 = Category.builder()
                .id(2)
                .name("Test Category2")
                .build();
        List<Category> categories = new ArrayList<>();
        categories.add(category1);
        categories.add(category2);

        Pageable pageable = Pageable.unpaged();

        Page<Category> page = new PageImpl<>(categories);
        List<CategoryDto> categoryDtos = new ArrayList<>();
        categoryDtos.add(new CategoryDto(1, "Category 1", 3));
        categoryDtos.add(new CategoryDto(2, "Category 2", 2));

        when(categoryRepository.findAll(pageable)).thenReturn(page);
        when(bookRepository.countBooksByCategory(Mockito.any(Category.class))).thenReturn(3);
        when(categoryMapper.toDto(Mockito.any(Category.class))).thenAnswer(invocation -> {
            Category category = invocation.getArgument(0);
            for (CategoryDto categoryDto : categoryDtos) {
                if (categoryDto.getId() == category.getId()) {
                    return categoryDto;
                }
            }
            return null;
        });

        Page<CategoryDto> result = categoryService.findAll(pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals("Category 1", result.getContent().get(0).getName());
        assertEquals("Category 2", result.getContent().get(1).getName());
    }
}
