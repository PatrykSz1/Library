package pl.masters.testmail.service;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import pl.masters.testmail.model.message.Message;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    private Message testMessage;

    @BeforeEach
    public void setUp() {
        testMessage = new Message();
        testMessage.setTo("recipient@example.com");
        testMessage.setTopic("Test Subject");
        testMessage.setText("Test Message Body");
    }

    @Test
    void testSendEmail_ShouldSendMail() {
        emailService.sendEmail(testMessage);

        SimpleMailMessage expectedMailMessage = new SimpleMailMessage();
        expectedMailMessage.setTo("recipient@example.com");
        expectedMailMessage.setSubject("Test Subject");
        expectedMailMessage.setText("Test Message Body");

        verify(mailSender).send(expectedMailMessage);
    }

    @Test
    public void testSendEmail_ShouldNotSent() {
        emailService.sendEmail(testMessage);

        verify(mailSender, never()).send((MimeMessage) any());
    }
}