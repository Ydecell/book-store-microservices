package com.daniil.bookstore.bookservice.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.daniil.bookstore.bookservice.dto.book.CreateBookRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
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
class BookControllerTest {

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
                "com/daniil/bookstore/bookservice/database/books/add-books.sql");
    }

    @AfterEach
    void teardown(@Autowired DataSource dataSource) throws SQLException {
        executeSqlScript(dataSource,
                "com/daniil/bookstore/bookservice/database/books/remove-book-category.sql");
        executeSqlScript(dataSource,
                "com/daniil/bookstore/bookservice/database/books/remove-books.sql");
    }

    private void executeSqlScript(DataSource dataSource, String path) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(path));
        }
    }

    @Test
    @DisplayName("GET /books returns 200 and list of books")
    @WithMockUser(username = "user")
    void getAll_BooksExist_ReturnsOk() throws Exception {
        mockMvc.perform(get("/books").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET /books/{id} valid id returns 200")
    @WithMockUser(username = "user")
    void getBookById_ValidId_ReturnsOk() throws Exception {
        mockMvc.perform(get("/books/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Dune"))
                .andExpect(jsonPath("$.author").value("Frank Herbert"));
    }

    @Test
    @DisplayName("GET /books/{id} invalid id returns 404")
    @WithMockUser(username = "user")
    void getBookById_InvalidId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/books/999").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /books with ADMIN role returns 201")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createBook_ValidRequest_ReturnsCreated() throws Exception {
        CreateBookRequestDto request = new CreateBookRequestDto()
                .setTitle("Children of Dune")
                .setAuthor("Frank Herbert")
                .setIsbn("3333333333333")
                .setPrice(new BigDecimal("11.99"))
                .setCoverImage("children-of-dune.jpg");

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Children of Dune"))
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    @DisplayName("POST /books with USER role returns 403")
    @WithMockUser(username = "user", roles = {"USER"})
    void createBook_UserRole_ReturnsForbidden() throws Exception {
        CreateBookRequestDto request = new CreateBookRequestDto()
                .setTitle("Some Book")
                .setAuthor("Some Author")
                .setIsbn("4444444444444")
                .setPrice(new BigDecimal("10.00"))
                .setCoverImage("cover.jpg");

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /books with missing title returns 400")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createBook_MissingTitle_ReturnsBadRequest() throws Exception {
        CreateBookRequestDto request = new CreateBookRequestDto()
                .setAuthor("Author")
                .setIsbn("5555555555555")
                .setPrice(new BigDecimal("10.00"))
                .setCoverImage("cover.jpg");

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /books/{id} with ADMIN role returns 200")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateBook_ValidRequest_ReturnsOk() throws Exception {
        CreateBookRequestDto request = new CreateBookRequestDto()
                .setTitle("Dune Updated")
                .setAuthor("Frank Herbert")
                .setIsbn("1111111111111")
                .setPrice(new BigDecimal("12.99"))
                .setCoverImage("dune-updated.jpg");

        mockMvc.perform(put("/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Dune Updated"));
    }

    @Test
    @DisplayName("DELETE /books/{id} with ADMIN role returns 204")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteBook_ValidId_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/books/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /books/search by title returns matching books")
    @WithMockUser(username = "user")
    void searchBooks_ByTitle_ReturnsMatchingBooks() throws Exception {
        mockMvc.perform(get("/books/search")
                        .param("titles", "Dune")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Dune"));
    }
}
