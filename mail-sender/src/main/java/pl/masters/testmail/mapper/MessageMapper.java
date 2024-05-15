package pl.masters.testmail.mapper;

import org.mapstruct.Mapper;
import pl.masters.testmail.model.message.Message;
import pl.masters.testmail.model.message.MessageDto;

@Mapper
public interface MessageMapper {

    MessageDto mapToDto(Message message);

    Message toEntity(MessageDto messageDto);
}

