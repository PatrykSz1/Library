package com.testlibrary.testlibrary.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.testlibrary.testlibrary.common.Role;
import com.testlibrary.testlibrary.integration.ContainerTest;
import com.testlibrary.testlibrary.model.category.Category;
import com.testlibrary.testlibrary.model.user.User;
import com.testlibrary.testlibrary.model.user.UserDto;
import com.testlibrary.testlibrary.repository.CategoryRepository;
import com.testlibrary.testlibrary.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class UserControllerTest extends ContainerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void init() {
        Category category = Category.builder()
                .name("Fantasy")
                .build();

        categoryRepository.saveAll(List.of(category));
        User user = User.builder()
                .email("test@example.com")
                .password("testPassword")
                .firstName("John")
                .lastName("Doe")
                .role(Role.EMPLOYEE)
                .blocked(false)
                .build();

        Set<Category> subscriptions = new HashSet<>();
        subscriptions.add(category);

        user.setSubscriptions(subscriptions);

        userRepository.save(user);
    }

    @Test
    @WithMockUser(authorities = "EMPLOYEE")
    void testGetUsers() throws Exception {
        mockMvc.perform(get("/api/v1/users?page=0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").exists())
                .andExpect(jsonPath("$.content[0].email").exists());
    }

    @Test
    @WithMockUser(authorities = "EMPLOYEE")
    @Transactional
    void testGetUserById() throws Exception {
        mockMvc.perform(get("/api/v1/users/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)));
    }

    @Test
    @WithMockUser(username = "test@example.com", authorities = "EMPLOYEE")
    void testCreateUser() throws Exception {
        mockMvc.perform(post("/api/v1/users")
                        .content(objectMapper.writeValueAsString(UserDto.builder()
                                .email("patryk@email.com")
                                .password("testPassword")
                                .firstName("John")
                                .lastName("Doe")
                                .role(Role.EMPLOYEE)
                                .build()))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("patryk@email.com"));
    }


    @Test
    @WithMockUser(authorities = "EMPLOYEE")
    void testUnlockUser() throws Exception {
        mockMvc.perform(patch("/api/v1/users/1/unlock"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.blocked").value(false));
    }

    @Test
    @WithMockUser(authorities = "EMPLOYEE")
    void testBlockUser() throws Exception {
        mockMvc.perform(patch("/api/v1/users/1/block"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.blocked").value(true));
    }

    @Test
    @WithMockUser(authorities = "EMPLOYEE")
    void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/v1/users/1"))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertFalse(userRepository.existsById(1));
    }

    @Test
    @WithMockUser(username = "test@example.com", authorities = "EMPLOYEE")
    void testAddCategoryToUserSubscription() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/api/v1/users/1/categories/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

    }
}
