package com.example.demo.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;

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
    public Comment(User user, Long newsId, String comment) {
        this.user = user;
        this.newsId = newsId;
        this.comment = comment;
    }
}
