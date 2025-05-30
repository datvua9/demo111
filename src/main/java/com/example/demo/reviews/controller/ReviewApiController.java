package com.example.demo.reviews.controller;

import com.example.demo.reviews.ReviewServiceImpl;
import com.example.demo.reviews.data.ReviewDTO;
import com.example.demo.reviews.data.Reviews;
import com.example.demo.user.data.User;
import com.example.demo.game.IGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/reviews")
public class ReviewApiController {

    @Autowired
    private ReviewServiceImpl reviewService;

    @Autowired
    private IGameService gameService;

    @PostMapping
    public ResponseEntity<?> submitReview(@RequestBody ReviewDTO reviewDTO, HttpServletRequest request) {
        try {
            // Lấy user từ request (được middleware thêm vào)
            User user = (User) request.getAttribute("authenticatedUser");
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\": \"User not authenticated\"}");
            }

            // Kiểm tra game có tồn tại không
            if (reviewDTO.getGameId() == null || gameService.getGameById(reviewDTO.getGameId()) == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"Invalid game ID\"}");
            }

            // Validate dữ liệu
            if (reviewDTO.getGameplay_rating() < 0 || reviewDTO.getGameplay_rating() > 10 ||
                    reviewDTO.getMusic_rating() < 0 || reviewDTO.getMusic_rating() > 10 ||
                    reviewDTO.getGraphic_rating() < 0 || reviewDTO.getGraphic_rating() > 10 ||
                    reviewDTO.getStory_rating() < 0 || reviewDTO.getStory_rating() > 10) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"Ratings must be between 0 and 10\"}");
            }

            if (reviewDTO.getComment() == null || reviewDTO.getComment().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"Comment is required\"}");
            }

            if (reviewDTO.getPlatform() == null || reviewDTO.getPlatform().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"Platform is required\"}");
            }

            // Kiểm tra xem user đã review game này chưa
            Reviews existingReview = reviewService.getReviewByUserAndGameId(user, reviewDTO.getGameId());
            if (existingReview != null) {
                reviewService.deleteReview(existingReview);
            }

            // Tạo review mới
            reviewDTO.setUserId(user.getUser_id());
            reviewDTO.setReviewDate(LocalDate.now());
            Reviews review = reviewDTO.convertToReview(user);
            reviewService.saveReview(review);

            return ResponseEntity.ok("{\"message\": \"Review submitted successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"message\": \"Error submitting review: " + e.getMessage() + "\"}");
        }
    }
}