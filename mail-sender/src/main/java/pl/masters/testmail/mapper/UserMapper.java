package pl.masters.testmail.mapper;

import org.mapstruct.Mapper;
import pl.masters.testmail.model.user.User;
import pl.masters.testmail.model.user.UserDto;

@Mapper
public interface UserMapper {
    UserDto toDto(User user);

    User toEntity(UserDto userDto);
}
