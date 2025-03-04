package com.example.demo.controller;

import com.example.demo.dto.GameDTO;
import com.example.demo.implementation.GameRestImple;
import com.example.demo.implementation.GameViewImple;
import com.example.demo.model.Reviews;
import com.example.demo.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/game")
public class GameController {
    private final GameViewImple gameViewImple;
    private final GameRestImple gameRestImple;
    private final GameService gameService;

    @Autowired
    public GameController(GameViewImple gameViewImple, GameRestImple gameRestImple, GameService gameService) {
        this.gameViewImple = gameViewImple;
        this.gameRestImple = gameRestImple;
        this.gameService = gameService;
    }

    @GetMapping("/home")
    public String gameHome(Model model) {
        model.addAttribute("games", gameViewImple.getAllGames());
        return "game_home";
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
            return "error/404";
        }
    }

    @GetMapping("/api/all")
    public ResponseEntity<List<GameDTO>> getAllGames() {
        return gameRestImple.getAllGames();
    }

    @PostMapping("/api/add")
    public ResponseEntity<GameDTO> createGame(@RequestBody GameDTO gameDTO) {
        return gameRestImple.createGame(gameDTO);
    }

    @PutMapping("/api/update/{id}")
    public ResponseEntity<GameDTO> updateGame(@PathVariable Long id, @RequestBody GameDTO gameDTO) {
        return gameRestImple.updateGame(id, gameDTO);
    }

    @DeleteMapping("/api/delete/{id}")
    public ResponseEntity<Void> deleteGame(@PathVariable Long id) {
        return gameRestImple.deleteGame(id);
    }
}