package com.daniil.bookstore.userservice.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.daniil.bookstore.userservice.client.CartClient;
import com.daniil.bookstore.userservice.dto.UserLoginRequestDto;
import com.daniil.bookstore.userservice.dto.UserRegistrationRequestDto;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthenticationControllerTest {

    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CartClient cartClient;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext context) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @AfterEach
    void teardown(@Autowired DataSource dataSource) throws SQLException {
        executeSqlScript(dataSource,
                "com/daniil/bookstore/userservice/database/users/remove-users-roles.sql");
        executeSqlScript(dataSource,
                "com/daniil/bookstore/userservice/database/users/remove-users.sql");
    }

    private void executeSqlScript(DataSource dataSource, String path) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(path));
        }
    }

    @Test
    @DisplayName("POST /auth/registration with valid data returns 201 and user body")
    void register_ValidRequest_ReturnsCreated() throws Exception {
        UserRegistrationRequestDto request = validRegistrationRequest();

        mockMvc.perform(post("/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    @DisplayName("POST /auth/registration with invalid email returns 400")
    void register_InvalidEmail_ReturnsBadRequest() throws Exception {
        UserRegistrationRequestDto request = validRegistrationRequest();
        request.setEmail("not-an-email");

        mockMvc.perform(post("/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /auth/registration with mismatched passwords returns 400")
    void register_PasswordMismatch_ReturnsBadRequest() throws Exception {
        UserRegistrationRequestDto request = validRegistrationRequest();
        request.setRepeatPassword("differentPass");

        mockMvc.perform(post("/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /auth/registration with short password returns 400")
    void register_PasswordTooShort_ReturnsBadRequest() throws Exception {
        UserRegistrationRequestDto request = validRegistrationRequest();
        request.setPassword("short");
        request.setRepeatPassword("short");

        mockMvc.perform(post("/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /auth/registration with duplicate email returns 409")
    void register_DuplicateEmail_ReturnsConflict() throws Exception {
        UserRegistrationRequestDto request = validRegistrationRequest();
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST /auth/login with valid credentials returns JWT token")
    void login_ValidCredentials_ReturnsToken() throws Exception {
        UserRegistrationRequestDto regRequest = validRegistrationRequest();
        mockMvc.perform(post("/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isCreated());

        UserLoginRequestDto loginRequest = new UserLoginRequestDto(
                "john@example.com", "password1"
        );

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    @DisplayName("POST /auth/login with wrong password returns 401")
    void login_WrongPassword_ReturnsUnauthorized() throws Exception {
        UserRegistrationRequestDto regRequest = validRegistrationRequest();
        mockMvc.perform(post("/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isCreated());

        UserLoginRequestDto loginRequest = new UserLoginRequestDto(
                "john@example.com", "wrongPassword"
        );

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /auth/login with invalid email format returns 400")
    void login_InvalidEmailFormat_ReturnsBadRequest() throws Exception {
        UserLoginRequestDto loginRequest = new UserLoginRequestDto("not-an-email", "password1");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    private UserRegistrationRequestDto validRegistrationRequest() {
        UserRegistrationRequestDto dto = new UserRegistrationRequestDto();
        dto.setEmail("john@example.com");
        dto.setPassword("password1");
        dto.setRepeatPassword("password1");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        return dto;
    }
}
