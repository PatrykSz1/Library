package pl.masters.testmail.mapper;

import org.mapstruct.Mapper;
import pl.masters.testmail.model.category.Category;
import pl.masters.testmail.model.category.CategoryDto;

@Mapper
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category toEntity(CategoryDto categoryDto);
}
