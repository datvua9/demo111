package com.example.demo.controller;

import com.example.demo.dto.GameDTO;
import com.example.demo.service.GameService;
import com.example.demo.webSocket.GameWebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
@CrossOrigin(origins = "*")
public class GameAPIController {
    @Autowired
    private GameService gameService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GameDTO>> getAllGames() {
        List<GameDTO> games = gameService.getAllGamesList();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(games);
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

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteGame(@PathVariable("id") Long id) {
        boolean deleted = gameService.deleteGame(id);
        if (deleted) {
            GameWebSocket.notifyClients("Game " + id + " deleted");
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
