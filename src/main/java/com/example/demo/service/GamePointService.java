package com.example.demo.service;

import com.example.demo.model.Games;
import com.example.demo.model.Reviews;
import com.example.demo.repository.GamesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GamePointService {
    @Autowired
    private GamesRepository gamesRepository;

    public Optional<Games> getGameById(Long id) {
        return gamesRepository.findById(id);
    }


    public double calculateAverageRating(List<Reviews> reviews, String category) {
        if (reviews.isEmpty()) return 0;

        double sum = 0;
        for (Reviews review : reviews) {
            switch (category) {
                case "gameplay": sum += review.getGameplay_rating(); break;
                case "music": sum += review.getMusic_rating(); break;
                case "graphic": sum += review.getGraphic_rating(); break;
                case "story": sum += review.getStory_rating(); break;
                default: break;
            }
        }
        return sum / reviews.size();
    }

    public double calculateOverallAverage(List<Reviews> reviews) {
        double avgGameplay = calculateAverageRating(reviews, "gameplay");
        double avgMusic = calculateAverageRating(reviews, "music");
        double avgGraphic = calculateAverageRating(reviews, "graphic");
        double avgStory = calculateAverageRating(reviews, "story");

        return (avgGameplay + avgMusic + avgGraphic + avgStory) / 4;
    }
}
