package com.example.demo.reviews;

import com.example.demo.reviews.data.Reviews;
import com.example.demo.user.data.User;

import java.util.List;
import java.util.Optional;

public interface IReviewService {
    List<Reviews> getAllReviews();
    void saveReview(Reviews review);
    void deleteReview(Reviews review);
    List<Reviews> findByGameId(Long gameId);
    Optional<Reviews> findById(Long reviewId);
    void saveOrUpdateReview(Reviews review);
    Reviews getReviewByUserAndGameId(User user, Long gameId);
}