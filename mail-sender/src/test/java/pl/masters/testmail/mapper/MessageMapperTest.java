package pl.masters.testmail.mapper;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.masters.testmail.model.message.Message;
import pl.masters.testmail.model.message.MessageDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(classes = {MessageMapperImpl.class})
public class MessageMapperTest {


    @Autowired
    private MessageMapper messageMapper;

    @Test
    void testToDto_shouldMapMessageToDto() {
        Message message = Message.builder()
                .to("patrykszmczk459@gmail.com")
                .topic("New Book Was Added")
                .text("Category which you subscribed has new book.")
                .build();

        MessageDto messageDto = messageMapper.mapToDto(message);

        assertEquals(messageDto.getTo(), message.getTo());
        assertEquals(messageDto.getTopic(), message.getTopic());
        assertEquals(messageDto.getText(), message.getText());
    }
}


