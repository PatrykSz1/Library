package com.testlibrary.testlibrary.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testlibrary.testlibrary.integration.ContainerTest;
import com.testlibrary.testlibrary.model.category.Category;
import com.testlibrary.testlibrary.model.category.CategoryDto;
import com.testlibrary.testlibrary.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class CategoryControllerTest extends ContainerTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void init() {
        Category category = Category.builder()
                .id(1)
                .name("Fantasy")
                .build();
        categoryRepository.save(category);
    }

    @Test
    @WithMockUser(authorities = "EMPLOYEE")
    void testCreateCategory() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/categories")
                        .content(objectMapper.writeValueAsString(CategoryDto.builder()
                                .id(1)
                                .name("Test")
                                .build()))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test"));
    }

    @Test
    @WithMockUser(authorities = "EMPLOYEE")
    void testGetCategoryById() throws Exception {
        mockMvc.perform(get("/api/v1/categories/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)));
    }

    @Test
    @WithAnonymousUser
    void testGetAllCategories() throws Exception {
        mockMvc.perform(get("/api/v1/categories?page=0"))
                .andDo(print())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").exists())
                .andExpect(jsonPath("$.content[0].name").exists());
    }

    @Test
    @WithMockUser(authorities = "EMPLOYEE")
    void testDeleteCategoryWithNoBooks() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/categories/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}


