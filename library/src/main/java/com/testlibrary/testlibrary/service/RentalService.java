package com.testlibrary.testlibrary.service;

import com.testlibrary.testlibrary.model.book.Book;
import com.testlibrary.testlibrary.model.rental.Rental;
import com.testlibrary.testlibrary.model.rental.RentalCommand;
import com.testlibrary.testlibrary.model.user.User;
import com.testlibrary.testlibrary.repository.BookRepository;
import com.testlibrary.testlibrary.repository.RentalRepository;
import com.testlibrary.testlibrary.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalRepository rentalRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public Page<Rental> getAllRentals(Pageable pageable) {
        return rentalRepository.findAll(pageable);
    }

    public Rental getRentalById(int id) {
        return rentalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rental not found"));
    }

    @Transactional
    public Rental createRental(RentalCommand rentalCommand) {
        LocalDate startDate = rentalCommand.getStartDate();
        LocalDate endDate = rentalCommand.getEndDate();

        int bookId = rentalCommand.getBookId();
        int userId = rentalCommand.getUserId();

        Book book = bookRepository.findWithLockingByIdAndAvailabilityTrue(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with ID: " + bookId));
        User user = userRepository.findWithLockingById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        if (rentalRepository.existsByStartDateIsBeforeAndEndDateIsAfterAndBook(endDate, startDate, book)) {
            throw new EntityExistsException("The book is already booked during the specified time interval.");
        }

        Rental rental = Rental.builder()
                .startDate(rentalCommand.getStartDate())
                .endDate(rentalCommand.getEndDate())
                .user(user)
                .book(book)
                .build();

        return rentalRepository.save(rental);
    }

    @Transactional
    public Rental updateRental(int id, RentalCommand rentalCommand) {
        Rental existingRental = getRentalById(id);

        if (!rentalCommand.isEndDateAfterStartDate()) {
            throw new EntityExistsException("End date cannot be before the start date.");
        }

        LocalDate startDate = rentalCommand.getStartDate();
        LocalDate endDate = rentalCommand.getEndDate();

        existingRental.setStartDate(startDate);
        existingRental.setEndDate(endDate);

        return existingRental;
    }

    public void deleteRental(int id) {
        rentalRepository.deleteById(id);
    }
}

