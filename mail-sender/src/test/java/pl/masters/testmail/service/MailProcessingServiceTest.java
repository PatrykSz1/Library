package pl.masters.testmail.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.masters.testmail.model.book.Book;
import pl.masters.testmail.model.book.BookDto;
import pl.masters.testmail.model.category.Category;
import pl.masters.testmail.model.message.Message;
import pl.masters.testmail.model.user.User;
import pl.masters.testmail.repository.UserRepository;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailProcessingServiceTest {


    @Mock
    private EmailService emailService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MailProcessingService mailProcessingService;

    @Captor
    private ArgumentCaptor<Message> messageCaptor;

    @Test
    public void testProcessBookFromQueue_ShouldSendMail() {
        User testUser = User.builder()
                .email("test@example.com")
                .build();

        Book testBook = new Book();
        testBook.setTitle("Test Book");
        testBook.setCategory(createCategory());

        List<User> users = Collections.singletonList(testUser);

        when(userRepository.findAllBySubscriptionsContaining(testBook.getCategory())).thenReturn(users);

        mailProcessingService.processBookFromQueue(testBook);

        verify(emailService, times(1)).sendEmail(messageCaptor.capture());

        String expectedTopic = "New Book Was Added";

        Message capturedMessage = messageCaptor.getValue();
        assertEquals("test@example.com", capturedMessage.getTo());
        assertEquals(expectedTopic, capturedMessage.getTopic());
    }

    @Test
    public void testProcessBookFromQueue_NoUsers() {
        Book testBook = new Book();
        testBook.setTitle("Test Book");
        testBook.setCategory(createCategory());

        when(userRepository.findAllBySubscriptionsContaining(testBook.getCategory())).thenReturn(Collections.emptyList());

        mailProcessingService.processBookFromQueue(testBook);

        verify(emailService, never()).sendEmail(any());
    }


    @Test
    public void testCreateMessage_ShouldCreateMessage() {
        String mail = "recipient@example.com";
        String topic = "Test Subject";
        String text = "Test Message Body";

        Message message = mailProcessingService.createMessage(mail, topic, text);

        assertEquals(mail, message.getTo());
        assertEquals(topic, message.getTopic());
        assertEquals(text, message.getText());
    }

    @Test
    public void testCreateMessage_ShouldBeInvalidInput() {
        String mail = null;
        String topic = null;
        String text = null;

        Message message = mailProcessingService.createMessage(mail, topic, text);

        assertNull(message.getTo());
        assertNull(message.getTopic());
        assertNull(message.getText());
    }

    private Category createCategory() {
        return Category.builder()
                .id(1)
                .name("Romantic")
                .build();
    }
}