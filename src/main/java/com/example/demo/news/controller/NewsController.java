package com.example.demo.news.controller;

import com.example.demo.news.INewsService;
import com.example.demo.news.data.NewsDTO;
import com.example.demo.news.NewsViewImple;
import com.example.demo.comment.data.Comment;
import com.example.demo.news.NewsService;
import com.example.demo.user.UserProfileRepository;
import com.example.demo.user.data.User;
import com.example.demo.user.data.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/news")
public class NewsController {
    private final INewsService newsService;
    private final NewsViewImple newsViewImple;
    private final UserProfileRepository userProfileRepository;

    @Autowired
    public NewsController(NewsViewImple newsViewImple, INewsService newsService, UserProfileRepository userProfileRepository) {
        this.newsViewImple = newsViewImple;
        this.newsService = newsService;
        this.userProfileRepository = userProfileRepository;
    }



    @GetMapping("/{id}")
    public String newsDetail(@PathVariable Long id, Model model) {
        NewsDTO newsDTO = newsViewImple.getNewsDetails(id);

        List<String> paragraphs = Arrays.asList(newsDTO.getContent().split("\n"));
        if(newsDTO != null) {
            List<Comment> comments = newsViewImple.getComments(id);

            Map<Long, UserProfile> userProfiles = new HashMap<>();

            for (Comment comment : comments) {
                User user = comment.getUser();
                if (user != null) {
                    UserProfile userProfile = userProfileRepository.findByUser(user);
                    userProfiles.put(user.getUser_id(), userProfile);
                }
            }

            model.addAttribute("totalNews", comments.size());
            model.addAttribute("comment", comments);
            model.addAttribute("news", newsDTO);
            model.addAttribute("paragraphs", paragraphs);
            model.addAttribute("userProfiles", userProfiles);
            model.addAttribute("defaultAvatar", "/img/alec.png");

            return "game_news";
        } else {
            return "error/404";
        }
    }
}
