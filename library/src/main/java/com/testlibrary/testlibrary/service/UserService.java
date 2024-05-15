package com.testlibrary.testlibrary.service;

import com.testlibrary.testlibrary.mapper.UserMapper;
import com.testlibrary.testlibrary.model.category.Category;
import com.testlibrary.testlibrary.model.user.User;
import com.testlibrary.testlibrary.model.user.UserCommand;
import com.testlibrary.testlibrary.model.user.UserDto;
import com.testlibrary.testlibrary.repository.CategoryRepository;
import com.testlibrary.testlibrary.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder encoder;
    private final UserMapper userMapper;

    public Page<UserDto> getUsers(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);

        List<UserDto> userDtos = userPage.getContent().stream()
                .map(user -> {
                    int subscriptionCount = user.getSubscriptions().size();
                    UserDto userDto = userMapper.toDto(user);
                    userDto.setSubscriptionsAmount(subscriptionCount);
                    return userDto;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(userDtos, pageable, userPage.getTotalElements());
    }

    public User getUserById(int id) {
        return userRepository.findWithLockingById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Transactional
    public User createUser(UserCommand userCommand) {
        User user = User.builder()
                .email(userCommand.getEmail())
                .password(encoder.encode(userCommand.getPassword()))
                .firstName(userCommand.getFirstName())
                .lastName(userCommand.getLastName())
                .role(userCommand.getRole())
                .blocked(userCommand.isBlocked())
                .build();
        return userRepository.save(user);
    }

    @Transactional
    @Modifying
    public User blockUser(int id) {
        User existingUser = getUserById(id);
        existingUser.setBlocked(true);
        return existingUser;
    }

    @Transactional
    @Modifying
    public User unlockUser(int id) {
        User existingUser = getUserById(id);
        existingUser.setBlocked(false);
        return existingUser;
    }

    @Modifying
    @Transactional
    public void addCategoryToSubscription(int userId, int categoryId, String email) {
        User existingUser = userRepository.findByEmail(email);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with ID: " + userId));

        if (existingUser.getId() != userId) {
            throw new EntityExistsException("User Id is not the same as given");
        }
        existingUser.getSubscriptions().add(category);
        userRepository.save(existingUser);
    }

    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }
}


