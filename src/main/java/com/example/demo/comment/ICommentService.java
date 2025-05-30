package com.example.demo.comment;

import com.example.demo.comment.data.Comment;

import com.example.demo.user.data.User;

import java.util.List;
import java.util.Optional;

public interface ICommentService {
    List<Comment> getAllComments();
    void saveComment(Comment comment);
    void deleteComment(Comment comment);
    List<Comment> findByNewsId(Long newsId);
    Optional<Comment> findById(Long commentId);
    void saveOrUpdateComment(Comment comment);
    Comment getCommentByUserAndNewsId(User user, Long newsId);
}
