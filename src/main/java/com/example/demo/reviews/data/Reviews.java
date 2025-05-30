package com.example.demo.reviews.data;

import com.example.demo.user.data.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "Reviews")
public class Reviews {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long review_id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "game_id")
    private Long gameId;

    private int music_rating;
    private int gameplay_rating;
    private int story_rating;
    private int graphic_rating;
    @Transient
    private float rating;
    public float getRating() {
        return (music_rating + gameplay_rating + story_rating + graphic_rating) / 4.0f;
    }

    private String comment;
    private String platform;

    @Column(name = "review_date")
    private LocalDate reviewDate;

    public Reviews() {}

    public Reviews(User user, Long gameId, int music_rating, int gameplay_rating, int story_rating, int graphic_rating, String comment, String platform, LocalDate reviewDate) {
        this.user = user;
        this.gameId = gameId;
        this.music_rating = music_rating;
        this.gameplay_rating = gameplay_rating;
        this.story_rating = story_rating;
        this.graphic_rating = graphic_rating;
        this.comment = comment;
        this.platform = platform;
        this.reviewDate = reviewDate;
    }
}