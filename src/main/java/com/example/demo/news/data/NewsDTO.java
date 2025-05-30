package com.example.demo.news.data;

import com.example.demo.game.data.Games;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
@Data
public class NewsDTO {
    private Long newsId;
    private String title;
    private String content;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date created_at;
    private String image;

    public NewsDTO() {}

    public NewsDTO(Long newsId, String title, String content , String image) {
        this.newsId = newsId;
        this.title = title;
        this.content = content;
        this.image = image;
    }

    public News toEntity() {
        News news = new News();
        news.setTitle( this.title );
        news.setContent( this.content );
        news.setImage( this.image );
        news.setCreatedAt( this.created_at );
        return news;
    }

    public void convertToEntity(News news) {
          this.newsId = news.getNewsId();
          this.title = news.getTitle();
          this.content = news.getContent();
          this.image = news.getImage();
          this.created_at = news.getCreatedAt();
    }
}
