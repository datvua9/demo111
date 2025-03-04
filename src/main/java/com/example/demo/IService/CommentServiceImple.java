package com.example.demo.IService;

import com.example.demo.model.Comment;
import com.example.demo.model.User;

import java.util.List;
import java.util.Optional;

public interface CommentServiceImple {

    List<Comment> getAllComments();

    void saveComment(Comment comment);

    List<Comment> findByNewsId(Long newsId);

    Optional<Comment> findById(Long commentId);

    void saveOrUpdateComment(Comment comment) ;

    Comment getCommentByUserAndNewsId(User user, Long newsId);
}
