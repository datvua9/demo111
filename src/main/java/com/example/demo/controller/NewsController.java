package com.example.demo.controller;

import com.example.demo.dto.NewsDTO;
import com.example.demo.implementation.NewsRestImple;
import com.example.demo.implementation.NewsViewImple;
import com.example.demo.model.Comment;
import com.example.demo.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/news")
public class NewsController {
    private final NewsService newsService;
    private final NewsViewImple newsViewImple;

    @Autowired
    public NewsController(NewsViewImple newsViewImple,NewsService newsService) {
        this.newsViewImple = newsViewImple;
        this.newsService = newsService;
    }

    @GetMapping("")
    public String news(Model model) {
        NewsDTO featuredNews = newsService.getLatestNews();
        if (featuredNews == null) {
            featuredNews = newsService.getAllNewsList().get(0);
        }
        List<NewsDTO> allNews = newsService.getAllNewsList();

        model.addAttribute("featuredNews", featuredNews);
        model.addAttribute("allNews", allNews);
        return "news";
    }

    @GetMapping("/{id}")
    public String newsDetail(@PathVariable Long id, Model model) {
        NewsDTO newsDTO = newsViewImple.getNewsDetails(id);

        List<String> paragraphs = Arrays.asList(newsDTO.getContent().split("\n"));
        if(newsDTO != null) {
            List<Comment> comments = newsViewImple.getComments(id);
            model.addAttribute("totalNews", comments.size());
            model.addAttribute("comment", comments);
            model.addAttribute("news", newsDTO);
            model.addAttribute("paragraphs", paragraphs);

            return "game_news";
        } else {
            return "error/404";
        }
    }
}
