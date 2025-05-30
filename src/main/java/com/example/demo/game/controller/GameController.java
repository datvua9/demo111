package com.example.demo.game.controller;

import com.example.demo.game.IGameService;
import com.example.demo.game.data.GameDTO;
import com.example.demo.game.GameViewImple;
import com.example.demo.reviews.data.Reviews;
import com.example.demo.game.GameService;
import com.example.demo.service.SteamApiService;
import com.example.demo.user.UserProfileRepository;
import com.example.demo.user.data.User;
import com.example.demo.user.data.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/games") // Thay đổi từ "/game" sang "/api/games" để thống nhất với các API khác
public class GameController {

    private final GameViewImple gameViewImple;
    private final IGameService gameService;
    private final UserProfileRepository userProfileRepository;

    @Autowired
    private SteamApiService steamApiService;

    @Autowired
    public GameController(GameViewImple gameViewImple, GameService gameService, UserProfileRepository userProfileRepository) {
        this.gameViewImple = gameViewImple;
        this.gameService = gameService;
        this.userProfileRepository = userProfileRepository;
    }
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getGameDetails(@PathVariable Long id) {
        GameDTO game = gameViewImple.getGameDetails(id);
        if (game != null) {
            List<Reviews> reviews = gameViewImple.getReviews(id);
            Map<Long, UserProfile> userProfiles = new HashMap<>();
            for (Reviews review : reviews) {
                User user = review.getUser();
                if (user != null) {
                    UserProfile userProfile = userProfileRepository.findByUser(user);
                    userProfiles.put(user.getUser_id(), userProfile);
                }
            }

            // Thêm baseUrl vào image
            String baseUrl = "http://localhost:8081";
            if (game.getImage() != null && !game.getImage().startsWith("http")) {
                game.setImage(baseUrl + game.getImage());
            }
//            for (Reviews review : reviews) {
//                if (review.getUser() != null && review.getUser().getUserProfile() != null &&
//                        review.getUser().getUserProfile().getAvatar() != null &&
//                        !review.getUser().getUserProfile().getAvatar().startsWith("http")) {
//                    review.getUser().getUserProfile().setAvatar(baseUrl + review.getUser().getUserProfile().getAvatar());
//                }
//            }
            // Ánh xạ relatedGames
            List<GameDTO> relatedGames = gameService.getRelatedGames(game.getGenre());
            relatedGames.forEach(relatedGame -> {
                if (relatedGame.getImage() != null && !relatedGame.getImage().startsWith("http")) {
                    relatedGame.setImage(baseUrl + relatedGame.getImage());
                }
            });

            Map<String, Object> response = new HashMap<>();
            response.put("game", game);
            response.put("totalReviews", reviews.size());
            response.put("reviews", reviews);
            response.put("relatedGames", relatedGames);
            response.put("avgGameplay", Double.parseDouble(gameViewImple.calculateAverage(reviews, "gameplay").toString().replace(",", ".")));
            response.put("avgMusic", Double.parseDouble(gameViewImple.calculateAverage(reviews, "music").toString().replace(",", ".")));
            response.put("avgGraphic", Double.parseDouble(gameViewImple.calculateAverage(reviews, "graphic").toString().replace(",", ".")));
            response.put("avgStory", Double.parseDouble(gameViewImple.calculateAverage(reviews, "story").toString().replace(",", ".")));
            response.put("overallAverage", Double.parseDouble(gameViewImple.calculateOverallAverage(reviews).toString().replace(",", ".")));
            response.put("userProfiles", userProfiles);
            response.put("defaultAvatar", baseUrl + "/img/alec.png"); // Sửa thành URL đầy đủ

            System.out.println("Game details for id " + id + ": " + response);
            return ResponseEntity.ok(response);
        } else {
            System.out.println("Game not found for id " + id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/steam/{appId}")
    public ResponseEntity<Map<String, Object>> getSteamGameDetails(@PathVariable Long appId) {
        GameDTO game = steamApiService.getGameData(appId);
        if (game != null && game.getName() != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("game", game);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}