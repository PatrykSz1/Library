package com.testlibrary.testlibrary.controller;


import com.testlibrary.testlibrary.mapper.CategoryMapper;
import com.testlibrary.testlibrary.model.category.CategoryCommand;
import com.testlibrary.testlibrary.model.category.CategoryDto;
import com.testlibrary.testlibrary.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/categories")
@RestController
@RequiredArgsConstructor
@EnableCaching
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @GetMapping
    public ResponseEntity<Page<CategoryDto>> findAll(@PageableDefault Pageable pageable) {
        Page<CategoryDto> categoryDtoPage = categoryService.findAll(pageable);
        return new ResponseEntity<>(categoryDtoPage, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getById(@PathVariable int id) {
        CategoryDto categoryDto = categoryMapper.toDto(categoryService.findById(id));
        return new ResponseEntity<>(categoryDto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CategoryDto> create(@RequestBody @Valid CategoryCommand categoryCommand) {
        CategoryDto createdCategoryDto = categoryMapper.toDto(categoryService.save(categoryCommand));
        return new ResponseEntity<>(createdCategoryDto, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable int id) {
        categoryService.deleteCategory(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}