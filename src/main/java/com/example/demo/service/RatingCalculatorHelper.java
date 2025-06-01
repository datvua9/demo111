package com.example.demo.service;

import com.example.demo.reviews.data.ReviewDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.function.ToIntFunction;

@Component // Để có thể inject vào các service khác
public class RatingCalculatorHelper {

    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(1, RoundingMode.HALF_UP);

    public BigDecimal calculateAverageRatingForDTOs(List<ReviewDTO> reviewDTOs, ToIntFunction<ReviewDTO> ratingExtractor) {
        if (reviewDTOs == null || reviewDTOs.isEmpty()) {
            return ZERO;
        }
        double average = reviewDTOs.stream()
                .mapToInt(ratingExtractor)
                .average()
                .orElse(0.0);
        return BigDecimal.valueOf(average).setScale(1, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateOverallAverageRatingForDTOs(List<ReviewDTO> reviewDTOs) {
        if (reviewDTOs == null || reviewDTOs.isEmpty()) {
            return ZERO;
        }
        // Sử dụng getRating() từ ReviewDTO, giả sử nó trả về điểm trung bình của review đó
        double average = reviewDTOs.stream()
                .mapToDouble(ReviewDTO::getRating) // ReviewDTO::getRating trả về float
                .average()
                .orElse(0.0);
        return BigDecimal.valueOf(average).setScale(1, RoundingMode.HALF_UP);
    }
}