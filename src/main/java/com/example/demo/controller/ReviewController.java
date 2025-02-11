package com.example.demo.controller;

import com.example.demo.model.Games;
import com.example.demo.model.User_Reviews;
import com.example.demo.repository.GamesRepository;
import com.example.demo.repository.User_ReviewsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class ReviewController {

    @Autowired
    private User_ReviewsRepository reviewRepository;

    @Autowired
    private GamesRepository gameRepository; // Need a GamesRepository

    @GetMapping("/reviews/{gameId}") // Path with gameId
    public String showReviews(@PathVariable Long gameId, Model model) {
        Games game = gameRepository.findById(gameId).orElseThrow(() -> new EntityNotFoundException("Game not found"));
        List<User_Reviews> reviews = reviewRepository.findByGame(game);
        model.addAttribute("reviews", reviews);
        model.addAttribute("game", game); // Add the game to the model
        return "reviews"; // Return the name of the review template
    }
}