package pl.masters.testmail.mapper;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.masters.testmail.model.user.User;
import pl.masters.testmail.model.user.UserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;


@Slf4j
@SpringBootTest(classes = {UserMapperImpl.class})
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void testToDto_shouldMapUserToDto() {
        User user = User.builder()
                .email("mail")
                .role("role")
                .build();

        UserDto userDto = userMapper.toDto(user);

        assertEquals(userDto.getEmail(), user.getEmail());
        assertEquals(userDto.getRole(), user.getRole());
    }
}