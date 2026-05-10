package com.cts.demo.service.impl;

import com.cts.demo.dao.UserDao;
import com.cts.demo.model.User;
import com.cts.demo.service.UserService;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Generates SHA-256 hash for the given password.
     *
     * @param password the plain text password to be hashed
     * @return the SHA-256 hashed password in hexadecimal format
     * @throws Exception if the hashing algorithm is not available
     */
    private String hashPassword(String password) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = messageDigest.digest(password.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashedBytes);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("System failure: Password hashing failed");
        }
    }

    /**
     * Registers a new user after validating email and encoding password.
     *
     * @param user
     */
    @Override
    public void registerUser(User user) {
        if (userDao.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("User with this email already exists");
        }
        String hashedPassword = hashPassword(user.getPassword());
        user.setPassword(hashedPassword);
        userDao.save(user);
    }

    /**
     * Retrieves all registered users.
     *
     * @return a list of all users
     */
    public List<User> getAllUser() {
        return userDao.findAllUser();
    }

    /**
     * Retrieves a user by their email.
     *
     * @param email
     * @return the matching user
     */
    @Override
    public User getUserByEmail(String email) {
        User user = userDao.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found with email: " + email);
        }
        return user;
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param userId
     * @return the matching user, or null if not found
     */
    @Override
    public User getUserById(int userId) {
        return userDao.findById(userId);
    }

    /**
     * Updates the name of a user.
     *
     * @param name
     * @param email
     * @param password
     * @return number of rows affected
     */
    @Override
    public int updateUserName(String name, String email, String password) {
        String hashedPassword = hashPassword(password);
        return userDao.updateUserName(name, email, hashedPassword);
    }

    /**
     * Validates user credentials and returns login result.
     *
     * @param email
     * @param password
     * @param role
     * @return true if login is successful
     */
    @Override
    public boolean login(String email, String password, String role) {
        User user = userDao.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User with this email does not exist");
        }
        String givenPassword = hashPassword(password);
        if (!givenPassword.equals(user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        return true;
    }
}