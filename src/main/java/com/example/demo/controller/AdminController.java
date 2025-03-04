package com.example.demo.controller;

import com.example.demo.dto.GameDTO;
import com.example.demo.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final GameService gameService;

    @Autowired
    public AdminController( GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("")
    public String adminHome(@RequestParam(defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(page, 4);
        Page<GameDTO> gamesPage = gameService.getAllGames(pageable);
        model.addAttribute("games", gamesPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", gamesPage.getTotalPages());
        return "admin";
    }

    @PostMapping("/add")
    public String addGame(@ModelAttribute GameDTO gameDTO) {
        gameService.createGame(gameDTO);
        return "redirect:/admin";
    }

    @GetMapping("/edit/{id}")
    public String showEditGameForm(@PathVariable Long id, Model model) {
        GameDTO game = gameService.getGameById(id);
        if (game != null) {
            model.addAttribute("game", game);
            return "edit_game";
        }
        return "error/404";
    }

    @PostMapping("/edit/{id}")
    public String editGame(@PathVariable Long id, @ModelAttribute GameDTO gameDTO) {
        gameService.updateGame(id, gameDTO);
        return "redirect:/admin";
    }

    @PostMapping("/delete/{id}")
    public String deleteGame(@PathVariable Long id) {
        gameService.deleteGame(id);
        return "redirect:/admin";
    }

}
