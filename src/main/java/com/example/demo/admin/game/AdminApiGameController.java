package com.example.demo.admin.game;

import com.example.demo.game.GameService;
import com.example.demo.game.IGameService;
import com.example.demo.game.data.GameDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/games")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AdminApiGameController {

    private final IGameService gameService;

    @Value("${upload.dir}")
    private String uploadDir;

    @Autowired
    public AdminApiGameController(IGameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllGames(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(required = false) String sortBy, // Tham số sắp xếp
            @RequestParam(required = false, defaultValue = "asc") String sortDir, // Hướng sắp xếp
            @RequestParam(required = false) String genre, // Bộ lọc thể loại
            @RequestParam(required = false) String platform, // Bộ lọc nền tảng
            @RequestParam(required = false) String year) { // Bộ lọc năm
        try {
            Pageable pageable;
            if (sortBy != null && !sortBy.isEmpty()) {
                Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
                pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            } else {
                pageable = PageRequest.of(page, size);
            }
            Page<GameDTO> gamesPage = gameService.getAllGames(pageable, genre, platform, year);
            System.out.println("Total elements: " + gamesPage.getTotalElements());
            System.out.println("Total pages: " + gamesPage.getTotalPages());

            Map<String, Object> response = new HashMap<>();
            response.put("content", gamesPage.getContent());
            response.put("currentPage", gamesPage.getNumber());
            response.put("totalPages", gamesPage.getTotalPages());
            response.put("totalElements", gamesPage.getTotalElements());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch games: " + e.getMessage()));
        }
    }

    // Thêm game mới
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createGame(
            @RequestPart("game") GameDTO gameDTO,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "imageUrl", required = false) String imageUrl,
            @RequestParam(value = "imageSourceType", required = false) String imageSourceType) {
        try {
            // Xử lý ảnh
            if ("url".equals(imageSourceType) && imageUrl != null && !imageUrl.isEmpty()) {
                gameDTO.setImage(imageUrl);
            } else if ("file".equals(imageSourceType) && imageFile != null && !imageFile.isEmpty()) {
                String fileName = saveFile(imageFile);
                gameDTO.setImage("http://localhost:8081/uploads/" + fileName);
            }

            GameDTO savedGame = gameService.createGame(gameDTO);
            return ResponseEntity.ok(Map.of("message", "Game created successfully", "game", savedGame));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to upload image: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to create game: " + e.getMessage()));
        }
    }

    // Cập nhật game
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateGame(
            @PathVariable Long id,
            @RequestPart("game") GameDTO gameDTO,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "imageUrl", required = false) String imageUrl,
            @RequestParam(value = "imageSourceType", required = false) String imageSourceType) {
        try {
            // Xử lý ảnh
            if ("url".equals(imageSourceType) && imageUrl != null && !imageUrl.isEmpty()) {
                gameDTO.setImage(imageUrl);
            } else if ("file".equals(imageSourceType) && imageFile != null && !imageFile.isEmpty()) {
                String fileName = saveFile(imageFile);
                gameDTO.setImage("http://localhost:8081/uploads/" + fileName);
            }

            GameDTO updatedGame = gameService.updateGame(id, gameDTO);
            if (updatedGame == null) {
                return ResponseEntity.status(404).body(Map.of("error", "Game not found"));
            }
            return ResponseEntity.ok(Map.of("message", "Game updated successfully", "game", updatedGame));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to upload image: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to update game: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteGame(@PathVariable Long id) {
        try {
            boolean deleted = gameService.deleteGame(id);
            if (!deleted) {
                return ResponseEntity.status(404).body(Map.of("error", "Game not found"));
            }
            return ResponseEntity.ok(Map.of("message", "Game deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to delete game: " + e.getMessage()));
        }
    }

    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = saveFile(file);
            String fileUrl = "http://localhost:8081/uploads/" + fileName;
            return ResponseEntity.ok(Map.of("url", fileUrl));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to upload image: " + e.getMessage()));
        }
    }

    // Phương thức lưu file
    private String saveFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        Files.write(filePath, file.getBytes());

        return fileName;
    }
}