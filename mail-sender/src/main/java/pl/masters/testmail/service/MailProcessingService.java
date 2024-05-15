package pl.masters.testmail.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.masters.testmail.model.category.Category;
import pl.masters.testmail.model.message.Message;
import pl.masters.testmail.model.book.Book;
import pl.masters.testmail.model.user.User;
import pl.masters.testmail.repository.CategoryRepository;
import pl.masters.testmail.repository.UserRepository;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class MailProcessingService {

    private final EmailService emailService;
    private final UserRepository userRepository;

    public void processBookFromQueue(Book book) {
        List<User> users = userRepository.findAllBySubscriptionsContaining(book.getCategory());

        String topic = "New Book Was Added";
        String text = "Category which you subscribed has new book.\n\n" +
                "Title: " + book.getTitle() + "\n" +
                "Category: " + book.getCategory().getName();

        users.stream()
                .map(User::getEmail)
                .map(mail -> createMessage(mail, topic, text))
                .forEach(emailService::sendEmail);

    }

    public Message createMessage(String mail, String topic, String text) {
        return Message.builder()
                .to(mail)
                .topic(topic)
                .text(text)
                .build();
    }
}