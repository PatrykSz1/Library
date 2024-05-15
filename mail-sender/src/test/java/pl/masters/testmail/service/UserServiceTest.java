package pl.masters.testmail.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.masters.testmail.model.category.Category;
import pl.masters.testmail.model.user.User;
import pl.masters.testmail.repository.UserRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void testGetAllUsersByCategory() {
        String categoryName = "Test Category";
        Category category = Category.builder().id(1).name(categoryName).build();

        User user1 = User.builder()
                .email("user1@example.com")
                .subscriptions(Collections.singleton(category))
                .build();


        User user3 = User.builder()
                .email("user3@example.com")
                .subscriptions(Collections.singleton(category))
                .build();

        List<User> allUsers = Arrays.asList(user1, user3);

        when(userRepository.findAllBySubscriptionsContaining(category)).thenReturn(allUsers);

        List<User> result = userService.getAllUsersByCategory(category);

        assertEquals(2, result.size());
        assertTrue(result.contains(user1));
        assertTrue(result.contains(user3));
    }

    @Test
    public void testGetAllUsersByCategory_NoUsersWithCategory() {
        String categoryName = "Non-existent Category";
        Category category = Category.builder().id(2).name(categoryName).build();

        when(userRepository.findAllBySubscriptionsContaining(category)).thenReturn(Collections.emptyList());

        List<User> result = userService.getAllUsersByCategory(category);

        assertTrue(result.isEmpty());
    }
}
