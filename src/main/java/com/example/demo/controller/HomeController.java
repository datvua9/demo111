package com.example.demo.controller;

import com.example.demo.dto.GameDTO;
import com.example.demo.dto.NewsDTO;
import com.example.demo.implementation.GameViewImple;
import com.example.demo.service.GameService;
import com.example.demo.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Controller
public class HomeController {
    private final GameService gameService;
    private final NewsService newsService;

    public HomeController(GameService gameService, NewsService newsService) {
        this.gameService = gameService;
        this.newsService = newsService;
    }
    @GetMapping("/home")
    public String home(@RequestParam(defaultValue = "0") int page, Model model) {;
        Pageable pageable = PageRequest.of(page, 4);
        Pageable pageable1 = PageRequest.of(page, 3);

        List<GameDTO> newReleaseGames = gameService.getNewReleaseGames();
        List<GameDTO> commingGame = gameService.getCommingGames();
        List<GameDTO> mustplay = gameService.getMustPlayGames();
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