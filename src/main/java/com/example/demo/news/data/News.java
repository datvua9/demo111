package com.example.demo.news.data;
import com.example.demo.comment.data.Comment;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;
@Data
@Entity
@Table(name = "news")
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "news_id")
    private Long newsId;

    private String title;
    private String content;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "created_at")
    private Date createdAt;
    private String image;

    @OneToMany
    @JoinColumn(name = "news_id")
    private List<Comment> comments;

    public News() {}
    public void convertToDTO(NewsDTO dto) {
        this.newsId = dto.getNewsId();
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.createdAt = dto.getCreated_at();
        this.image = dto.getImage();
    }
}