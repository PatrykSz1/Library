package com.testlibrary.testlibrary.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testlibrary.testlibrary.configuration.RabbitMQConfiguration;
import com.testlibrary.testlibrary.mapper.BookMapper;
import com.testlibrary.testlibrary.model.book.Book;
import com.testlibrary.testlibrary.model.book.BookDto;
import com.testlibrary.testlibrary.monitoring.MonitorMethodModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMQService {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final BookMapper bookMapper;

    public void sendMessage(Book book) {
        BookDto bookDto = bookMapper.toDto(book);
        rabbitTemplate.convertAndSend(
                RabbitMQConfiguration.EXCHANGE,
                RabbitMQConfiguration.ROUTING_KEY,
                convertToString(bookDto)
        );
    }

    private String convertToString(BookDto bookDto) {
        try {
            return objectMapper.writeValueAsString(bookDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Invalid object");
        }
    }

    public void sendMessageToRabbitMQ(MonitorMethodModel monitorMethodModel) {
        try {
            String logMessageJson = objectMapper.writeValueAsString(monitorMethodModel);
            rabbitTemplate.convertAndSend(RabbitMQConfiguration.LOG_QUEUE_NAME, logMessageJson);
        } catch (JsonProcessingException e) {
            log.error("Error converting log message to JSON: {}", e.getMessage());
        }
    }
}

