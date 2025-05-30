package com.example.demo.admin.reviews;

import com.example.demo.reviews.ReviewService;
import com.example.demo.reviews.data.ReviewDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/reviews")
public class AdminApiReviewController {
    private final ReviewService reviewService;

    @Autowired
    public AdminApiReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDir,
            @RequestParam(required = false) Long gameId,
            @RequestParam(required = false) Long userId) {
        try {
            Sort sort = sortBy != null && !sortBy.isEmpty()
                    ? Sort.by(sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy)
                    : Sort.unsorted();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<ReviewDTO> reviewsPage = reviewService.getAllReviews(pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("content", reviewsPage.getContent());
            response.put("currentPage", reviewsPage.getNumber());
            response.put("totalPages", reviewsPage.getTotalPages());
            response.put("totalElements", reviewsPage.getTotalElements());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch reviews: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        try {
            reviewService.deleteReview(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}