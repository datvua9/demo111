package com.example.demo.comment.data;

import com.example.demo.user.data.User;
import jakarta.persistence.*;

import java.time.LocalDate;

import lombok.Data;

@Data
@Entity
@Table(name = "News_Comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long comment_id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "news_id")
    private Long newsId;

    private String comment;

    @Column(name = "comment_date")
    private LocalDate comment_date;

    public Comment(){}
    public Comment(User user, Long newsId, String comment, LocalDate comment_date) {
        this.user = user;
        this.newsId = newsId;
        this.comment = comment;
        this.comment_date = comment_date;
    }
}
