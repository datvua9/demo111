package com.example.demo.controller;

import com.example.demo.dto.GameDTO;
import com.example.demo.dto.ReviewDTO;
import com.example.demo.service.GameService;
import com.example.demo.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/admin/reviews")
public class AdminReviewController {
    private final ReviewService reviewService;

    @Autowired
    private GameService gameService;

    @Autowired
    public AdminReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("")
    public String adminReview(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "username") String sortBy,
                              @RequestParam(defaultValue = "asc") String sortDir,
                              Model model) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<ReviewDTO> reviewPage = reviewService.getAllReviews(pageable);
        List<ReviewDTO> reviewList = new ArrayList<>(reviewPage.getContent());

        reviewList.forEach(review -> {
            Long gameId = review.getGameId();
            if (gameId != null) {
                GameDTO game = gameService.getGameById(gameId);
                review.setGameName(game != null ? game.getName() : "Unknown Game");
            } else {
                review.setGameName("Unknown Game");
            }
        });

        Comparator<ReviewDTO> comparator;
        switch (sortBy) {
            case "gameName":
                comparator = Comparator.comparing(ReviewDTO::getGameName, Comparator.nullsLast(String::compareTo));
                break;
            case "rating":
                comparator = Comparator.comparing(ReviewDTO::getRating, Comparator.nullsLast(Float::compareTo));
                break;
            case "username":
            default:
                comparator = Comparator.comparing(ReviewDTO::getUsername, Comparator.nullsLast(String::compareTo));
                break;
        }
        if ("desc".equalsIgnoreCase(sortDir)) {
            comparator = comparator.reversed();
        }
        reviewList.sort(comparator);

        Page<ReviewDTO> sortedPage = new PageImpl<>(reviewList, pageable, reviewPage.getTotalElements());

        model.addAttribute("reviews", sortedPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", sortedPage.getTotalPages());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        return "admin_reviews";
    }

    @PostMapping("/delete/{id}")
    public String deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return "redirect:/admin/reviews";
    }
}