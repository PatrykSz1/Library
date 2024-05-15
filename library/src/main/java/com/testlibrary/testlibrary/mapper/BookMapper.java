package com.testlibrary.testlibrary.mapper;

import com.testlibrary.testlibrary.model.book.Book;
import com.testlibrary.testlibrary.model.book.BookDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface BookMapper {

    @Mapping(source = "category.id", target = "categoryId")
    BookDto toDto(Book book);

    Book toEntity(BookDto bookDto);
}
