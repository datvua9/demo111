package com.example.demo.game.component;

import com.example.demo.reviews.data.Reviews;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RatingCalculator {
    public float calculateOverallAverage(List<Reviews> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return 0f;
        }
        float totalRating = 0f;
        for (Reviews review : reviews) {
            totalRating += review.getRating();
        }
        return totalRating / reviews.size();
    }
}