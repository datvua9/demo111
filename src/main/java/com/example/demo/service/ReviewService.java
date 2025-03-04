package com.example.demo.service;

import com.example.demo.dto.ReviewDTO;
import com.example.demo.model.Reviews;
import com.example.demo.model.User;
import com.example.demo.repository.ReviewsRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
    @Autowired
    private ReviewsRepository reviewsRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private GameService gameService;

    @PostConstruct
    public void checkReviews() {
        List<Reviews> reviews = reviewsRepository.findAll();
        reviews.forEach(System.out::println);
    }

    public Page<ReviewDTO> getAllReviews(Pageable pageable) {
        Page<Reviews> reviewsPage = reviewsRepository.findAll(pageable);
        Page<ReviewDTO> reviewDTOPage = reviewsPage.map(this::convertToDTO);

        return reviewDTOPage;
    }

    public void saveReview(Reviews review) {
        reviewsRepository.save(review);
    }

    public List<Reviews> findByGameId(Long gameId) {
        return reviewsRepository.findByGameId(gameId);
    }

    public Optional<Reviews> findById(Long reviewId) {
        return reviewsRepository.findById(reviewId);
    }

    public void saveOrUpdateReview(Reviews review) {
        Optional<Reviews> existingReview = reviewsRepository.findByUserAndGameId(review.getUser(), review.getGameId());
        if (existingReview.isPresent()) {
            Reviews updatedReview = existingReview.get();
            updatedReview.setMusic_rating(review.getMusic_rating());
            updatedReview.setGameplay_rating(review.getGameplay_rating());
            updatedReview.setStory_rating(review.getStory_rating());
            updatedReview.setGraphic_rating(review.getGraphic_rating());
            updatedReview.setComment(review.getComment());
            updatedReview.setPlatform(review.getPlatform());
            updatedReview.setReviewDate(LocalDate.now());
            reviewsRepository.save(updatedReview);
            return;
        }
        review.setReviewDate(LocalDate.now());
        reviewsRepository.save(review);
    }

    public Reviews getReviewByUserAndGameId(User user, Long gameId) {
        return reviewsRepository.findByUserAndGameId(user, gameId).orElse(null);
    }

    public void deleteReview(Long reviewId) {
        reviewsRepository.deleteById(reviewId);
    }

    private ReviewDTO convertToDTO(Reviews review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setReview_id(review.getReview_id());
        dto.setUserId(review.getUser() != null ? review.getUser().getUser_id() : null);
        dto.setUsername(review.getUser() != null ? review.getUser().getUsername() : "Unknown");
        dto.setGameId(review.getGameId());
        dto.setMusic_rating(review.getMusic_rating());
        dto.setGameplay_rating(review.getGameplay_rating());
        dto.setStory_rating(review.getStory_rating());
        dto.setGraphic_rating(review.getGraphic_rating());
        dto.setComment(review.getComment());
        dto.setPlatform(review.getPlatform());
        dto.setReviewDate(review.getReviewDate());
        return dto;
    }
}