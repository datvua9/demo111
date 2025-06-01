package com.example.demo.API;

import com.example.demo.game.data.GameDTO;
import com.example.demo.game.GameService;
import com.example.demo.webSocket.GameWebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/games")
public class GameAPIController {

    @Autowired
    private GameService gameService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<GameDTO>> getAllGames(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String year,
            @RequestParam(required = false) String platform
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<GameDTO> gamesPage = gameService.getFilteredGames(category, year, platform, pageable);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(gamesPage);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GameDTO> updateGame(@PathVariable("id") Long id, @RequestBody GameDTO gameDTO) {
        GameDTO updatedGame = gameService.updateGame(id, gameDTO);
        if (updatedGame != null) {
            GameWebSocket.notifyClients("Game " + id + " updated");
            return ResponseEntity.ok(updatedGame);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GameDTO> updateGamePost(@PathVariable("id") Long id, @RequestBody GameDTO gameDTO) {
        System.out.println("Received update request for game: " + gameDTO.getGameId());
        GameDTO updatedGame = gameService.updateGame(id, gameDTO);
        if (updatedGame != null) {
            GameWebSocket.notifyClients("Game " + id + " updated");
            return ResponseEntity.ok(updatedGame);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}