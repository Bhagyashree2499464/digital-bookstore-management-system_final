package com.cts.demo.service.impl;

import com.cts.demo.dao.ReviewDao;
import com.cts.demo.model.Review;
import com.cts.demo.service.ReviewService;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewDao reviewDao;

    public ReviewServiceImpl(ReviewDao reviewDao) {
        this.reviewDao = reviewDao;
    }

    /**
     * Adds or updates a review.
     *
     * @param review
     * @return number of rows affected
     */
    @Override
    public int addReview(Review review) {
        return reviewDao.saveReview(review);
    }

    /**
     * Retrieves all reviews for a specific book.
     *
     * @param bookId
     * @return a list of reviews
     */
    @Override
    public List<Review> getReviewsForBook(int bookId) {
        return reviewDao.findByBookId(bookId);
    }

    /**
     * Retrieves all reviews by a specific user.
     *
     * @param userId
     * @return a list of reviews
     */
    @Override
    public List<Review> findByUserId(int userId){
        return reviewDao.findByUserId(userId);
    }

    /**
     * Moderates a review by updating its comment.
     *
     * @param reviewId
     * @param comment
     * @return number of rows affected
     */
    @Override
    public int moderateReview(int reviewId, String comment){
        return reviewDao.moderateReview(reviewId, comment);
    }

    /**
     * Retrieves all reviews.
     *
     * @return a list of all reviews
     */
    @Override
    public List<Review> findAll() {
        return reviewDao.findAll();
    }
}