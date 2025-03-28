package com.example.demo.implementation;

import com.example.demo.dto.GameDTO;
import com.example.demo.model.Reviews;
import com.example.demo.repository.GamesRepository;
import com.example.demo.repository.ReviewsRepository;
import com.example.demo.service.GamePointService;
import com.example.demo.IService.IReviewService;
import com.example.demo.component.RatingCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameViewImple {
    private final GamePointService gamePointService;
    private final IReviewService reviewService;
    private final ReviewsRepository reviewsRepository;
    private final RatingCalculator ratingCalculator;

    @Autowired
    private GamesRepository gamesRepository;

    public GameViewImple(GamePointService gamePointService, IReviewService reviewService,
                         ReviewsRepository reviewsRepository, RatingCalculator ratingCalculator) {
        this.gamePointService = gamePointService;
        this.reviewService = reviewService;
        this.reviewsRepository = reviewsRepository;
        this.ratingCalculator = ratingCalculator;
    }

    public List<GameDTO> getAllGames() {
        return gamesRepository.findAll().stream()
                .map(game -> {
                    GameDTO dto = new GameDTO();
                    dto.convertToEntity(game);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public GameDTO getGameDetails(Long id) {
        return gamesRepository.findById(id)
                .map(game -> {
                    GameDTO dto = new GameDTO();
                    dto.convertToEntity(game);
                    return dto;
                })
                .orElse(null);
    }

    public List<Reviews> getReviews(Long gameId) {
        return reviewService.findByGameId(gameId);
    }

    public String calculateAverage(List<Reviews> reviews, String category) {
        DecimalFormat df = new DecimalFormat("#.0");
        return df.format(gamePointService.calculateAverageRating(reviews, category));
    }

    public String calculateOverallAverage(List<Reviews> reviews) {
        DecimalFormat df = new DecimalFormat("#.0");
        return df.format(ratingCalculator.calculateOverallAverage(reviews));
    }

    public float calculateOverallAverage(Long gameId) {
        List<Reviews> reviews = reviewsRepository.findByGameId(gameId);
        return ratingCalculator.calculateOverallAverage(reviews);
    }
}