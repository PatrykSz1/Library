package com.testlibrary.testlibrary.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testlibrary.testlibrary.configuration.RabbitMQConfiguration;
import com.testlibrary.testlibrary.mapper.BookMapper;
import com.testlibrary.testlibrary.model.book.Book;
import com.testlibrary.testlibrary.model.book.BookDto;
import com.testlibrary.testlibrary.model.category.Category;
import com.testlibrary.testlibrary.monitoring.MonitorMethodModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RabbitMQServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private RabbitMQService rabbitMQService;

    private static Category createCategory() {
        return Category.builder()
                .id(1)
                .name("Romantic")
                .build();
    }

    private static Book createBook() {
        return Book.builder()
                .id(1)
                .title("Sample Book")
                .category(createCategory())
                .availability(true)
                .build();
    }

    private static BookDto createBookDto() {
        Book book = createBook();
        return BookDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .categoryId(book.getCategory().getId())
                .availability(true)
                .build();
    }

    @Test
    void testSendMessage_shouldSendMessageToRabbitMQ() throws JsonProcessingException {
        String json = "{\"id\":1,\"title\":\"Sample Book\",\"category\":\"ROMANTIC\",\"availability\":true}";
        Book book = createBook();
        BookDto bookDto = createBookDto();

        when(bookMapper.toDto(book)).thenReturn(bookDto);
        when(objectMapper.writeValueAsString(bookDto)).thenReturn(json);

        rabbitMQService.sendMessage(book);

        verify(bookMapper, times(1)).toDto(book);
        verify(objectMapper, times(1)).writeValueAsString(bookDto);
        verify(rabbitTemplate, times(1)).convertAndSend(
                RabbitMQConfiguration.EXCHANGE,
                RabbitMQConfiguration.ROUTING_KEY,
                json
        );
    }

    @Test
    void testSendMessage_shouldThrowExceptionOnJsonProcessingError() throws JsonProcessingException {
        Book book = createBook();
        BookDto bookDto = createBookDto();

        when(bookMapper.toDto(book)).thenReturn(bookDto);
        when(objectMapper.writeValueAsString(bookDto)).thenThrow(JsonProcessingException.class);

        assertThrows(RuntimeException.class, () -> rabbitMQService.sendMessage(book));
    }

    @Test
    void testSendMessageToRabbitMQ() throws JsonProcessingException {
        MonitorMethodModel monitorMethodModel = MonitorMethodModel.builder()
                .user("testUser")
                .timestamp(123456789L)
                .method("testMethod")
                .datetime(LocalDateTime.of(2023, 7, 18, 12, 34, 56))
                .build();

        String expectedJson = "{\"user\":\"testUser\",\"timestamp\":123456789,\"method\":\"testMethod\",\"datetime\":\"2023-07-18T12:34:56\"}";

        when(objectMapper.writeValueAsString(monitorMethodModel)).thenReturn(expectedJson);

        rabbitMQService.sendMessageToRabbitMQ(monitorMethodModel);

        verify(objectMapper, times(1)).writeValueAsString(monitorMethodModel);
        verify(rabbitTemplate, times(1)).convertAndSend(RabbitMQConfiguration.LOG_QUEUE_NAME, expectedJson);
    }

    @Test
    void testSendMessageToRabbitMQ_JsonProcessingException() throws JsonProcessingException {
        MonitorMethodModel monitorMethodModel = MonitorMethodModel.builder()
                .user("testUser")
                .timestamp(123456789L)
                .method("testMethod")
                .datetime(LocalDateTime.of(2023, 7, 18, 12, 34, 56))
                .build();

        when(objectMapper.writeValueAsString(monitorMethodModel)).thenThrow(new JsonProcessingException("Error") {
        });

        assertDoesNotThrow(() -> rabbitMQService.sendMessageToRabbitMQ(monitorMethodModel));

        verify(objectMapper, times(1)).writeValueAsString(monitorMethodModel);

        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString());
    }
}

