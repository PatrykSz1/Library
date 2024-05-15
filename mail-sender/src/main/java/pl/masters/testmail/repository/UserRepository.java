package pl.masters.testmail.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.masters.testmail.model.category.Category;
import pl.masters.testmail.model.user.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findAllBySubscriptionsContaining(Category subscriptions);
}

