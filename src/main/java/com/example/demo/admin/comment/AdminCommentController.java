package com.example.demo.admin.comment;

import com.example.demo.comment.CommentService;
import com.example.demo.comment.data.CommentDTO;
import com.example.demo.news.NewsService;
import com.example.demo.news.data.NewsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/admin/comments")
    public class AdminCommentController {
        private final CommentService commentService;

        @Autowired
        private NewsService newsService;

        @Autowired
        public AdminCommentController(CommentService commentService) {
            this.commentService = commentService;
        }

        @GetMapping("")
        public String adminComment(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "username") String sortBy,
                                  @RequestParam(defaultValue = "asc") String sortDir,
                                  Model model) {
            Pageable pageable = PageRequest.of(page, 10);
            Page<CommentDTO> commentPage = commentService.getAllComment(pageable);
            List<CommentDTO> commentList = new ArrayList<>(commentPage.getContent());

            commentList.forEach(comment -> {
                Long newsId = comment.getNewsId();
                if (newsId != null) {
                    NewsDTO news = newsService.getNewsById(newsId);
                    comment.setNewsName(news != null ? news.getTitle() : "Unknown Title");
                } else {
                    comment.setNewsName("Unknown News");
                }
            });

            Comparator<CommentDTO> comparator;
            switch (sortBy) {
                case "newsTitle":
                    comparator = Comparator.comparing(CommentDTO::getNewsName, Comparator.nullsLast(String::compareTo));
                    break;
                case "username":
                default:
                    comparator = Comparator.comparing(CommentDTO::getUsername, Comparator.nullsLast(String::compareTo));
                    break;
            }
            if ("desc".equalsIgnoreCase(sortDir)) {
                comparator = comparator.reversed();
            }
            commentList.sort(comparator);

            Page<CommentDTO> sortedPage = new PageImpl<>(commentList, pageable, commentPage.getTotalElements());

            model.addAttribute("comments", sortedPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", sortedPage.getTotalPages());
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDir", sortDir);
            return "admin_comment";
        }

        @PostMapping("/delete/{id}")
        public String deleteReview(@PathVariable Long id) {
            commentService.deleteComment(id);
            return "redirect:/admin/commnent";
        }
    }
