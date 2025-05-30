package com.example.demo.game.gamelist.controller;

import com.example.demo.game.GameService;
import com.example.demo.game.GamesRepository;
import com.example.demo.game.data.GameDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/games")
public class GameListController {

    private final GameService gameService;
    private final GamesRepository gamesRepository;
    private final String baseUrl = "http://localhost:8081"; // Base URL của backend

    @Autowired
    public GameListController(GameService gameService, GamesRepository gamesRepository) {
        this.gameService = gameService;
        this.gamesRepository = gamesRepository;
    }

    @GetMapping("/home")
    public Map<String, Object> getGameList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "16") int pageSize,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String year,
            @RequestParam(required = false) String platform) {
        try {
            Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.ASC, "name"));
            Page<GameDTO> filteredGames = gameService.getFilteredGames(category, year, platform, pageable);

            List<GameDTO> gamesWithFullImageUrl = filteredGames.getContent().stream().map(game -> {
                if (game.getImage() != null && !game.getImage().startsWith("http")) {
                    game.setImage(baseUrl + game.getImage());
                }
                return game;
            }).collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("games", gamesWithFullImageUrl);
            response.put("currentPage", page);
            response.put("totalPages", filteredGames.getTotalPages());
            response.put("categories", Arrays.asList("Action", "Adventure", "RPG", "Strategy"));
            response.put("years", Arrays.asList("2025", "2024", "2023", "2022"));
            response.put("platforms", Arrays.asList("PC", "PS5", "Xbox", "Switch"));
            response.put("selectedCategory", category);
            response.put("selectedYear", year);
            response.put("selectedPlatform", platform);

            return response;
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch games: " + e.getMessage());
            return errorResponse;
        }
    }

    @GetMapping("/new_release")
    public List<GameDTO> getNewReleaseGames() {
        LocalDate recentDate = LocalDate.now().minusDays(365);
        return gamesRepository.findByReleaseDateAfterAndStatusNot(
                        recentDate, 0, Sort.by(Sort.Direction.DESC, "releaseDate"))
                .stream()
                .map(game -> {
                    GameDTO dto = new GameDTO();
                    dto.convertToEntity(game);
                    if (dto.getImage() != null && !dto.getImage().startsWith("http")) {
                        dto.setImage(baseUrl + dto.getImage());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/must_play")
    public List<GameDTO> getMustPlayGames() {
        return gameService.getMustPlayGames().stream().map(game -> {
            if (game.getImage() != null && !game.getImage().startsWith("http")) {
                game.setImage(baseUrl + game.getImage());
            }
            return game;
        }).collect(Collectors.toList());
    }

    @GetMapping("/coming_soon")
    public List<GameDTO> getComingSoonGames() {
        return gameService.getCommingGames().stream().map(game -> {
            if (game.getImage() != null && !game.getImage().startsWith("http")) {
                game.setImage(baseUrl + game.getImage());
            }
            return game;
        }).collect(Collectors.toList());
    }
}