package com.daniil.bookstore.userservice.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.daniil.bookstore.userservice.model.User;
import com.daniil.bookstore.userservice.repository.user.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("existsByEmail returns true when user exists")
    @Sql(scripts = "classpath:com/daniil/bookstore/userservice/database/users/add-users.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:com/daniil/bookstore/userservice/database/users/remove-users.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void existsByEmail_ExistingEmail_ReturnsTrue() {
        assertTrue(userRepository.existsByEmail("john@example.com"));
    }

    @Test
    @DisplayName("existsByEmail returns false when user does not exist")
    void existsByEmail_NonExistingEmail_ReturnsFalse() {
        assertFalse(userRepository.existsByEmail("notexist@example.com"));
    }

    @Test
    @DisplayName("findByEmail returns user when email exists")
    @Sql(scripts = "classpath:com/daniil/bookstore/userservice/database/users/add-users.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:com/daniil/bookstore/userservice/database/users/remove-users.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByEmail_ExistingEmail_ReturnsUser() {
        Optional<User> result = userRepository.findByEmail("john@example.com");

        assertTrue(result.isPresent());
        assertEquals("john@example.com", result.get().getEmail());
        assertEquals("John", result.get().getFirstName());
        assertEquals("Doe", result.get().getLastName());
    }

    @Test
    @DisplayName("findByEmail returns empty when email does not exist")
    void findByEmail_NonExistingEmail_ReturnsEmpty() {
        Optional<User> result = userRepository.findByEmail("notexist@example.com");

        assertFalse(result.isPresent());
    }
}
