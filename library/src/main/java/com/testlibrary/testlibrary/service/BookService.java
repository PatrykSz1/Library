package com.testlibrary.testlibrary.service;

import com.testlibrary.testlibrary.model.book.Book;
import com.testlibrary.testlibrary.model.book.BookCommand;
import com.testlibrary.testlibrary.repository.BookRepository;
import com.testlibrary.testlibrary.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final RabbitMQService rabbitMQService;

    public Page<Book> getAllBooks(Pageable pageable, String category) {
        Page<Book> allBooks = bookRepository.findAll(pageable);

        if (category != null && !category.isEmpty()) {
            List<Book> booksInCategory = new ArrayList<>();
            for (Book book : allBooks) {
                if (book.getCategory() != null && book.getCategory().getName().equals(category)) {
                    booksInCategory.add(book);
                }
            }
            if (booksInCategory.isEmpty()) {
                throw new EntityNotFoundException("No books found for category: " + category);
            }
            return new PageImpl<>(booksInCategory, pageable, booksInCategory.size());
        }

        return allBooks;
    }


    public Book getBookById(int id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with ID: " + id));
    }

    public Book createBook(BookCommand bookCommand) {
        Book book = new Book();
        book.setTitle(bookCommand.getTitle());
        book.setAvailability(bookCommand.isAvailability());

        int categoryId = bookCommand.getCategoryId();
        book.setCategory(categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with ID: " + categoryId)));

        book = bookRepository.save(book);
        rabbitMQService.sendMessage(book);
        return book;
    }

    @Transactional
    @Modifying
    public void toggleBookLock(int id, boolean availability) {
        bookRepository.updateByIdAndAvailability(id, availability);
    }

    @Transactional
    public void deleteBook(int id) {
        bookRepository.deleteById(id);
    }

    public Book getWithLockingByIdAndAvailabilityTrue(int id) {
        return bookRepository.findWithLockingByIdAndAvailabilityTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with ID: " + id));
    }
}
