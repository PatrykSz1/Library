package com.testlibrary.testlibrary.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testlibrary.testlibrary.common.Role;
import com.testlibrary.testlibrary.integration.ContainerTest;
import com.testlibrary.testlibrary.model.book.Book;
import com.testlibrary.testlibrary.model.category.Category;
import com.testlibrary.testlibrary.model.rental.Rental;
import com.testlibrary.testlibrary.model.rental.RentalDto;
import com.testlibrary.testlibrary.model.user.User;
import com.testlibrary.testlibrary.repository.BookRepository;
import com.testlibrary.testlibrary.repository.CategoryRepository;
import com.testlibrary.testlibrary.repository.RentalRepository;
import com.testlibrary.testlibrary.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.util.NestedServletException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class RentalControllerTest extends ContainerTest {

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void init() {
        Category category = Category.builder()
                .name("Romantic")
                .build();
        categoryRepository.save(category);

        Book book = Book.builder()
                .title("Sample Book")
                .category(category)
                .availability(true)
                .build();
        bookRepository.save(book);

        User user = User.builder()
                .email("test1@example.com")
                .password("password123")
                .role(Role.EMPLOYEE)
                .firstName("Johny")
                .lastName("Does")
                .blocked(false)
                .build();
        userRepository.saveAll(List.of(
                user,
                User.builder()
                        .email("test@example.com")
                        .password("password123")
                        .role(Role.EMPLOYEE)
                        .firstName("John")
                        .lastName("Doe")
                        .blocked(false)
                        .build())
        );

        rentalRepository.saveAll(List.of(
                Rental.builder()
                        .book(book)
                        .user(user)
                        .startDate(LocalDate.of(2023, 10, 20))
                        .endDate(LocalDate.of(2023, 10, 26))
                        .build())
        );
    }

    @Test
    @WithMockUser(authorities = "EMPLOYEE")
    void testGetAllRentals() throws Exception {
        mockMvc.perform(get("/api/v1/rentals?page=0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").exists());
    }

    @Test
    @WithMockUser(authorities = "EMPLOYEE")
    void testGetRentalById() throws Exception {
        mockMvc.perform(get("/api/v1/rentals/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)));
    }

    @Test
    @WithMockUser(authorities = "EMPLOYEE")
    void testCreateRental() throws Exception {
        LocalDate endDate = LocalDate.now().plusDays(7);
        mockMvc.perform(post("/api/v1/rentals")
                        .content(objectMapper.writeValueAsString(RentalDto.builder()
                                .userId(1)
                                .bookId(1)
                                .startDate(LocalDate.now())
                                .endDate(endDate)
                                .build()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.bookId").value(1));
    }

    @Test
    @WithMockUser(authorities = "EMPLOYEE")
    void testCreateRental_ShouldExpectBadRequest_WhenStartAndEndDateInPast() throws Exception {
        mockMvc.perform(post("/api/v1/rentals")
                        .content(objectMapper.writeValueAsString(RentalDto.builder()
                                .userId(1)
                                .bookId(1)
                                .startDate(LocalDate.of(2023, 8, 20))
                                .endDate(LocalDate.of(2023, 8, 26))
                                .build()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "EMPLOYEE")
    void testCreateRental_ShouldNotCreateRental_WhenUserNotFound() throws Exception {
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(5);
        RentalDto rentalDto = RentalDto.builder()
                .userId(10)
                .bookId(1)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        mockMvc.perform(post("/api/v1/rentals")
                        .content(objectMapper.writeValueAsString(rentalDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "EMPLOYEE")
    void testCreateRental_ShouldThrowEntityExistsException_WhenPeriodAlreadyBooked() throws Exception {
        RentalDto rentalDto = RentalDto.builder()
                .userId(1)
                .bookId(1)
                .startDate(LocalDate.of(2023, 10, 20))
                .endDate(LocalDate.of(2023, 10, 26))
                .build();

        mockMvc.perform(post("/api/v1/rentals")
                        .content(objectMapper.writeValueAsString(rentalDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "EMPLOYEE")
    void testCreateRental_ShouldThrowException_WhenStartDateInPast() throws Exception {
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(5);
        RentalDto rentalDto = RentalDto.builder()
                .userId(1)
                .bookId(1)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        mockMvc.perform(post("/api/v1/rentals")
                        .content(objectMapper.writeValueAsString(rentalDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "EMPLOYEE")
    void testCreateRental_ShouldThrowException_WhenEndDateBeforeStartDate() throws Exception {
        LocalDate startDate = LocalDate.now().plusDays(5);
        LocalDate endDate = LocalDate.now().plusDays(2);
        RentalDto rentalDto = RentalDto.builder()
                .userId(1)
                .bookId(1)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        mockMvc.perform(post("/api/v1/rentals")
                        .content(objectMapper.writeValueAsString(rentalDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "EMPLOYEE")
    void testConcurrentCreateRental() throws Exception {
        int numberOfThreads = 2;
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                latch.countDown();
                try {
                    mockMvc.perform(MockMvcRequestBuilders
                                    .post("/api/v1/rentals")
                                    .content("{ \"userId\": 1, \"bookId\": 1, \"startDate\": \"" +
                                            LocalDate.now() + "\", \"endDate\": \"" +
                                            LocalDateTime.of(2023, 9, 30, 22, 0) + "\" }")
                                    .contentType(MediaType.APPLICATION_JSON))
                            .andExpect(MockMvcResultMatchers.status().isCreated());
                } catch (NestedServletException e) {
                    Throwable rootCause = e.getRootCause();
                    assertThat(rootCause).isInstanceOf(EntityExistsException.class)
                            .hasMessage("The book is already booked during the specified time interval.");
                } catch (Exception e) {
                    throw new RuntimeException("Nieoczekiwany błąd", e);
                }
            });
        }
        latch.await(5, TimeUnit.SECONDS);
        assertThat(latch.getCount()).isZero();
        List<Rental> rentals = rentalRepository.findAll();
        assertThat(rentals).isNotEmpty();
    }

    @Test
    @WithMockUser(authorities = "EMPLOYEE")
    void testUpdateRental() throws Exception {
        Rental rental = rentalRepository.findAll().get(0);
        RentalDto rentalDto = RentalDto.builder()
                .userId(1)
                .bookId(1)
                .startDate(LocalDate.now())
                .endDate(LocalDate.from(LocalDateTime.of(2024, 12, 30, 22, 0)))
                .build();

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/rentals/{id}/update", rental.getId())
                        .content(objectMapper.writeValueAsString(rentalDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.bookId").value(1));
    }

    @Test
    @WithMockUser(authorities = "EMPLOYEE")
    void testDeleteRental() throws Exception {
        mockMvc.perform(delete("/api/v1/rentals/1", 1))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}
