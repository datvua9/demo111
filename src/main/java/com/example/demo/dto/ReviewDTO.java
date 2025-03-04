package com.example.demo.dto;

import com.example.demo.model.Reviews;
import com.example.demo.model.User;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReviewDTO {
    private Long review_id;
    private Long userId;
    private Long gameId;
    private int music_rating;
    private int gameplay_rating;
    private int story_rating;
    private int graphic_rating;
    private String comment;
    private String platform;
    private LocalDate reviewDate;
    private String username; // Thêm trường username
    private String gameName; // Thêm trường gameName

    public ReviewDTO() {}

    public ReviewDTO(Long gameId, int gameplay_rating, int music_rating, int graphic_rating, int story_rating, String comment, String platform, Long userId, LocalDate reviewDate) {
        this.gameId = gameId;
        this.gameplay_rating = gameplay_rating;
        this.music_rating = music_rating;
        this.graphic_rating = graphic_rating;
        this.story_rating = story_rating;
        this.comment = comment;
        this.platform = platform;
        this.userId = userId;
        this.reviewDate = reviewDate;
    }

    public ReviewDTO(Long review_id, Long gameId, int gameplay_rating, int music_rating, int graphic_rating, int story_rating, String comment, String platform, Long userId, LocalDate reviewDate) {
        this.review_id = review_id;
        this.gameId = gameId;
        this.gameplay_rating = gameplay_rating;
        this.music_rating = music_rating;
        this.graphic_rating = graphic_rating;
        this.story_rating = story_rating;
        this.comment = comment;
        this.platform = platform;
        this.userId = userId;
        this.reviewDate = reviewDate;
    }

    public Reviews convertToReview(User user) {
        Reviews review = new Reviews();
        review.setReview_id(this.review_id);
        review.setUser(user);
        review.setGameId(this.gameId);
        review.setGameplay_rating(this.gameplay_rating);
        review.setMusic_rating(this.music_rating);
        review.setGraphic_rating(this.graphic_rating);
        review.setStory_rating(this.story_rating);
        review.setComment(this.comment);
        review.setPlatform(this.platform);
        review.setReviewDate(this.reviewDate);
        return review;
    }

    // Sửa convertToEntity để nhận Reviews
    public void convertToEntity(Reviews reviews) {
        this.review_id = reviews.getReview_id();
        this.userId = reviews.getUser() != null ? reviews.getUser().getUser_id() : null;
        this.username = reviews.getUser() != null ? reviews.getUser().getUsername() : "Unknown";
        this.gameId = reviews.getGameId();
        this.gameplay_rating = reviews.getGameplay_rating();
        this.music_rating = reviews.getMusic_rating();
        this.graphic_rating = reviews.getGraphic_rating();
        this.story_rating = reviews.getStory_rating();
        this.comment = reviews.getComment();
        this.platform = reviews.getPlatform();
        this.reviewDate = reviews.getReviewDate();
    }

    public float getRating() {
        return (float) (music_rating + gameplay_rating + story_rating + graphic_rating) / 4;
    }
}