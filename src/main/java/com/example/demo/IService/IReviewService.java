package com.example.demo.IService;

import com.example.demo.model.Reviews;
import com.example.demo.model.User;

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