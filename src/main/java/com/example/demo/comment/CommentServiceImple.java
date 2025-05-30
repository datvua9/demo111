package com.example.demo.comment;

import com.example.demo.comment.data.Comment;
import com.example.demo.user.data.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImple implements ICommentService {
    private final CommentRepository commentRepository;

    public CommentServiceImple(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
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
    public void deleteComment(Comment comment) {commentRepository.delete(comment);}

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
        commentRepository.save(comment);
    }

    @Override
    public Comment getCommentByUserAndNewsId(User user, Long newsId) {
        return commentRepository.findByUserAndNewsId(user, newsId).orElse(null);
    }
}
