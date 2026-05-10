package com.cts.demo.usertests;

import com.cts.demo.dao.UserDao;
import com.cts.demo.model.User;
import com.cts.demo.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link UserServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class UserTest {

    @Mock
    private UserDao userDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    /**
     * Sets up a sample user before each test.
     */
    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword("password123");
        user.setRole("USER");
    }

    /**
     * Tests that registerUser saves user when user is valid.
     */
    @Test
    void registerUser_shouldSaveUser_whenUserIsValid() {

        when(userDao.findByEmail("john@example.com")).thenReturn(null);
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");

        userService.registerUser(user);

        assertEquals("hashedPassword", user.getPassword());
        verify(userDao).save(user);
    }

    /**
     * Tests that registerUser throws RuntimeException when email is invalid.
     */
    @Test
    void registerUser_shouldThrowException_whenEmailInvalid() {

        user.setEmail("invalidEmail");
        when(userDao.findByEmail("invalidEmail")).thenReturn(null);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> userService.registerUser(user)
        );

        assertEquals("Invalid Email", ex.getMessage());
    }

    /**
     * Tests that registerUser throws RuntimeException when email already exists.
     */
    @Test
    void registerUser_shouldThrowException_whenEmailAlreadyExists() {
        when(userDao.findByEmail("john@example.com")).thenReturn(user);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> userService.registerUser(user)
        );

        assertEquals("User with this email already exists", ex.getMessage());
    }

    /**
     * Tests that getAllUser returns list of all users.
     */
    @Test
    void getAllUser_shouldReturnUserList() {
        when(userDao.findAllUser()).thenReturn(List.of(user));

        List<User> result = userService.getAllUser();

        assertEquals(1, result.size());
    }

    /**
     * Tests that getUserByEmail returns user when email exists.
     */
    @Test
    void getUserByEmail_shouldReturnUser_whenExists() {
        when(userDao.findByEmail("john@example.com")).thenReturn(user);

        User result = userService.getUserByEmail("john@example.com");

        assertNotNull(result);
        assertEquals("john@example.com", result.getEmail());
    }

    /**
     * Tests that getUserByEmail throws RuntimeException when email not found.
     */
    @Test
    void getUserByEmail_shouldThrowException_whenNotFound() {
        when(userDao.findByEmail("unknown@example.com")).thenReturn(null);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> userService.getUserByEmail("unknown@example.com")
        );

        assertTrue(ex.getMessage().contains("User not found"));
    }

    /**
     * Tests that getUserById returns user when ID exists.
     */
    @Test
    void getUserById_shouldReturnUser() {
        when(userDao.findById(1)).thenReturn(user);

        User result = userService.getUserById(1);

        assertEquals(1, result.getUserId());
    }

    /**
     * Tests that login returns true when credentials are valid.
     */
    @Test
    void login_shouldReturnTrue_whenCredentialsValid() {

        user.setPassword("hashedPassword");

        when(userDao.findByEmail("john@example.com")).thenReturn(user);
        when(passwordEncoder.matches("password123", "hashedPassword"))
                .thenReturn(true);

        boolean result =
                userService.login("john@example.com", "password123", "USER");

        assertTrue(result);
    }

    /**
     * Tests that login throws RuntimeException when user not found.
     */
    @Test
    void login_shouldThrowException_whenUserNotFound() {
        when(userDao.findByEmail("unknown@example.com")).thenReturn(null);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> userService.login("unknown@example.com", "pass", "USER")
        );

        assertEquals("User with this email does not exist", ex.getMessage());
    }

    /**
     * Tests that login throws RuntimeException when email is invalid.
     */
    @Test
    void login_shouldThrowException_whenEmailInvalid() {

        when(userDao.findByEmail("invalidEmail")).thenReturn(null);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> userService.login("invalidEmail", "pass", "USER")
        );

        assertEquals(
                "User with this email does not exist",
                ex.getMessage()
        );
    }

    /**
     * Tests that updateUserName returns number of rows updated.
     */
    @Test
    void updateUserName_shouldReturnUpdatedRows() {
        when(userDao.updateUserName("NewName", "john@example.com", "password123"))
                .thenReturn(1);

        int result =
                userService.updateUserName("NewName", "john@example.com", "password123");

        assertEquals(1, result);
    }
}