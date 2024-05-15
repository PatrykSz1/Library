package com.testlibrary.testlibrary.service;

import com.testlibrary.testlibrary.common.Role;
import com.testlibrary.testlibrary.model.book.Book;
import com.testlibrary.testlibrary.model.category.Category;
import com.testlibrary.testlibrary.model.rental.Rental;
import com.testlibrary.testlibrary.model.rental.RentalCommand;
import com.testlibrary.testlibrary.model.user.User;
import com.testlibrary.testlibrary.repository.BookRepository;
import com.testlibrary.testlibrary.repository.RentalRepository;
import com.testlibrary.testlibrary.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RentalServiceTest {

    @Mock
    BookRepository bookRepository;
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private RentalService rentalService;

    @Test
    void getAllRentals_ShouldGetListOfRentals() {
        List<Rental> rentalList = rentalsList();
        Page<Rental> page = new PageImpl<>(rentalList);

        when(rentalRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Rental> result = rentalService.getAllRentals(Pageable.unpaged());

        assertEquals(rentalList.size(), result.getTotalElements());

        verify(rentalRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getRentalById_ShouldGetRental() {
        Rental rental = createRental();
        int rentalId = rental.getId();

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));

        Rental result = rentalService.getRentalById(rentalId);

        assertEquals(rental, result);
    }

    @Test
    void testGetRentalById_EntityNotFoundException() {
        int nonExistentId = 123;

        when(rentalRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> rentalService.getRentalById(nonExistentId));
    }

    @Test
    void createRental_ShouldCreateRental() {
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(5);
        RentalCommand rentalCommand = RentalCommand.builder()
                .startDate(startDate)
                .endDate(endDate)
                .bookId(1)
                .userId(1)
                .build();

        Book book = createBook();
        User user = createUser();

        when(bookRepository.findWithLockingByIdAndAvailabilityTrue(anyInt())).thenReturn(Optional.of(book));
        when(userRepository.findWithLockingById(anyInt())).thenReturn(Optional.of(user));
        when(rentalRepository.existsByStartDateIsBeforeAndEndDateIsAfterAndBook(
                eq(endDate), eq(startDate), any(Book.class))).thenReturn(false);
        when(rentalRepository.save(any(Rental.class))).thenAnswer(invocation -> {
            Rental rentalToSave = invocation.getArgument(0);
            rentalToSave.setId(1);
            return rentalToSave;
        });

        Rental result = rentalService.createRental(rentalCommand);

        assertNotNull(result);
        assertEquals(startDate, result.getStartDate());
        assertEquals(endDate, result.getEndDate());
        assertEquals(book, result.getBook());
        assertEquals(user, result.getUser());

        verify(bookRepository, times(1)).findWithLockingByIdAndAvailabilityTrue(anyInt());
        verify(userRepository, times(1)).findWithLockingById(anyInt());
        verify(rentalRepository, times(1)).existsByStartDateIsBeforeAndEndDateIsAfterAndBook(
                eq(endDate), eq(startDate), any(Book.class));
        verify(rentalRepository, times(1)).save(any(Rental.class));
    }

    @Test
    void createRental_ShouldCreateRental_WhenStartDateAndEndDateAreEqual() {
        LocalDate startDate = LocalDate.now().plusDays(5);
        LocalDate endDate = LocalDate.now().plusDays(5);
        RentalCommand rentalCommand = RentalCommand.builder()
                .startDate(startDate)
                .endDate(endDate)
                .bookId(1)
                .userId(1)
                .build();

        Book book = createBook();
        User user = createUser();

        when(bookRepository.findWithLockingByIdAndAvailabilityTrue(anyInt())).thenReturn(Optional.of(book));
        when(userRepository.findWithLockingById(anyInt())).thenReturn(Optional.of(user));
        when(rentalRepository.existsByStartDateIsBeforeAndEndDateIsAfterAndBook(
                eq(endDate), eq(startDate), any(Book.class))).thenReturn(false);
        when(rentalRepository.save(any(Rental.class))).thenAnswer(invocation -> {
            Rental rentalToSave = invocation.getArgument(0);
            rentalToSave.setId(1);
            return rentalToSave;
        });

        Rental result = rentalService.createRental(rentalCommand);

        assertNotNull(result);
        assertEquals(startDate, result.getStartDate());
        assertEquals(endDate, result.getEndDate());
        assertEquals(book, result.getBook());
        assertEquals(user, result.getUser());

        verify(bookRepository, times(1)).findWithLockingByIdAndAvailabilityTrue(anyInt());
        verify(userRepository, times(1)).findWithLockingById(anyInt());
        verify(rentalRepository, times(1)).existsByStartDateIsBeforeAndEndDateIsAfterAndBook(
                eq(endDate), eq(startDate), any(Book.class));
        verify(rentalRepository, times(1)).save(any(Rental.class));
    }

    @Test
    void createRental_ShouldNotCreateRental_WhenBookNotFound() {
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(5);
        RentalCommand rentalCommand = RentalCommand.builder()
                .startDate(startDate)
                .endDate(endDate)
                .build();

        assertThrows(EntityNotFoundException.class, () -> rentalService.createRental(rentalCommand));

        verify(rentalRepository, never()).save(any());
    }

    @Test
    void createRental_ShouldNotCreateRental_WhenUserNotFound() {
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(5);
        RentalCommand rentalCommand = RentalCommand.builder()
                .startDate(startDate)
                .endDate(endDate)
                .build();

        Book book = createBook();
        User user = createUser();
        String exceptionMessage = "User not found with ID: " + user.getId();

        when(bookRepository.findWithLockingByIdAndAvailabilityTrue(book.getId())).thenReturn(Optional.of(book));
        when(userRepository.findWithLockingById(user.getId())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> rentalService.createRental(rentalCommand));

        assertEquals(exceptionMessage, exception.getMessage());
        verify(rentalRepository, never()).save(any());
    }


    @Test
    void createRental_ShouldThrowEntityExistsException_WhenPeriodAlreadyBooked() {
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(5);
        RentalCommand rentalCommand = RentalCommand.builder()
                .startDate(startDate)
                .endDate(endDate)
                .build();

        Book book = createBook();
        User user = createUser();
        int userId = user.getId();

        when(bookRepository.findWithLockingByIdAndAvailabilityTrue(book.getId())).thenReturn(Optional.of(book));
        when(userRepository.findWithLockingById(userId)).thenReturn(Optional.of(user));
        when(rentalRepository.existsByStartDateIsBeforeAndEndDateIsAfterAndBook(endDate, startDate, book)).thenReturn(true);

        EntityExistsException exception = assertThrows(EntityExistsException.class,
                () -> rentalService.createRental(rentalCommand));

        String exceptionMessage = "The book is already booked during the specified time interval.";

        assertEquals(exceptionMessage, exception.getMessage());
        verify(rentalRepository, never()).save(any());
    }

    @Test
    void createRental_ShouldThrowIllegalArgumentException_WhenStartDateInPast() {
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(5);
        RentalCommand rentalCommand = RentalCommand.builder()
                .startDate(startDate)
                .endDate(endDate)
                .bookId(1)
                .userId(1)
                .build();

        assertThrows(EntityNotFoundException.class, () -> rentalService.createRental(rentalCommand));

        verify(rentalRepository, never()).save(any());
    }

    @Test
    void createRental_ShouldThrowIllegalArgumentException_WhenEndDateBeforeStartDate() {
        LocalDate startDate = LocalDate.now().plusDays(5);
        LocalDate endDate = LocalDate.now().plusDays(2);
        RentalCommand rentalCommand = RentalCommand.builder()
                .startDate(startDate)
                .endDate(endDate)
                .bookId(1)
                .userId(1)
                .build();

        assertThrows(EntityNotFoundException.class, () -> rentalService.createRental(rentalCommand));

        verify(rentalRepository, never()).save(any());
    }

    @Test
    void updateRental_ShouldUpdateTest() {
        LocalDate newStartDate = LocalDate.of(2023, 8, 1);
        LocalDate newEndDate = LocalDate.of(2023, 8, 10);
        RentalCommand rentalCommand = RentalCommand.builder()
                .startDate(newStartDate)
                .endDate(newEndDate)
                .build();

        Rental rental = createRental();
        int rentalId = rental.getId();

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));

        Rental result = rentalService.updateRental(rentalId, rentalCommand);

        assertEquals(newStartDate, rental.getStartDate());
        assertEquals(newEndDate, rental.getEndDate());

        assertEquals(newStartDate, result.getStartDate());
        assertEquals(newEndDate, result.getEndDate());
    }

    @Test
    void updateRental_ShouldThrowEntityNotFoundException_WhenRentalNotFound() {
        int nonExistentRentalId = 123;
        RentalCommand rentalCommand = RentalCommand.builder()
                .startDate(LocalDate.of(2023, 8, 1))
                .endDate(LocalDate.of(2023, 8, 10))
                .build();

        when(rentalRepository.findById(nonExistentRentalId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> rentalService.updateRental(nonExistentRentalId, rentalCommand));

        verify(rentalRepository, never()).save(any());
    }

    @Test
    void updateRental_ShouldThrowIllegalArgumentException_WhenEndDateBeforeStartDate() {
        int existingRentalId = 1;
        LocalDate startDate = LocalDate.of(2023, 8, 10);
        LocalDate endDate = LocalDate.of(2023, 8, 1);
        RentalCommand rentalCommand = RentalCommand.builder()
                .startDate(startDate)
                .endDate(endDate)
                .build();

        assertThrows(EntityNotFoundException.class, () -> rentalService.updateRental(existingRentalId, rentalCommand));
    }

    @Test
    void deleteRental_ShouldDeleteRental_WhenRentalExists() {
        int rentalIdToDelete = 1;

        doNothing().when(rentalRepository).deleteById(rentalIdToDelete);

        rentalService.deleteRental(rentalIdToDelete);

        verify(rentalRepository, times(1)).deleteById(rentalIdToDelete);
    }

    @Test
    void updateRental_ShouldThrowIllegalArgumentException_WhenStartDateInPast() {
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.of(2023, 8, 10);
        RentalCommand rentalCommand = RentalCommand.builder()
                .startDate(startDate)
                .endDate(endDate)
                .build();

        int existingRentalId = 1;

        when(rentalRepository.findById(existingRentalId)).thenReturn(Optional.of(createRental()));

        assertThrows(EntityExistsException  .class, () -> rentalService.updateRental(existingRentalId, rentalCommand));
    }

    private Rental createRental() {
        Book book = createBook();
        User user = createUser();
        return Rental.builder()
                .user(user)
                .book(book)
                .startDate(LocalDate.now())
                .endDate(LocalDate.from(LocalDate.of(2023, 12, 22)))
                .build();
    }

    private List<Rental> rentalsList() {
        return List.of(
                Rental.builder()
                        .user(createUser())
                        .book(createBook())
                        .startDate(LocalDate.now())
                        .endDate(LocalDate.from(LocalDate.of(2023, 10, 30)))
                        .build(),
                Rental.builder()
                        .user(createUser())
                        .book(createBook2())
                        .startDate(LocalDate.from(LocalDate.of(2023, 11, 30)))
                        .endDate(LocalDate.from(LocalDate.of(2023, 12, 30)))
                        .build()
        );
    }

    private User createUser() {
        return User.builder()
                .email("test@example.com")
                .password("password123")
                .role(Role.EMPLOYEE)
                .firstName("John")
                .lastName("Doe")
                .blocked(false)
                .build();
    }

    private Book createBook() {
        return Book.builder()
                .title("Sample Book")
                .category(createCategory())
                .availability(true)
                .build();
    }

    private Book createBook2() {
        return Book.builder()
                .title("Sample Book2")
                .category(createCategory())
                .availability(true)
                .build();
    }

    private Category createCategory() {
        return Category.builder()
                .id(1)
                .name("Romantic")
                .build();
    }
}