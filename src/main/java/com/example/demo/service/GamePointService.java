package com.example.demo.service;

import com.example.demo.model.Reviews;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GamePointService {
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
}
