package com.example.demo.game;

import com.example.demo.game.data.GameDTO;
import com.example.demo.reviews.data.ReviewDTO;
import com.example.demo.reviews.data.Reviews;
import com.example.demo.reviews.ReviewsRepository;
import com.example.demo.reviews.IReviewService;
import com.example.demo.game.component.RatingCalculator;
import com.example.demo.user.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;

@Service
public class GameViewImple {

    private final IGameService gameService;

    @Value("${http://localhost:8081}") // Ví dụ: app.base-url=http://localhost:8081 trong application.properties
    private String appBaseUrl;

    @Autowired
    public GameViewImple(IGameService gameService  /*, GamePointService gamePointService, IReviewService reviewService, RatingCalculator ratingCalculator*/) {
        this.gameService = gameService;
        // this.gamePointService = gamePointService;
        // this.reviewService = reviewService;
        // this.ratingCalculator = ratingCalculator;
    }

    public List<GameDTO> getAllGamesBasicInfo() {
        // Lấy danh sách game với thông tin cơ bản (không cần reviews, điểm chi tiết)
        return gameService.getAllGamesList().stream().map(gameDTO -> {
            processGameDtoImageUrls(gameDTO);
            return gameDTO;
        }).collect(Collectors.toList());
    }

    public GameDTO getGameDetailsWithReviews(Long gameId) {
        GameDTO gameDTO = gameService.getGameById(gameId); // gameService đã tạo GameDTO hoàn chỉnh
        if (gameDTO == null) {
            return null;
        }

        // Xử lý URL hình ảnh cho game chính
        processGameDtoImageUrls(gameDTO);


        // Xử lý URL hình ảnh cho các review avatars nếu chúng là đường dẫn tương đối
        if (gameDTO.getReviews() != null) {
            gameDTO.getReviews().forEach(reviewDTO -> {
                if (reviewDTO.getUserAvatar() != null && !reviewDTO.getUserAvatar().startsWith("http")) {
                    if (!reviewDTO.getUserAvatar().startsWith("/")) {
                        reviewDTO.setUserAvatar(appBaseUrl + "/" + reviewDTO.getUserAvatar());
                    } else {
                        reviewDTO.setUserAvatar(appBaseUrl + reviewDTO.getUserAvatar());
                    }
                }
            });
        }
        return gameDTO;
    }

    // Hàm tiện ích để xử lý URL hình ảnh cho một GameDTO
    private void processGameDtoImageUrls(GameDTO gameDTO) {
        if (gameDTO.getImage() != null && !gameDTO.getImage().startsWith("http")) {
            if (!gameDTO.getImage().startsWith("/")) {
                gameDTO.setImage(appBaseUrl + "/" + gameDTO.getImage());
            } else {
                gameDTO.setImage(appBaseUrl + gameDTO.getImage());
            }
        }
        // Bạn có thể làm tương tự cho gameDTO.getVideo() nếu nó cũng là đường dẫn tương đối
    }

    // Các phương thức calculateAverage và calculateOverallAverage cũ nên được loại bỏ
    // vì logic này đã được chuyển vào GameService (sử dụng RatingCalculatorHelper)
    // và kết quả đã có sẵn trong GameDTO trả về từ gameService.getGameById().

    // Nếu Controller cần danh sách related games đã xử lý URL:
    public List<GameDTO> getProcessedRelatedGames(String genre) {
        List<GameDTO> relatedGames = gameService.getRelatedGames(genre);
        relatedGames.forEach(this::processGameDtoImageUrls);
        return relatedGames;
    }
}