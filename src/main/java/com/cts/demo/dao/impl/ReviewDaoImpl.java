package com.cts.demo.dao.impl;

import com.cts.demo.dao.ReviewDao;
import com.cts.demo.model.Review;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JDBC-based implementation of {@link ReviewDao}.
 */
@Repository
public class ReviewDaoImpl implements ReviewDao {

    private static final String INSERT_OR_UPDATE_SQL = """
            INSERT INTO Review (UserID, BookID, Rating, Comments) VALUES (?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
            Rating = VALUES(Rating),
            Comments = VALUES(Comments)
            """;
    private static final String FIND_BY_BOOK_ID_SQL = "SELECT * FROM Review WHERE BookID = ?";
    private static final String FIND_BY_USER_ID_SQL = "SELECT * FROM Review WHERE USERID = ?";
    private static final String MODERATE_SQL = "UPDATE REVIEW SET COMMENTS=? WHERE REVIEWID=?";
    private static final String FIND_ALL_SQL = "SELECT * FROM Review";

    private final JdbcTemplate jdbcTemplate;

    public ReviewDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Review> reviewRowMapper = (rs, rowNum) ->
            new Review(
                    rs.getInt("ReviewID"),
                    rs.getInt("UserID"),
                    rs.getInt("BookID"),
                    rs.getInt("Rating"),
                    rs.getString("Comments")
            );

    /**
     * Saves a new review or updates an existing one if the user has already reviewed the book.
     *
     * @param review
     * @return number of rows affected
     */
    @Override
    public int saveReview(Review review) {
        return jdbcTemplate.update(INSERT_OR_UPDATE_SQL,
                review.getUserId(),
                review.getBookId(),
                review.getRating(),
                review.getComment());
    }

    /**
     * Retrieves all reviews for a specific book.
     *
     * @param bookId
     * @return a list of reviews for the book
     */
    @Override
    public List<Review> findByBookId(int bookId) {
        return jdbcTemplate.query(FIND_BY_BOOK_ID_SQL, reviewRowMapper, bookId);
    }

    /**
     * Retrieves all reviews written by a specific user.
     *
     * @param userId
     * @return a list of reviews written by the user
     */
    @Override
    public List<Review> findByUserId(int userId) {
        return jdbcTemplate.query(FIND_BY_USER_ID_SQL, reviewRowMapper, userId);
    }

    /**
     * Moderates a review by updating its comment.
     *
     * @param reviewId
     * @param comment
     * @return number of rows affected
     */
    @Override
    public int moderateReview(int reviewId, String comment) {
        return jdbcTemplate.update(MODERATE_SQL, comment, reviewId);
    }

    /**
     * Retrieves all reviews from the database.
     *
     * @return a list of all reviews
     */
    @Override
    public List<Review> findAll() {
        return jdbcTemplate.query(FIND_ALL_SQL, reviewRowMapper);
    }
}