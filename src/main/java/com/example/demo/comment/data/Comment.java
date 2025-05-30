package com.example.demo.comment.data;

import com.example.demo.user.data.User;
import com.example.demo.news.data.News;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // Trường này chỉ để đọc, không dùng để ghi vào DB nếu 'news' quản lý FK
    @Column(name = "news_id", insertable = false, updatable = false)
    private Long newsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_id", nullable = false) // Trường này sẽ quản lý việc ghi vào cột "news_id"
    private News news;

    private String comment;

    @Column(name = "comment_date")
    private LocalDate comment_date;

    public Comment() {}

    public Comment(User user, News news, String comment, LocalDate comment_date) {
        this.user = user;
        this.news = news;
        if (news != null) { // Đồng bộ newsId nếu news được cung cấp
            this.newsId = news.getNewsId();
        }
        this.comment = comment;
        this.comment_date = comment_date;
    }

    // Lombok sẽ tạo getter/setter cho cả newsId và news.
    // Khi bạn setNews(someNewsObject), bạn cũng nên cập nhật this.newsId = someNewsObject.getNewsId();
    public void setNews(News news) {
        this.news = news;
        if (news != null) {
            this.newsId = news.getNewsId();
        } else {
            this.newsId = null;
        }
    }
    @Override
    public String toString() {
        return "Comment{" +
                "comment_id=" + comment_id +
                ", user=" + (user != null ? "User(id=" + user.getUser_id() + ")" : "null") +
                ", news=" + (news != null && org.hibernate.Hibernate.isInitialized(news) ? "News(id=" + news.getNewsId() + ", title=" + news.getTitle() + ")" : "not_initialized_or_null") +
                ", comment='" + comment + '\'' +
                ", comment_date=" + comment_date +
                '}';
    }
}