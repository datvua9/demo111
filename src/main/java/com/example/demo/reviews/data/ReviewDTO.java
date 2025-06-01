package com.example.demo.reviews.data;

import com.example.demo.user.data.User;
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
    private String username;
    private String gameName;
    private String userAvatar;

    public String getUserAvatar() { return userAvatar; }
    public void setUserAvatar(String userAvatar) { this.userAvatar = userAvatar; }

    public ReviewDTO() {
        this.username = "Unknown";
        this.gameName = "Unknown";
    }

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
        this.username = "Unknown";
        this.gameName = "Unknown";
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

    public float getRating() {
        return (float) (music_rating + gameplay_rating + story_rating + graphic_rating) / 4;
    }
}