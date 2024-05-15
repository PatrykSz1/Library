package com.testlibrary.testlibrary.controller;

import com.testlibrary.testlibrary.annotation.MonitorMethod;
import com.testlibrary.testlibrary.mapper.BookMapper;
import com.testlibrary.testlibrary.model.book.BookCommand;
import com.testlibrary.testlibrary.model.book.BookDto;
import com.testlibrary.testlibrary.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final BookMapper bookMapper;

    @GetMapping
    @MonitorMethod
    public ResponseEntity<Page<BookDto>> getAllBooks(@PageableDefault Pageable pageable, @RequestParam(required = false) String category) {
        Page<BookDto> bookDtoPage = bookService.getAllBooks(pageable, category).map(bookMapper::toDto);
        return new ResponseEntity<>(bookDtoPage, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @MonitorMethod
    public ResponseEntity<BookDto> getBookById(@PathVariable int id) {
        BookDto bookDto = bookMapper.toDto(bookService.getBookById(id));
        return new ResponseEntity<>(bookDto, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    @MonitorMethod
    public ResponseEntity<BookDto> createBook(@RequestBody @Valid BookCommand bookCommand) {
        BookDto createdBookDto = bookMapper.toDto(bookService.createBook(bookCommand));
        return new ResponseEntity<>(createdBookDto, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", params = "availability")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity<Void> toggleBookLock(@PathVariable int id, @RequestParam boolean availability) {
        bookService.toggleBookLock(id, availability);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity<Void> deleteBook(@PathVariable int id) {
        bookService.deleteBook(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

