package com.example.demo.admin.game;

import com.example.demo.game.IGameService;
import com.example.demo.game.data.GameDTO;
import com.example.demo.game.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final IGameService gameService;

    @Value("${upload.dir}")
    private String uploadDir;

    @Autowired
    public AdminController(GameService gameService) {
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
    public String addGame(@ModelAttribute GameDTO gameDTO,
                          @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                          @RequestParam(value = "imageUrl", required = false) String imageUrl,
                          @RequestParam(value = "imageSourceType", required = false) String imageSourceType) throws IOException {

        if ("url".equals(imageSourceType) && imageUrl != null && !imageUrl.isEmpty()) {
            // Use the URL directly
            gameDTO.setImage(imageUrl);
        } else if ("file".equals(imageSourceType) && imageFile != null && !imageFile.isEmpty()) {
            // Save the file and use the file path
            String fileName = saveFile(imageFile);
            gameDTO.setImage("/uploads/" + fileName);
        }

        gameService.createGame(gameDTO);
        return "redirect:/admin";
    }

    @GetMapping("/edit/{id}")
    public String showEditGameForm(@PathVariable Long id,
                                   @RequestParam(defaultValue = "0") int page,
                                   Model model) {
        GameDTO game = gameService.getGameById(id);
        if (game != null) {
            model.addAttribute("game", game);
            model.addAttribute("page", page);
            return "edit_game";
        }
        return "error/404";
    }

    @PostMapping("/edit/{id}")
    public String editGame(@PathVariable Long id,
                           @ModelAttribute GameDTO gameDTO,
                           @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                           @RequestParam(value = "imageUrl", required = false) String imageUrl,
                           @RequestParam(value = "imageSourceType", required = false) String imageSourceType,
                           @RequestParam(value = "page", defaultValue = "0") int page) throws IOException {

        if ("url".equals(imageSourceType) && imageUrl != null && !imageUrl.isEmpty()) {
            // Use the URL directly
            gameDTO.setImage(imageUrl);
        } else if ("file".equals(imageSourceType) && imageFile != null && !imageFile.isEmpty()) {
            // Save the file and use the file path
            String fileName = saveFile(imageFile);
            gameDTO.setImage("/uploads/" + fileName);
        }

        gameService.updateGame(id, gameDTO);
        return "redirect:/admin?page=" + page;
    }

    @PostMapping("/delete/{id}")
    public String deleteGame(@PathVariable Long id) {
        gameService.deleteGame(id);
        return "redirect:/admin";
    }

    private String saveFile(MultipartFile file) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        Files.write(filePath, file.getBytes());

        return fileName;
    }
}
