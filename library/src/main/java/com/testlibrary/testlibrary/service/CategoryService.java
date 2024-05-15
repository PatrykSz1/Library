package com.testlibrary.testlibrary.service;

import com.testlibrary.testlibrary.mapper.CategoryMapper;
import com.testlibrary.testlibrary.model.category.Category;
import com.testlibrary.testlibrary.model.category.CategoryCommand;
import com.testlibrary.testlibrary.model.category.CategoryDto;
import com.testlibrary.testlibrary.repository.BookRepository;
import com.testlibrary.testlibrary.repository.CategoryRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final CategoryMapper categoryMapper;


    @Cacheable("categoriesCache")
    public Page<CategoryDto> findAll(Pageable pageable) {
        Page<Category> categoryPage = categoryRepository.findAll(pageable);

        List<CategoryDto> categoryDtos = categoryPage.getContent().stream()
                .map(category -> {
                    int bookAmount = bookRepository.countBooksByCategory(category);
                    CategoryDto categoryDto = categoryMapper.toDto(category);
                    categoryDto.setBookAmount(bookAmount);
                    return categoryDto;
                })
                .collect(Collectors.toList());

        log.info("Getting categories from cache...");
        return new PageImpl<>(categoryDtos, pageable, categoryPage.getTotalElements());
    }

    @Cacheable("categoryCache")
    public Category findById(int id) {
        log.info("Getting category from cache...");
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with ID: " + id));
    }

    public Category save(CategoryCommand categoryCommand) {
        Category category = new Category();
        category.setName(categoryCommand.getName());

        try {
            return categoryRepository.save(category);
        } catch (DataIntegrityViolationException ex) {
            throw new EntityExistsException("Category with this name already exists.");
        }
    }

    public Category findCategoryByName(String categoryName) {
        return categoryRepository.findCategoryByName(categoryName)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with this name: " + categoryName));
    }

    @Transactional
    public void deleteCategory(int id) {
        categoryRepository.deleteById(id);
    }
}
