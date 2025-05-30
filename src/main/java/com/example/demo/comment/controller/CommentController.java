package com.example.demo.comment.controller;

import com.example.demo.comment.data.Comment;
import com.example.demo.comment.data.CommentDTO;
import com.example.demo.news.INewsService;
import com.example.demo.news.data.NewsDTO;
import com.example.demo.comment.CommentServiceImple;
import com.example.demo.user.UserService;
import com.example.demo.user.data.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.Optional;

@Controller
public class CommentController {
    private final CommentServiceImple commentService;
    private final INewsService newsService;
    private final UserService userService;

    public CommentController(CommentServiceImple commentService, INewsService newsService, UserService userService) {
        this.commentService = commentService;
        this.newsService = newsService;
        this.userService = userService;
    }

    @PostMapping("/submitComment")
    public ModelAndView submitComment(
            @RequestParam Long newsId,
            @RequestParam(value = "comment", required = false) String comment) {
        if (comment == null || comment.trim().isEmpty()) {
            ModelAndView mav = new ModelAndView("redirect:/news/" + newsId);
            mav.addObject("errorMessage", "Comment cannot be empty.");
            return mav;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userService.getCurrentUser();
        NewsDTO news = newsService.getNewsById(newsId);

        if (user == null || news == null) {
            ModelAndView mav = new ModelAndView("login");
            mav.addObject("errorMessage", "User or news not found.");
            return mav;
        }

        Comment existingComment = commentService.getCommentByUserAndNewsId(user, newsId);
        if (existingComment != null) {
            commentService.deleteComment(existingComment);
        }

        Comment newComment = new Comment();
        newComment.setNewsId(newsId);
        newComment.setComment(comment);
        newComment.setUser(user);
        newComment.setComment_date(LocalDate.now());

        commentService.saveComment(newComment);

        return new ModelAndView("redirect:/news/" + newsId);
    }

    @GetMapping("/comment/edit/{id}")
    public String editComment(@PathVariable Long id, Model model) {
        Optional<Comment> commentOptional = commentService.findById(id);
        if (commentOptional.isPresent()) {
            model.addAttribute("comment", commentOptional.get());
            return "comment/edit";
        }
        return "error/404";
    }

    @PostMapping("/comment/update")
    public String updateComment(@ModelAttribute Comment comment) {
        if (comment.getComment() == null || comment.getComment().trim().isEmpty()) {
            return "redirect:/news/" + comment.getNewsId() + "?error=emptyComment";
        }
        commentService.saveComment(comment);
        return "redirect:/news/" + comment.getNewsId();
    }

    @GetMapping("/commentForm")
    public String showCommentForm(@RequestParam("newsId") Long newsId, Model model) {
        User user = userService.getCurrentUser();
        NewsDTO news = newsService.getNewsById(newsId);

        if (user == null || news == null) {
            return "error/404";
        }

        Comment existingComment = commentService.getCommentByUserAndNewsId(user, newsId);
        Comment comment = existingComment != null ? existingComment : new Comment();
        comment.setNewsId(news.getNewsId());

        model.addAttribute("comment", comment);
        model.addAttribute("newsId", newsId);
        return "commentForm";
    }
}