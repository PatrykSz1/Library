package pl.masters.testmail.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import pl.masters.testmail.model.message.Message;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendEmail(Message message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(message.getTo());
        mailMessage.setSubject(message.getTopic());
        mailMessage.setText(message.getText());
        mailSender.send(mailMessage);
    }
}
