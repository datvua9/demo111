package com.example.demo.comment.controller;

import com.example.demo.comment.ICommentService;
import com.example.demo.comment.data.Comment;
import com.example.demo.user.data.User;
import com.example.demo.user.UserService; // Giả sử bạn có UserService
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/comments")
public class CommentApiController {
    private final ICommentService commentService;
    private final UserService userService;

    public CommentApiController(ICommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.userService = userService;
    }

    @GetMapping("/news/{newsId}")
    public ResponseEntity<List<Comment>> getCommentsByNewsId(@PathVariable Long newsId) {
        List<Comment> comments = commentService.findByNewsId(newsId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/news/{newsId}")
    public ResponseEntity<?> postComment(@PathVariable Long newsId,
                                         @RequestBody Map<String, String> payload,
                                         Authentication authentication) {
        String commentText = payload.get("comment");
        if (commentText == null || commentText.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Comment cannot be empty."));
        }

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not authenticated."));
        }

        User currentUser = userService.findByUsername(authentication.getName());
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not found."));
        }

        Comment commentToSave = commentService.getCommentByUserAndNewsId(currentUser, newsId);
        if (commentToSave == null) {
            commentToSave = new Comment();
            commentToSave.setUser(currentUser);
            commentToSave.setNewsId(newsId);
        }
        commentToSave.setComment(commentText);
        commentToSave.setComment_date(LocalDate.now());

        commentService.saveOrUpdateComment(commentToSave);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentToSave);
    }

    @GetMapping("/news/{newsId}/count")
    public ResponseEntity<Long> getCommentsCountByNewsId(@PathVariable Long newsId) {
        List<Comment> comments = commentService.findByNewsId(newsId);
        return ResponseEntity.ok((long) comments.size());
    }
}