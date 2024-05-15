package com.testlibrary.testlibrary.repository;


import com.testlibrary.testlibrary.model.book.Book;
import com.testlibrary.testlibrary.model.rental.Rental;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RentalRepository extends JpaRepository<Rental, Integer> {

    boolean existsByStartDateIsBeforeAndEndDateIsAfterAndBook(LocalDate startDate, LocalDate endDate, Book book);

    boolean existsByBookAndEndDateIsAfterOrEndDateIsBefore(Book book, LocalDate endDateAfter, LocalDate endDateBefore);

    List<Rental> findByBookAndEndDateIsAfter(Book book, LocalDate endDate);

    List<Rental> findByBookAndEndDateIsBefore(Book book, LocalDate endDate);

    Page<Rental> findAll(Pageable pageable);

    void deleteById(int id);
}