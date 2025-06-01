package com.example.demo.comment;

import com.example.demo.comment.data.Comment;
import com.example.demo.comment.data.CommentDTO;
import com.example.demo.user.data.User;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    @PostConstruct
    public void checkComments() {
        List<Comment> comments = commentRepository.findAll();
        comments.forEach(System.out::println);
    }

    public Page<CommentDTO> getAllComment(Pageable pageable) {
        Page<Comment> commentsPage = commentRepository.findAll(pageable);
        Page<CommentDTO> commentDTOPage = commentsPage.map(this::convertToDTO);

        return commentDTOPage;
    }

    public Optional<Comment> findById(Long commentId) {
        return commentRepository.findById(commentId);
    }

    public void deleteComment(Long commentId) {

        commentRepository.deleteById(commentId);
    }

    private CommentDTO convertToDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setComment_id(comment.getComment_id());
        dto.setUserId(comment.getUser() != null ? comment.getUser().getUserId() : null);
        dto.setUsername(comment.getUser() != null ? comment.getUser().getUsername() : "Unknown");
        dto.setNewsId(comment.getNewsId());
        dto.setComment(comment.getComment());
        dto.setComment_date(comment.getComment_date());
        return dto;
    }
}
