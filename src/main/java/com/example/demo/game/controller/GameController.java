package com.example.demo.game.controller;

import com.example.demo.game.IGameService;
import com.example.demo.game.data.GameDTO;
import com.example.demo.game.GameViewImple;
import com.example.demo.reviews.data.ReviewDTO;
import com.example.demo.reviews.data.Reviews;
import com.example.demo.game.GameService;
import com.example.demo.service.SteamApiService;
import com.example.demo.user.UserProfileRepository;
import com.example.demo.user.data.User;
import com.example.demo.user.data.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
        // Yêu cầu GameViewImple trả về GameDTO đã chứa List<ReviewDTO>
        GameDTO gameDTO = gameViewImple.getGameDetailsWithReviews(id); // Cần tạo/sửa method này trong GameViewImple

        if (gameDTO == null) {
            System.out.println("Game not found for id " + id);
            return ResponseEntity.notFound().build();
        }

        // Danh sách ReviewDTO đã có trong gameDTO
        List<ReviewDTO> reviewDTOs = gameDTO.getReviews() != null ? gameDTO.getReviews() : new ArrayList<>();

        String baseUrl = "http://localhost:8081"; // Nên lấy từ cấu hình
        // Xử lý URL hình ảnh cho game chính
        if (gameDTO.getImage() != null && !gameDTO.getImage().startsWith("http")) {
            if (!gameDTO.getImage().startsWith("/")) gameDTO.setImage(baseUrl + "/" + gameDTO.getImage());
            else gameDTO.setImage(baseUrl + gameDTO.getImage());
        }

        // Xử lý URL hình ảnh cho related games (nếu relatedGames nằm trong GameDTO)
        // Hoặc nếu bạn lấy relatedGames riêng:
        List<GameDTO> relatedGames = gameService.getRelatedGames(gameDTO.getGenre()); // Giả sử đây là IGameService
        relatedGames.forEach(relatedGame -> {
            if (relatedGame.getImage() != null && !relatedGame.getImage().startsWith("http")) {
                if (!relatedGame.getImage().startsWith("/")) relatedGame.setImage(baseUrl + "/" + relatedGame.getImage());
                else relatedGame.setImage(baseUrl + relatedGame.getImage());
            }
        });


        Map<String, Object> response = new HashMap<>();
        response.put("game", gameDTO); // gameDTO đã chứa List<ReviewDTO> reviews
        response.put("totalReviews", reviewDTOs.size());
        response.put("reviews", reviewDTOs); // Gửi List<ReviewDTO>
        response.put("relatedGames", relatedGames); // Gửi danh sách relatedGames đã xử lý

        // Các điểm avg... nên được tính và set vào GameDTO từ service/GameViewImple
        // Hoặc truyền vào response map nếu GameDTO không có các trường này
        response.put("avgGameplay", gameDTO.getAvgGameplay()); // Giả sử GameDTO có các trường này
        response.put("avgMusic", gameDTO.getAvgMusic());
        response.put("avgGraphic", gameDTO.getAvgGraphic()); // Sửa tên nếu cần
        response.put("avgStory", gameDTO.getAvgStory());
        response.put("overallAverage", gameDTO.getOverallReviewAverage()); // Hoặc gameDTO.getRating() tùy theo ý nghĩa

        response.put("defaultAvatar", baseUrl + "/img/alec.png");

        // System.out.println("Game details for id " + id + ": " + response);
        return ResponseEntity.ok(response);
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