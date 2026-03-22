package com.daniil.bookstore.bookservice.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.daniil.bookstore.bookservice.dto.category.CategoryDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CategoryControllerTest {

    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext context) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    void setup(@Autowired DataSource dataSource) throws SQLException {
        executeSqlScript(dataSource,
                "com/daniil/bookstore/bookservice/database/categories/add-categories.sql");
        executeSqlScript(dataSource,
                "com/daniil/bookstore/bookservice/database/books/add-books.sql");
        executeSqlScript(dataSource,
                "com/daniil/bookstore/bookservice/database/books/add-book-category.sql");
    }

    @AfterEach
    void teardown(@Autowired DataSource dataSource) throws SQLException {
        executeSqlScript(dataSource,
                "com/daniil/bookstore/bookservice/database/books/remove-book-category.sql");
        executeSqlScript(dataSource,
                "com/daniil/bookstore/bookservice/database/books/remove-books.sql");
        executeSqlScript(dataSource,
                "com/daniil/bookstore/bookservice/database/categories/remove-categories.sql");
    }

    private void executeSqlScript(DataSource dataSource, String path) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(path));
        }
    }

    @Test
    @DisplayName("GET /categories returns all categories")
    @WithMockUser(username = "user")
    void getAll_CategoriesExist_ReturnsOk() throws Exception {
        mockMvc.perform(get("/categories").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    @DisplayName("GET /categories/{id} valid id returns category")
    @WithMockUser(username = "user")
    void getCategoryById_ValidId_ReturnsOk() throws Exception {
        mockMvc.perform(get("/categories/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Science Fiction"));
    }

    @Test
    @DisplayName("GET /categories/{id} invalid id returns 404")
    @WithMockUser(username = "user")
    void getCategoryById_InvalidId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/categories/999").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /categories with ADMIN role returns 201")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createCategory_ValidRequest_ReturnsCreated() throws Exception {
        CategoryDto request = new CategoryDto().setName("Horror").setDescription("Horror books");

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Horror"))
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    @DisplayName("POST /categories with USER role returns 403")
    @WithMockUser(username = "user", roles = {"USER"})
    void createCategory_UserRole_ReturnsForbidden() throws Exception {
        CategoryDto request = new CategoryDto().setName("Horror");

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PUT /categories/{id} with ADMIN role returns 200")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateCategory_ValidRequest_ReturnsOk() throws Exception {
        CategoryDto request = new CategoryDto()
                .setName("Updated Science Fiction")
                .setDescription("Updated description");

        mockMvc.perform(put("/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Science Fiction"));
    }

    @Test
    @DisplayName("DELETE /categories/{id} with ADMIN role returns 204")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteCategory_ValidId_ReturnsNoContent() throws Exception {
        executeSqlScript(
                mockMvc.getDispatcherServlet().getWebApplicationContext()
                        .getBean(DataSource.class),
                "com/daniil/bookstore/bookservice/database/books/remove-book-category.sql"
        );

        mockMvc.perform(delete("/categories/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /categories/{id}/books returns books for category")
    @WithMockUser(username = "user")
    void getBooksByCategoryId_ValidId_ReturnsBooks() throws Exception {
        mockMvc.perform(get("/categories/1/books").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Dune"));
    }
}
