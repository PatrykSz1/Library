package com.testlibrary.testlibrary.mapper;

import com.testlibrary.testlibrary.model.category.Category;
import com.testlibrary.testlibrary.model.category.CategoryDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.assertEquals;

@SpringBootTest(classes = {CategoryMapperImpl.class})
public class CategoryMapperTest {

    @Autowired
    private CategoryMapper categoryMapper;

    @Test
    public void testToDto() {
        Category category = Category.builder()
                .id(1)
                .name("Test Category")
                .build();

        CategoryDto categoryDto = categoryMapper.toDto(category);

        assertEquals(1, categoryDto.getId());
        assertEquals("Test Category", categoryDto.getName());
    }

    @Test
    public void testToEntity() {
        CategoryDto categoryDto = CategoryDto.builder()
                .id(1)
                .name("Test Category")
                .build();

        Category category = categoryMapper.toEntity(categoryDto);

        assertEquals(1, category.getId());
        assertEquals("Test Category", category.getName());
    }
}

