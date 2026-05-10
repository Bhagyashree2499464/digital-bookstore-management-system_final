package com.cts.demo.dao.impl;

import com.cts.demo.dao.UserDao;
import com.cts.demo.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;


import java.util.List;

/**
 * JDBC-based implementation of {@link UserDao}.
 */
@Repository
public class UserDaoImpl implements UserDao {

    private static final String INSERT_SQL = "INSERT INTO User (Name, Email, Password, Role) VALUES (?, ?, ?, ?)";
    private static final String LOGIN_SQL = "SELECT EXISTS( SELECT 1 FROM USER WHERE EMAIL=? AND PASSWORD=? AND ROLE=?)";
    private static final String FIND_BY_EMAIL_SQL = "SELECT * FROM User WHERE Email = ?";
    private static final String FIND_BY_NAME_SQL = "SELECT * FROM User WHERE NAME = ?";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM User WHERE UserID = ?";
    private static final String FIND_ALL_SQL = "SELECT * FROM USER";
    private static final String UPDATE_NAME_SQL = """
            UPDATE USER SET NAME=?
            WHERE EMAIL=? AND PASSWORD=?
            """;

    private final JdbcTemplate jdbcTemplate;

    public UserDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<User> rowMapper = (rs, i) -> new User(
            rs.getInt("UserID"),
            rs.getString("Name"),
            rs.getString("Email"),
            rs.getString("Password"),
            rs.getString("Role"));

    /**
     * Saves a new user to the database.
     *
     * @param user
     */
    @Override
    public void save(User user) {
        jdbcTemplate.update(INSERT_SQL,
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                user.getRole());
    }

    /**
     * Checks whether a user exists with the given email, password, and role.
     *
     * @param email
     * @param password
     * @param role
     * @return true if the credentials match, false otherwise
     */
    @Override
    public boolean login(String email, String password, String role) {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(LOGIN_SQL,
                Boolean.class,
                email,
                password,
                role));
    }

    /**
     * Finds a user by their email address.
     *
     * @param email
     * @return the matching user, or null if not found
     */
    @Override
    public User findByEmail(String email) {
        return jdbcTemplate.query(FIND_BY_EMAIL_SQL, rowMapper, email)
                .stream()
                .findFirst()
                .orElse(null);
    }

    /**
     * Finds a user by their name.
     *
     * @param name
     * @return the matching user, or null if not found
     */
    @Override
    public User findByName(String name) {
        return jdbcTemplate.query(FIND_BY_NAME_SQL, rowMapper, name)
                .stream()
                .findFirst()
                .orElse(null);
    }

    /**
     * Finds a user by their ID.
     *
     * @param userId
     * @return the matching user, or null if not found
     */
    @Override
    public User findById(int userId) {
        return jdbcTemplate.query(FIND_BY_ID_SQL, rowMapper, userId)
                .stream()
                .findFirst()
                .orElse(null);
    }

    /**
     * Retrieves all users from the database.
     *
     * @return a list of all users
     */
    @Override
    public List<User> findAllUser() {
        return jdbcTemplate.query(FIND_ALL_SQL, rowMapper);
    }

    /**
     * Updates the name of a user identified by their email and password.
     *
     * @param name
     * @param email
     * @param password
     * @return number of rows affected
     */
    @Override
    public int updateUserName(String name, String email, String password) {
        return jdbcTemplate.update(UPDATE_NAME_SQL, name, email, password);
    }
}