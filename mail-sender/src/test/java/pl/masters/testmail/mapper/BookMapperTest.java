package pl.masters.testmail.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.masters.testmail.model.book.Book;
import pl.masters.testmail.model.book.BookDto;
import pl.masters.testmail.model.category.Category;

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

    private Category createCategory() {
        return Category.builder()
                .id(1)
                .name("Romantic")
                .build();
    }
}