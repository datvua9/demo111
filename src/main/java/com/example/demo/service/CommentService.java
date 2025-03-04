package com.example.demo.service;

import com.example.demo.IService.CommentServiceImple;
import com.example.demo.model.Comment;
import com.example.demo.model.User;
import com.example.demo.repository.CommentRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService implements CommentServiceImple {
    @Autowired
    private CommentRepository commentRepository;

    @PostConstruct
    public void checkComments() {
        List<Comment> comments = commentRepository.findAll();
        comments.forEach(System.out::println);
    }

    @Override
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    @Override
    public void saveComment(Comment comment) {
        commentRepository.save(comment);
    }

    @Override
    public List<Comment> findByNewsId(Long newsId) {
        return commentRepository.findByNewsId(newsId);
    }

    @Override
    public Optional<Comment> findById(Long commentId) {
        return commentRepository.findById(commentId);
    }

    @Override
    public void saveOrUpdateComment(Comment comment) {
        Optional<Comment> existingComment = commentRepository.findByUserAndNewsId(comment.getUser(), comment.getNewsId()); // Sửa tên phương thức
        if (existingComment.isPresent()) {
            Comment updatedReview = existingComment.get();
            updatedReview.setComment(comment.getComment());
            updatedReview.setComment_date(LocalDate.now());
            commentRepository.save(updatedReview);
        } else {
            comment.setComment_date(LocalDate.now());
            commentRepository.save(comment);
        }
    }

    @Override
    public Comment getCommentByUserAndNewsId(User user, Long newsId) {
        return commentRepository.findByUserAndNewsId(user, newsId).orElse(null); // Sửa tên phương thức
    }
}
