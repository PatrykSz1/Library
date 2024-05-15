package com.testlibrary.testlibrary.repository;

import com.testlibrary.testlibrary.model.user.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Integer> {
    void deleteById(int id);

    User findByEmail(String email);

    Optional<User> findByBlockedFalseAndEmail(String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<User> findWithLockingById(int id);

    @EntityGraph(attributePaths = "subscriptions")
    Page<User> findAll(Pageable pageable);
}
