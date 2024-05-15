package pl.masters.testmail.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import pl.masters.testmail.mapper.BookMapper;
import pl.masters.testmail.model.book.Book;
import pl.masters.testmail.model.book.BookDto;
import pl.masters.testmail.repository.CategoryRepository;


@Service
@Slf4j
@RequiredArgsConstructor
public class RabbitMQService {

    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;
    private final ObjectMapper objectMapper;
    private final BookMapper bookMapper;
    private final MailProcessingService mailProcessingService;


    @RabbitListener(queues = "library_books")
    public void receiveBook(String message) throws JsonProcessingException {
        BookDto bookDto = objectMapper.readValue(message, BookDto.class);
        Book book = bookMapper.toEntity(bookDto);
        book.setCategory(categoryRepository.findById(bookDto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found.")));
        mailProcessingService.processBookFromQueue(book);
        log.info(message);
    }
}
