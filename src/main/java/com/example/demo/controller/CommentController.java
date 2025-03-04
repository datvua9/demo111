package com.example.demo.controller;

import com.example.demo.dto.NewsDTO;
import com.example.demo.model.*;
import com.example.demo.IService.CommentServiceImple;
import com.example.demo.service.NewsService;
import com.example.demo.service.UserService;
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
    private final CommentServiceImple commentServiceImple;
    private final NewsService newsService;
    private final UserService userService;

    public CommentController(CommentServiceImple commentServiceImple,NewsService newsService,UserService userService) {
        this.commentServiceImple = commentServiceImple;
        this.newsService = newsService;
        this.userService = userService;
    }

    @PostMapping("/submitComment")
    public ModelAndView submitComment(@ModelAttribute Comment comment, @RequestParam Long newsId, Model model ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.getCurrentUser();
        NewsDTO news = newsService.getNewsById(newsId);

        comment.setUser(user);
        comment.setNewsId(news.getNewsId());
        comment.setComment_date(LocalDate.now());
        commentServiceImple.saveOrUpdateComment(comment);

        return new ModelAndView("redirect:/news/" + newsId);
    }

    @GetMapping("/comment/edit/{id}")
    public String editComment(@PathVariable Long id, Model model) {
        Optional<Comment> commentOptional = commentServiceImple.findById(id);
        if (commentOptional.isPresent()) {
            Comment comment = commentOptional.get();
            model.addAttribute("comment", comment);
            return "comment/edit";
        } else {
            return "error/404";
        }
    }

    @PostMapping("/comment/update")
    public String updateComment(@ModelAttribute Comment comment) {
        commentServiceImple.saveComment(comment);
        return "redirect:/news/" + comment.getNewsId();
    }

    @GetMapping("/commentForm")
    public String showCommentForm(@RequestParam("newsId") Long newsId, Model model) {
        User user = userService.getCurrentUser();
        NewsDTO news = newsService.getNewsById(newsId);

        if (user == null || news == null) {
            return "error/404";
        }

        Comment existingcomment = commentServiceImple.getCommentByUserAndNewsId(user, newsId);

        Comment comment;
        if (existingcomment != null) {
            comment = existingcomment;
        } else {
            comment = new Comment();
            comment.setNewsId(news.getNewsId());
        }
        model.addAttribute("comment", comment);
        model.addAttribute("newsId", newsId);
        return "commentForm";
    }
}