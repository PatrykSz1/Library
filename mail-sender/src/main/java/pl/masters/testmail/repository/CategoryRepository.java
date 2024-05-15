package pl.masters.testmail.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.masters.testmail.model.category.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
