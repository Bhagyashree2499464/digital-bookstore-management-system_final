package com.cts.demo.dao.impl;

import com.cts.demo.dao.AuthorDao;
import com.cts.demo.model.Author;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JDBC-based implementation of {@link AuthorDao}.
 */
@Repository
public class AuthorDaoImpl implements AuthorDao {

    private static final String INSERT_SQL = "INSERT INTO AUTHOR(AuthorName) VALUES(?)";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM AUTHOR WHERE AUTHORID=?";
    private static final String FIND_BY_NAME_SQL = "SELECT * FROM AUTHOR WHERE AUTHORNAME=?";
    private static final String FIND_ALL_SQL = "SELECT * FROM AUTHOR";

    private final JdbcTemplate jdbcTemplate;

    public AuthorDaoImpl(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Author> rowMapper = (rs, rowNum) ->
            new Author(
                    rs.getInt("AuthorId"),
                    rs.getString("AuthorName")
            );

    /**
     * Adds a new author to the database.
     *
     * @param authorName
     * @return number of rows affected
     */
    @Override
    public int addAuthor(String authorName){
        return jdbcTemplate.update(
                INSERT_SQL,
                authorName
        );
    }

    /**
     * Finds an author by their ID.
     *
     * @param authorId
     * @return an Optional containing the author, or empty if not found
     */
    @Override
    public Optional<Author> findByAuthorId(int authorId) {
        List<Author> author = jdbcTemplate.query(
                FIND_BY_ID_SQL,
                rowMapper,
                authorId
        );
        return author.stream().findFirst();
    }

    /**
     * Finds an author by their name.
     *
     * @param name
     * @return an Optional containing the author, or empty if not found
     */
    @Override
    public Optional<Author> findByAuthorName(String name) {
        List<Author> author = jdbcTemplate.query(
                FIND_BY_NAME_SQL,
                rowMapper,
                name
        );
        return author.stream().findFirst();
    }

    /**
     * Retrieves all authors from the database.
     *
     * @return a list of all authors
     */
    @Override
    public List<Author> findAll() {
        return jdbcTemplate.query(
                FIND_ALL_SQL,
                rowMapper
        );
    }
}