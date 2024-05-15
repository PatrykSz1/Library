package com.testlibrary.testlibrary.mapper;

import com.testlibrary.testlibrary.common.Role;
import com.testlibrary.testlibrary.model.user.User;
import com.testlibrary.testlibrary.model.user.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest(classes = {UserMapperImpl.class})
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void testToDto_shouldMapUserToDto() {
        User user = User.builder()
                .email("mail")
                .role(Role.EMPLOYEE)
                .build();

        UserDto userDto = userMapper.toDto(user);

        assertEquals(userDto.getEmail(), user.getEmail());
        assertEquals(userDto.getRole(), user.getRole());
    }
}