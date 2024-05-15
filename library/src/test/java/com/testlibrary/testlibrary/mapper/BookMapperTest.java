package com.testlibrary.testlibrary.mapper;

import com.testlibrary.testlibrary.model.book.Book;
import com.testlibrary.testlibrary.model.book.BookDto;
import com.testlibrary.testlibrary.model.category.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {BookMapperImpl.class})
class BookMapperTest {

    @Autowired
    private BookMapper bookMapper;

    @Test
    void testToDto_shouldMapBookToDto() {
        Book book = Book.builder()
                .title("title")
                .category(createCategory())
                .build();

        BookDto bookDto = bookMapper.toDto(book);

        assertEquals(bookDto.getTitle(), book.getTitle());
    }

    @Test
    void testToEntity_shouldMapBookDtoToEntity() {
        BookDto bookDto = BookDto.builder()
                .title("title")
                .categoryId(createCategory().getId())
                .build();

        Book book = bookMapper.toEntity(bookDto);

        assertThat(book.getTitle()).isEqualTo(bookDto.getTitle());
    }

    private Category createCategory() {
        return Category.builder()
                .id(1)
                .name("Romantic")
                .build();
    }
}