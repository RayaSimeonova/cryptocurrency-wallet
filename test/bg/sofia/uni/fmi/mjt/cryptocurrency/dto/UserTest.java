package bg.sofia.uni.fmi.mjt.cryptocurrency.dto;

import bg.sofia.uni.fmi.mjt.cryptocurrency.server.dto.User;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserTest {

    @Test
    void testUserCreationWithBlankUsername() {
        assertThrows(IllegalArgumentException.class, () -> new User("  ", "bella"),
            "Should not create user with blank string for username");
    }

    @Test
    void testUserCreationWithBlankPassword() {
        assertThrows(IllegalArgumentException.class, () -> new User("whale", "  "),
            "Should not create user with blank string for password");
    }

}
