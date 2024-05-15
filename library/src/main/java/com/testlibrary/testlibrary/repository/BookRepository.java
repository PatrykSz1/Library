package com.testlibrary.testlibrary.repository;

import com.testlibrary.testlibrary.model.book.Book;
import com.testlibrary.testlibrary.model.category.Category;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Integer> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Book> findWithLockingByIdAndAvailabilityTrue(int id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Book> findWithLockingById(int id);

    Page<Book> findAll(Pageable pageable);

    int countBooksByCategory(Category category);

    @Modifying
    @Query("UPDATE Book b SET b.availability = :availability WHERE b.id = :id")
    void updateByIdAndAvailability(int id, boolean availability);
}

