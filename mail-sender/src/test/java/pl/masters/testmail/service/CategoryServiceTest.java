package pl.masters.testmail.service;


import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.masters.testmail.model.category.Category;
import pl.masters.testmail.repository.CategoryRepository;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    public void testFindById_CategoryExists() {
        Category mockCategory = new Category();
        mockCategory.setId(1);
        mockCategory.setName("Test Category");

        when(categoryRepository.findById(1)).thenReturn(java.util.Optional.of(mockCategory));

        Category result = categoryService.findById(1);

        assertEquals(1, result.getId());
        assertEquals("Test Category", result.getName());

        verify(categoryRepository, times(1)).findById(1);
    }

    @Test
    public void testFindById_CategoryNotFound() {
        when(categoryRepository.findById(anyInt())).thenReturn(java.util.Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            categoryService.findById(1);
        });
        verify(categoryRepository, times(1)).findById(1);
    }
}

