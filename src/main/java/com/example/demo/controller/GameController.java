package com.example.demo.controller;

import com.example.demo.IService.IGameService;
import com.example.demo.dto.GameDTO;
import com.example.demo.implementation.GameViewImple;
import com.example.demo.model.Reviews;
import com.example.demo.service.GameService;
import com.example.demo.service.SteamApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/game")
public class GameController {
    private final GameViewImple gameViewImple;
    private final IGameService gameService;

    @Autowired
    private SteamApiService steamApiService;

    @Autowired
    public GameController(GameViewImple gameViewImple, GameService gameService) {
        this.gameViewImple = gameViewImple;
        this.gameService = gameService;
    }

    @GetMapping("/{id}")
    public String gameDetails(@PathVariable Long id, Model model) {
        GameDTO game = gameViewImple.getGameDetails(id);
        if (game != null) {
            List<Reviews> reviews = gameViewImple.getReviews(id);
            model.addAttribute("totalReviews", reviews.size());
            model.addAttribute("reviews", reviews);
            model.addAttribute("game", game);
            model.addAttribute("games", gameService.getRelatedGames(game.getGenre()));
            model.addAttribute("avgGameplay", gameViewImple.calculateAverage(reviews, "gameplay"));
            model.addAttribute("avgMusic", gameViewImple.calculateAverage(reviews, "music"));
            model.addAttribute("avgGraphic", gameViewImple.calculateAverage(reviews, "graphic"));
            model.addAttribute("avgStory", gameViewImple.calculateAverage(reviews, "story"));
            model.addAttribute("overallAverage", gameViewImple.calculateOverallAverage(reviews));
            return "game_home";
        } else {
            return "404";
        }
    }

    @GetMapping("/steam/{appId}")
    public String steamGameDetails(@PathVariable Long appId, Model model) {
        GameDTO game = steamApiService.getGameData(appId);
        if (game != null && game.getName() != null) {
            model.addAttribute("game", game);
            return "steam_game";
        } else {
            return "404";
        }
    }
}
