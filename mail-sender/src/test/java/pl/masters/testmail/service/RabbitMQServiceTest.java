package pl.masters.testmail.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.masters.testmail.mapper.BookMapper;
import pl.masters.testmail.model.book.Book;
import pl.masters.testmail.model.book.BookDto;
import pl.masters.testmail.model.category.Category;
import pl.masters.testmail.repository.CategoryRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class RabbitMQServiceTest {
    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private MailProcessingService mailProcessingService;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private RabbitMQService rabbitMQService;

    @Test
    public void testReceiveBook() throws JsonProcessingException {
        String testMessage = "{\"title\":\"Test Book\",\"category\":\"Test Category\",\"categoryId\":1}";
        BookDto testBookDto = new BookDto();
        testBookDto.setTitle("Test Book");
        testBookDto.setCategoryId(1);

        Book testBook = new Book();
        testBook.setTitle("Test Book");
        testBook.setCategory(createCategory());

        when(objectMapper.readValue(testMessage, BookDto.class)).thenReturn(testBookDto);
        when(bookMapper.toEntity(testBookDto)).thenReturn(testBook);

        Category testCategory = createCategory();
        when(categoryRepository.findById(testBookDto.getCategoryId())).thenReturn(Optional.ofNullable(testCategory));

        rabbitMQService.receiveBook(testMessage);

        verify(mailProcessingService, times(1)).processBookFromQueue(testBook);
    }


    @Test
    public void testReceiveBook_InvalidMessage() throws JsonProcessingException {
        String invalidMessage = "Invalid JSON Message";

        when(objectMapper.readValue(invalidMessage, BookDto.class)).thenThrow(new JsonParseException(null, "Invalid JSON"));

        assertThrows(JsonProcessingException.class, () -> rabbitMQService.receiveBook(invalidMessage));
        verify(mailProcessingService, never()).processBookFromQueue(any());
    }

    private Category createCategory() {
        return Category.builder()
                .id(1)
                .name("Romantic")
                .build();
    }
}
