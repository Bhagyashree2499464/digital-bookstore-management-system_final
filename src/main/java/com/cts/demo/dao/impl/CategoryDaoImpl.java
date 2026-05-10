
package com.cts.demo.dao.impl;

import com.cts.demo.dao.CategoryDao;
import com.cts.demo.model.Category;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JDBC-based implementation of {@link CategoryDao}.
 */
@Repository
public class CategoryDaoImpl implements CategoryDao {

    private static final String INSERT_SQL = "INSERT INTO Category (CategoryName) VALUES (?)";
    private static final String FIND_ALL_SQL = "SELECT * FROM Category";
    private static final String FIND_BY_NAME_SQL = "SELECT * FROM CATEGORY WHERE CATEGORYNAME=?";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM CATEGORY WHERE CATEGORYID=?";

    private final JdbcTemplate jdbcTemplate;

    public CategoryDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Category> categoryRowMapper = (rs, rowNum) -> {
        Category c = new Category();
        c.setCategoryId(rs.getInt("CategoryID"));
        c.setCategoryName(rs.getString("CategoryName"));
        return c;
    };

    /**
     * Adds a new category to the database.
     *
     * @param categoryName
     * @return number of rows affected
     */
    @Override
    public int addCategory(String categoryName) {
        return jdbcTemplate.update(INSERT_SQL, categoryName);
    }

    /**
     * Retrieves all categories from the database.
     *
     * @return a list of all categories
     */
    @Override
    public List<Category> getAllCategories() {
        return jdbcTemplate.query(FIND_ALL_SQL, categoryRowMapper);
    }

    /**
     * Finds a category by its name.
     *
     * @param categoryName
     * @return an Optional containing the category, or empty if not found
     */
    @Override
    public Optional<Category> findByName(String categoryName) {
        List<Category> categories = jdbcTemplate.query(FIND_BY_NAME_SQL, categoryRowMapper, categoryName);
        return categories.stream().findFirst();
    }

    /**
     * Finds a category by its ID.
     *
     * @param categoryId
     * @return an Optional containing the category, or empty if not found
     */
    @Override
    public Optional<Category> findById(int categoryId) {
        List<Category> categories = jdbcTemplate.query(FIND_BY_ID_SQL, categoryRowMapper, categoryId);
        return categories.stream().findFirst();
    }
}