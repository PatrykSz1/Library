package com.testlibrary.testlibrary.mapper;

import com.testlibrary.testlibrary.model.category.Category;
import com.testlibrary.testlibrary.model.category.CategoryDto;
import org.mapstruct.Mapper;

@Mapper
public interface CategoryMapper {

    CategoryDto toDto(Category category);

    Category toEntity(CategoryDto categoryDto);
}
