package com.cts.demo.reviewtests;

import com.cts.demo.dao.ReviewDao;
import com.cts.demo.model.Review;
import com.cts.demo.service.impl.ReviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ReviewServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class ReviewTest {

    @Mock
    private ReviewDao reviewDao;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private Review review;

    /**
     * Sets up a sample review before each test.
     */
    @BeforeEach
    void setUp() {
        review = new Review();
        review.setReviewId(1);
        review.setBookId(10);
        review.setUserId(100);
        review.setRating(5);
        review.setComment("Excellent book");
    }

    /**
     * Tests that addReview returns number of rows inserted.
     */
    @Test
    void addReview_shouldReturnRowsInserted() {
        when(reviewDao.saveReview(review)).thenReturn(1);

        int result = reviewService.addReview(review);

        assertEquals(1, result);
        verify(reviewDao).saveReview(review);
    }

    /**
     * Tests that getReviewsForBook returns list of reviews for a book.
     */
    @Test
    void getReviewsForBook_shouldReturnReviewList() {
        when(reviewDao.findByBookId(10))
                .thenReturn(List.of(review));

        List<Review> result =
                reviewService.getReviewsForBook(10);

        assertEquals(1, result.size());
        assertEquals(10, result.get(0).getBookId());
    }

    /**
     * Tests that findByUserId returns list of reviews by a user.
     */
    @Test
    void findByUserId_shouldReturnReviewList() {
        when(reviewDao.findByUserId(100))
                .thenReturn(List.of(review));

        List<Review> result =
                reviewService.findByUserId(100);

        assertEquals(1, result.size());
        assertEquals(100, result.get(0).getUserId());
    }

    /**
     * Tests that moderateReview returns number of rows updated.
     */
    @Test
    void moderateReview_shouldReturnRowsUpdated() {
        when(reviewDao.moderateReview(1, "Updated Comment"))
                .thenReturn(1);

        int result =
                reviewService.moderateReview(1, "Updated Comment");

        assertEquals(1, result);
        verify(reviewDao).moderateReview(1, "Updated Comment");
    }

    /**
     * Tests that findAll returns all reviews.
     */
    @Test
    void findAll_shouldReturnAllReviews() {
        when(reviewDao.findAll())
                .thenReturn(List.of(review));

        List<Review> result =
                reviewService.findAll();

        assertEquals(1, result.size());
    }
}