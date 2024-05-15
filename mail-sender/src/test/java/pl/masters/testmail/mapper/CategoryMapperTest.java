package pl.masters.testmail.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.masters.testmail.model.category.Category;
import pl.masters.testmail.model.category.CategoryDto;

import static org.junit.Assert.assertEquals;

@SpringBootTest(classes = {CategoryMapperImpl.class})
public class CategoryMapperTest {

    @Autowired
    private  CategoryMapper categoryMapper;

    @Test
    void testToDto_shouldMapCategoryToCategoryDto() {
        Category category = Category.builder()
                .id(1)
                .name("Fantasy")
                .build();

        CategoryDto categoryDto = categoryMapper.toDto(category);

        assertEquals(category.getId(), categoryDto.getId());
        assertEquals(category.getName(), categoryDto.getName());
    }

    @Test
    void testToEntity_shouldMapCategoryDtoToCategory() {
        CategoryDto categoryDto = CategoryDto.builder()
                .id(2)
                .name("Science Fiction")
                .build();

        Category category = categoryMapper.toEntity(categoryDto);

        assertEquals(categoryDto.getId(), category.getId());
        assertEquals(categoryDto.getName(), category.getName());
    }
}

