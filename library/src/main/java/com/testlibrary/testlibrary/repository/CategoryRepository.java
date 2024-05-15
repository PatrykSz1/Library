package com.testlibrary.testlibrary.repository;

import com.testlibrary.testlibrary.model.category.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    Optional<Category> findCategoryByName(String name);

    Page<Category> findAll(Pageable pageable);
}
