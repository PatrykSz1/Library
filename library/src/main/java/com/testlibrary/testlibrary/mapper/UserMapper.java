package com.testlibrary.testlibrary.mapper;

import com.testlibrary.testlibrary.model.user.User;
import com.testlibrary.testlibrary.model.user.UserDto;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {

    UserDto toDto(User user);

    User toEntity(UserDto userDto);

}
