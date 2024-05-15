package pl.masters.testmail.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.masters.testmail.model.category.Category;
import pl.masters.testmail.model.user.User;
import pl.masters.testmail.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAllUsersByCategory(Category category) {
        return userRepository.findAllBySubscriptionsContaining(category);
    }
}
