package com.example.demo.reviews;

import com.example.demo.game.GamesRepository;
import com.example.demo.game.data.Games;
import com.example.demo.reviews.data.ReviewDTO;
import com.example.demo.reviews.data.Reviews;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
    @Autowired
    private ReviewsRepository reviewsRepository;

    @Autowired
    private GamesRepository gamesRepository; // Thêm GamesRepository

    @PostConstruct
    @Transactional(readOnly = true)
    public void checkReviews() {
        List<Reviews> reviews = reviewsRepository.findAll();
        reviews.forEach(review -> {
            System.out.println("Review ID: " + review.getReview_id() +
                    ", Game ID: " + review.getGameId() +
                    ", User: " + (review.getUser() != null ? review.getUser().getUsername() : "Unknown"));
        });
    }

    public Page<ReviewDTO> getAllReviews(Pageable pageable) {
        Page<Reviews> reviewsPage = reviewsRepository.findAll(pageable);
        return reviewsPage.map(this::convertToDTO);
    }

    public Optional<Reviews> findById(Long reviewId) {
        return reviewsRepository.findById(reviewId);
    }

    public void deleteReview(Long reviewId) {
        reviewsRepository.deleteById(reviewId);
    }

    private ReviewDTO convertToDTO(Reviews review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setReview_id(review.getReview_id());
        dto.setUserId(review.getUser() != null ? review.getUser().getUserId() : null);
        dto.setUsername(review.getUser() != null ? review.getUser().getUsername() : "Unknown");
        dto.setGameId(review.getGameId());
        // Lấy gameName từ GamesRepository
        Games game = gamesRepository.findById(review.getGameId()).orElse(null);
        dto.setGameName(game != null ? game.getName() : "Unknown");
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