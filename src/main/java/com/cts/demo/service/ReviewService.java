package com.cts.demo.service;

import com.cts.demo.model.Review;

import java.util.List;

public interface ReviewService
{

    int addReview(Review review);

    List<Review> getReviewsForBook(int bookId);

    List<Review> findByUserId(int userId);

    int moderateReview(int reviewId, String comment);

    List<Review> findAll();
}
