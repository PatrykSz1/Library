package com.testlibrary.testlibrary.service;

import com.testlibrary.testlibrary.common.Role;
import com.testlibrary.testlibrary.mapper.UserMapper;
import com.testlibrary.testlibrary.model.user.User;
import com.testlibrary.testlibrary.model.user.UserCommand;
import com.testlibrary.testlibrary.model.user.UserDto;
import com.testlibrary.testlibrary.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;


    @Test
    void testGetUsers() {
        List<User> users = usersList();

        Pageable pageable = Pageable.unpaged();

        Page<User> userPage = new PageImpl<>(users);
        List<UserDto> userDtos = new ArrayList<>();
        UserDto userDto1 = UserDto.builder()
                .email("test@example.com")
                .password("password123")
                .role(Role.EMPLOYEE)
                .firstName("John")
                .lastName("Doe")
                .blocked(false)
                .subscriptionsAmount(2)
                .build();
        UserDto userDto2 = UserDto.builder()
                .email("test2@example.com")
                .password("password1234")
                .role(Role.EMPLOYEE)
                .firstName("Johny")
                .lastName("Dowe")
                .blocked(false)
                .subscriptionsAmount(1)
                .build();
        userDtos.add(userDto1);
        userDtos.add(userDto2);

        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userMapper.toDto(users.get(0))).thenReturn(userDto1);
        when(userMapper.toDto(users.get(1))).thenReturn(userDto2);

        Page<UserDto> result = userService.getUsers(pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals("John", result.getContent().get(0).getFirstName());
        assertEquals("Johny", result.getContent().get(1).getFirstName());
    }

@Test
    void getUsers_ShouldReturnEmptyPage_WhenNoUsersFound() {
        List<User> emptyList = new ArrayList<>();
        Page<User> emptyPage = new PageImpl<>(emptyList);

        when(userRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        Page<UserDto> result = userService.getUsers(Pageable.unpaged());

        assertEquals(0, result.getTotalElements());
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    public void testCreateUser() {
        UserCommand userCommand = UserCommand.builder()
                .email("test@example.com")
                .password("password")
                .firstName("John")
                .lastName("Doe")
                .role(Role.EMPLOYEE)
                .blocked(false)
                .build();

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1);
            return savedUser;
        });

        User createdUser = userService.createUser(userCommand);

        verify(userRepository, times(1)).save(any(User.class));


        assertNotNull(createdUser);
        assertEquals("test@example.com", createdUser.getEmail());
        assertEquals("John", createdUser.getFirstName());
        assertEquals("Doe", createdUser.getLastName());
        assertEquals(Role.EMPLOYEE, createdUser.getRole());
        assertFalse(createdUser.isBlocked());
    }

    @Test
    void createUser_ShouldThrowEntityException_WhenUserExists() {
        User user = createUser();

        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(user);
    }

    @Test
    void deleteUser_ShouldDeleteExistingUser() {
        int id = 1;
        userService.deleteUser(id);

        verify(userRepository, times(1)).deleteById(id);
    }


    private User createUser() {
        return User.builder()
                .email("test@example.com")
                .password("password123")
                .role(Role.EMPLOYEE)
                .firstName("John")
                .lastName("Doe")
                .blocked(false)
                .build();
    }

    private List<User> usersList() {
        return List.of(
                User.builder()
                        .email("test@example.com")
                        .password("password123")
                        .role(Role.EMPLOYEE)
                        .firstName("John")
                        .lastName("Doe")
                        .blocked(false)
                        .subscriptions(new HashSet<>(1))
                        .build(),
                User.builder()
                        .email("test2@example.com")
                        .password("password1234")
                        .role(Role.EMPLOYEE)
                        .firstName("Johny")
                        .lastName("Dowe")
                        .subscriptions(new HashSet<>(1))
                        .blocked(false)
                        .build()
        );
    }
}