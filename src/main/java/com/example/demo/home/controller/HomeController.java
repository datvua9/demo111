package com.example.demo.home.controller;

import com.example.demo.game.data.GameDTO;
import com.example.demo.news.data.NewsDTO;
import com.example.demo.game.GameService;
import com.example.demo.news.NewsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {
    private final GameService gameService;
    private final NewsService newsService;

    public HomeController(GameService gameService, NewsService newsService) {
        this.gameService = gameService;
        this.newsService = newsService;
    }
    @GetMapping("/home")
    public String home(@RequestParam(defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(page, 4);
        Pageable pageable1 = PageRequest.of(page, 3);

        List<GameDTO> newReleaseGames = gameService.getNewReleaseGames().stream().limit(10).toList();
        List<GameDTO> commingGame = gameService.getCommingGames().stream().limit(10).toList();
        List<GameDTO> mustplay = gameService.getMustPlayGames().stream().limit(10).toList();
        Page<NewsDTO> news = newsService.getAllNews(pageable1);

        model.addAttribute("newReleaseGames", newReleaseGames);
        model.addAttribute("comingSoonGames", commingGame);
        model.addAttribute("mustPlayGames", mustplay);
        model.addAttribute("currentPage", page);
        model.addAttribute("news", news != null ? news : List.of());
        return "home";
    }

    @GetMapping("/")
    public String redirectHome() {
        return "redirect:/home";
    }
}
