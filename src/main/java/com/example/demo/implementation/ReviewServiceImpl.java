package com.example.demo.implementation;

import com.example.demo.model.Reviews;
import com.example.demo.model.User;
import com.example.demo.repository.ReviewsRepository;
import com.example.demo.IService.IReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements IReviewService {
    private final ReviewsRepository reviewsRepository;

    @Autowired
    public ReviewServiceImpl(ReviewsRepository reviewsRepository) {
        this.reviewsRepository = reviewsRepository;
    }

    @Override
    public List<Reviews> getAllReviews() {
        return reviewsRepository.findAll();
    }

    @Override
    public void saveReview(Reviews review) {
        reviewsRepository.save(review);
    }

    @Override
    public void deleteReview(Reviews review) {reviewsRepository.delete(review);}

    @Override
    public List<Reviews> findByGameId(Long gameId) {
        return reviewsRepository.findByGameId(gameId);
    }

    @Override
    public Optional<Reviews> findById(Long reviewId) {
        return reviewsRepository.findById(reviewId);
    }

    @Override
    public void saveOrUpdateReview(Reviews review) {
        reviewsRepository.save(review);
    }

    @Override
    public Reviews getReviewByUserAndGameId(User user, Long gameId) {
        return reviewsRepository.findByUserAndGameId(user, gameId).orElse(null);
    }
}