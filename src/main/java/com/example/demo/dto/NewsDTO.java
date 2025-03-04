package com.example.demo.dto;

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
}
