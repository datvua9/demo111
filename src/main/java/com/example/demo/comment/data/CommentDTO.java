package com.example.demo.comment.data;

import com.example.demo.user.data.User;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CommentDTO {
    private Long comment_id;
    private Long userId;
    private Long newsId;
    private String comment;
    private LocalDate comment_date;
    private String username;
    private String NewsName;

    public CommentDTO(){}

    public CommentDTO(Long newsId, String comment,Long userId, LocalDate comment_date) {
        this.newsId = newsId;
        this.comment = comment;
        this.userId = userId;
        this.comment_date = comment_date;
    }

    public Comment convertToComment(User user) {
        Comment comment = new Comment();
        comment.setComment_id(this.comment_id);
        comment.setUser(user);
        comment.setNewsId(this.newsId);
        comment.setComment(this.comment);
        comment.setComment_date(this.comment_date);
        return comment;
      }

}

