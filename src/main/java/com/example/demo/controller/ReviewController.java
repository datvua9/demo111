package com.example.demo.controller;

import com.example.demo.IService.IGameService;
import com.example.demo.dto.GameDTO;
import com.example.demo.dto.ReviewDTO;
import com.example.demo.implementation.ReviewServiceImpl;
import com.example.demo.model.Reviews;
import com.example.demo.model.User;
import com.example.demo.service.GameService;
import com.example.demo.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.Optional;

@Controller
public class ReviewController {
    private ReviewServiceImpl reviewService;
    private IGameService gameService;
    private UserService userService;

    public ReviewController(ReviewServiceImpl reviewService, GameService gameService,UserService userService) {
        this.reviewService = reviewService;
        this.gameService = gameService;
        this.userService = userService;
    }

    @PostMapping("/submitReview")
    public ModelAndView submitReview(@RequestParam Long gameId,
                                     @RequestParam("gameplay_rating") int gameplayRating,
                                     @RequestParam("music_rating") int musicRating,
                                     @RequestParam("graphics_rating") int graphicsRating,
                                     @RequestParam("story_rating") int storyRating,
                                     @RequestParam("platform") String platform,
                                     @RequestParam("comment") String comment) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userService.getCurrentUser();
        GameDTO game = gameService.getGameById(gameId);

        if (user == null || game == null) {
            ModelAndView mav = new ModelAndView("login");
            mav.addObject("errorMessage", "Không tìm thấy người dùng hoặc game.");
            return mav;
        }
        Reviews existingReview = reviewService.getReviewByUserAndGameId(user, gameId);
        if (existingReview != null) {
            reviewService.deleteReview(existingReview);
        }

        ReviewDTO reviewDTO = new ReviewDTO(gameId, gameplayRating, musicRating, graphicsRating, storyRating, comment, platform, user.getUser_id(), LocalDate.now());
        Reviews review = reviewDTO.convertToReview(user);

        reviewService.saveReview(review);

        return new ModelAndView("redirect:/game/" + gameId);
    }

    @GetMapping("/review/edit/{id}")
    public String editReview(@PathVariable Long id, Model model) {
        Optional<Reviews> reviewOptional = reviewService.findById(id);
        if (reviewOptional.isPresent()) {
            Reviews review = reviewOptional.get();
            model.addAttribute("review", review);
            return "review/edit";
        } else {
            return "error/404";
        }
    }

    @PostMapping("/review/update")
    public String updateReview(@ModelAttribute Reviews review) {
        reviewService.saveReview(review);
        return "redirect:/game/" + review.getGameId();
    }

    @GetMapping("/reviewForm")
    public String showReviewForm(@RequestParam("gameId") Long gameId, Model model) {
        User user = userService.getCurrentUser();
        GameDTO game = gameService.getGameById(gameId);

        if (user == null || game == null) {
            return "error/404";
        }
        Reviews existingReview = reviewService.getReviewByUserAndGameId(user, gameId);
        Reviews review;
        if (existingReview != null) {
            review = existingReview;
        } else {
            review = new Reviews();
            review.setGameId(game.getGameId());
        }
        model.addAttribute("review", review);
        model.addAttribute("gameId", gameId);
        return "reviewForm";
    }
}