package com.testlibrary.testlibrary.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testlibrary.testlibrary.integration.ContainerTest;
import com.testlibrary.testlibrary.model.book.Book;
import com.testlibrary.testlibrary.model.book.BookDto;
import com.testlibrary.testlibrary.model.category.Category;
import com.testlibrary.testlibrary.repository.BookRepository;
import com.testlibrary.testlibrary.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class BookControllerTest extends ContainerTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;


    @BeforeEach
    void init() {
        Category category = Category.builder()
                .id(1)
                .name("Fantasy")
                .build();
        categoryRepository.save(category);

        bookRepository.saveAll(List.of(
                Book.builder()
                        .id(1)
                        .title("test")
                        .category(category)
                        .availability(true)
                        .build(),
                Book.builder()
                        .id(2)
                        .title("Test2")
                        .category(category)
                        .availability(true)
                        .build(),
                Book.builder()
                        .id(3)
                        .title("Test3")
                        .category(category)
                        .availability(false)
                        .build()
        ));
    }

    @Test
    @WithAnonymousUser
    void testGetAllBooks() throws Exception {
        mockMvc.perform(get("/api/v1/books?page=0"))
                .andDo(print())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").exists())
                .andExpect(jsonPath("$.content[0].title").exists());
    }

    @Test
    @WithMockUser(authorities = "CUSTOMER")
    void testGetBookById() throws Exception {
        mockMvc.perform(get("/api/v1/books/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)));
    }

    @Test
    @WithMockUser(authorities = "CUSTOMER")
    void testGetBooksByCategory() throws Exception {
        mockMvc.perform(get("/api/v1/books?category=Fantasy"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = "EMPLOYEE")
    void testCreateBook() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/books")
                        .content(objectMapper.writeValueAsString(BookDto.builder()
                                .title("test")
                                .availability(true)
                                .categoryId(1)
                                .build()))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("test")) ;

        Book savedBook = bookRepository.findWithLockingByIdAndAvailabilityTrue(1)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        assertNotNull(savedBook);
        assertEquals("test", savedBook.getTitle());
        assertTrue(savedBook.isAvailability());
        assertEquals(1, savedBook.getCategory().getId());
    }

    @Test
    @WithMockUser(authorities = "EMPLOYEE")
    void testToggleBookLock() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/books/3?availability=true")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "EMPLOYEE")
    void testDeleteBook() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/books/2", 2)
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}