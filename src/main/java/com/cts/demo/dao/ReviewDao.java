package com.cts.demo.dao;

import com.cts.demo.model.Review;

import java.util.List;


public interface ReviewDao {

    int saveReview(Review review);

    List<Review> findByBookId(int bookId);

    List<Review> findByUserId(int userId);

    int moderateReview(int reviewId, String comment);

    List<Review> findAll();
}