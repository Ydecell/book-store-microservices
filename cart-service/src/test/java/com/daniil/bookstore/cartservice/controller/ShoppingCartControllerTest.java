package com.daniil.bookstore.cartservice.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.daniil.bookstore.cartservice.client.BookClient;
import com.daniil.bookstore.cartservice.dto.book.BookDto;
import com.daniil.bookstore.cartservice.dto.cartitem.CreateCartItemRequestDto;
import com.daniil.bookstore.cartservice.dto.cartitem.UpdateCartItemRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShoppingCartControllerTest {

    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookClient bookClient;

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
                "com/daniil/bookstore/cartservice/database/carts/add-shopping-carts.sql");
        executeSqlScript(dataSource,
                "com/daniil/bookstore/cartservice/database/items/add-cart-items.sql");
    }

    @AfterEach
    void teardown(@Autowired DataSource dataSource) throws SQLException {
        executeSqlScript(dataSource,
                "com/daniil/bookstore/cartservice/database/items/remove-cart-items.sql");
        executeSqlScript(dataSource,
                "com/daniil/bookstore/cartservice/database/carts/remove-shopping-carts.sql");
    }

    private void executeSqlScript(DataSource dataSource, String path) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(path));
        }
    }

    // Контроллер делает (Long) auth.getPrincipal() — нужен Long principal
    private RequestPostProcessor userWithId(Long userId) {
        return authentication(new UsernamePasswordAuthenticationToken(
                userId, null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))));
    }

    @Test
    @DisplayName("GET /cart returns 200 and shopping cart for current user")
    void getShoppingCart_ValidUser_ReturnsOk() throws Exception {
        mockMvc.perform(get("/cart")
                        .with(userWithId(100L))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(100));
    }

    @Test
    @DisplayName("GET /cart returns 404 when cart not found")
    void getShoppingCart_CartNotFound_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/cart")
                        .with(userWithId(999L))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /cart returns 403 without authentication")
    void getShoppingCart_Unauthenticated_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/cart")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /cart adds item and returns 201")
    void addItemToCart_ValidRequest_ReturnsCreated() throws Exception {
        BookDto bookDto = new BookDto();
        bookDto.setId(20L);
        bookDto.setTitle("The Hobbit");
        bookDto.setPrice(new BigDecimal("8.99"));

        when(bookClient.getBookById(20L)).thenReturn(bookDto);

        CreateCartItemRequestDto request = new CreateCartItemRequestDto();
        request.setBookId(20L);
        request.setQuantity(1);

        mockMvc.perform(post("/cart")
                        .with(userWithId(100L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(100));
    }

    @Test
    @DisplayName("POST /cart with invalid bookId returns 400")
    void addItemToCart_InvalidBookId_ReturnsBadRequest() throws Exception {
        CreateCartItemRequestDto request = new CreateCartItemRequestDto();
        request.setBookId(null);
        request.setQuantity(1);

        mockMvc.perform(post("/cart")
                        .with(userWithId(100L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /cart/items/{id} updates quantity and returns 200")
    void updateCartItem_ValidRequest_ReturnsOk() throws Exception {
        UpdateCartItemRequestDto request = new UpdateCartItemRequestDto();
        request.setQuantity(5);

        mockMvc.perform(put("/cart/items/1")
                        .with(userWithId(100L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(100));
    }

    @Test
    @DisplayName("PUT /cart/items/{id} item not found returns 404")
    void updateCartItem_ItemNotFound_ReturnsNotFound() throws Exception {
        UpdateCartItemRequestDto request = new UpdateCartItemRequestDto();
        request.setQuantity(5);

        mockMvc.perform(put("/cart/items/999")
                        .with(userWithId(100L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /cart/items/{id} removes item and returns 204")
    void removeCartItem_ValidId_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/cart/items/1")
                        .with(userWithId(100L))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /cart/items/{id} item not found returns 404")
    void removeCartItem_ItemNotFound_ReturnsNotFound() throws Exception {
        mockMvc.perform(delete("/cart/items/999")
                        .with(userWithId(100L))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
