package com.testlibrary.testlibrary.service;

import com.testlibrary.testlibrary.common.Role;
import com.testlibrary.testlibrary.mapper.BookMapper;
import com.testlibrary.testlibrary.model.book.Book;
import com.testlibrary.testlibrary.model.book.BookCommand;
import com.testlibrary.testlibrary.model.category.Category;
import com.testlibrary.testlibrary.model.rental.Rental;
import com.testlibrary.testlibrary.model.user.User;
import com.testlibrary.testlibrary.repository.BookRepository;
import com.testlibrary.testlibrary.repository.CategoryRepository;
import com.testlibrary.testlibrary.repository.RentalRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookMapper bookMapper;

    @Mock
    BookRepository bookRepository;

    @Mock
    RentalRepository rentalRepository;

    @Mock
    CategoryRepository categoryRepository;

    @Mock
    RabbitMQService rabbitMQService;

    @InjectMocks
    BookService bookService;

    @Test
    void testGetAllBooks_ShouldGetListOfBooks() {
        List<Book> mockBooks = createListOfBooks();
        Page<Book> page = new PageImpl<>(mockBooks);

        when(bookRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Book> result = bookService.getAllBooks(Pageable.unpaged(), null);

        assertEquals(mockBooks.size(), result.getTotalElements());

        verify(bookRepository, times(1)).findAll(any(Pageable.class));
    }


    @Test
    void testGetBookById_ShouldGetBook() {
        Book book = createBook();

        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));

        Book result = bookService.getBookById(book.getId());

        assertEquals(book.getTitle(), result.getTitle());
        assertEquals(book.getCategory(), result.getCategory());

        verify(bookRepository, times(1)).findById(book.getId());
    }

    @Test
    void testGetBookById_ShouldThrowEntityNotFoundException() {
        int bookId = 1;
        String exceptionMessage = "Book not found with ID: " + bookId;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());


        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookService.getBookById(bookId)
        );

        assertEquals(exceptionMessage, exception.getMessage());

        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    void testCreateBook_ShouldCreateBook() {
        BookCommand bookCommand = BookCommand.builder()
                .title("Test Book")
                .categoryId(createCategory().getId())
                .availability(true)
                .build();

        when(categoryRepository.findById(createCategory().getId())).thenReturn(Optional.ofNullable(createCategory()));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> {
            Book bookToSave = invocation.getArgument(0);
            bookToSave.setId(1);
            return bookToSave;
        });

        Book result = bookService.createBook(bookCommand);

        assertEquals(bookCommand.getTitle(), result.getTitle());
        assertEquals(createCategory().getId(), result.getCategory().getId());
        assertTrue(result.isAvailability());

        verify(bookRepository, times(1)).save(any(Book.class));
        verify(rabbitMQService, times(1)).sendMessage(any(Book.class));
    }

    @Test
    public void testCreateBookNegative() {
        BookCommand bookCommand = BookCommand.builder()
                .title("Test Book")
                .categoryId(createCategory().getId())
                .availability(true)
                .build();

        when(bookRepository.save(any(Book.class))).thenThrow(new RuntimeException("Failed to save book"));
        when(categoryRepository.findById(createCategory().getId())).thenReturn(Optional.ofNullable(createCategory()));

        assertThrows(RuntimeException.class, () -> bookService.createBook(bookCommand));

        verify(bookRepository, times(1)).save(any(Book.class));

        verifyNoInteractions(rabbitMQService);

        verifyNoInteractions(bookMapper);
    }

    @Test
    @Transactional
    public void testToggleBookLock_ShouldChangeAvailabilityToFalse() {
        Book book = createBook();
        bookService.toggleBookLock(book.getId(), true);

        verify(bookRepository, times(1)).updateByIdAndAvailability(1, true);
    }

    @Test
    public void testDeleteBookWithNoRentals_ShouldDeleteBook() {
        Book book = createBook();
        book.setId(1);
        book.setAvailability(true);

        bookService.deleteBook(1);

        verify(bookRepository, times(1)).deleteById(book.getId());
    }

    @Test
    public void testGetWithLockingByIdExistingBook() {
        Book testBook = createBook();
        when(bookRepository.findWithLockingByIdAndAvailabilityTrue(testBook.getId())).thenReturn(Optional.of(testBook));


        Book foundBook = bookService.getWithLockingByIdAndAvailabilityTrue(testBook.getId());

        assertNotNull(foundBook);
        assertEquals(testBook, foundBook);
        verify(bookRepository, times(1)).findWithLockingByIdAndAvailabilityTrue(testBook.getId());
    }

    private Category createCategory() {
        return Category.builder()
                .id(1)
                .name("Romantic")
                .build();
    }

    private Book createBook() {
        return Book.builder()
                .id(1)
                .title("Sample Book")
                .category(createCategory())
                .availability(true)
                .rentals((new HashSet<>()))
                .build();
    }

    private List<Book> createListOfBooks() {
        return List.of(
                Book.builder()
                        .id(1)
                        .title("Sample Book")
                        .category(createCategory())
                        .availability(true)
                        .build(),
                Book.builder()
                        .id(2)
                        .title("Sample Book2")
                        .category(createCategory())
                        .availability(true)
                        .build()
        );
    }

    private Rental createRental() {
        Book book = createBook();
        User user = createUser();
        return Rental.builder()
                .id(1)
                .user(user)
                .book(book)
                .startDate(LocalDate.now())
                .endDate(LocalDate.from(LocalDate.of(2023, 12, 22)))
                .build();
    }

    private User createUser() {
        return User.builder()
                .id(1)
                .email("test@example.com")
                .password("password123")
                .role(Role.EMPLOYEE)
                .firstName("John")
                .lastName("Doe")
                .blocked(false)
                .build();
    }
}