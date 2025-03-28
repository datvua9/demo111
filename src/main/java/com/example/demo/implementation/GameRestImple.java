//package com.example.demo.implementation;
//
//import com.example.demo.dto.GameDTO;
//import com.example.demo.service.GameService;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class GameRestImple {
//    private final GameService gameService;
//
//    public GameRestImple(GameService gameService) {
//        this.gameService = gameService;
//    }
//
//    public ResponseEntity<List<GameDTO>> getAllGames() {
//        List<GameDTO> games = gameService.getAllGamesList();
//        return new ResponseEntity<>(games, HttpStatus.OK);
//    }
//
//    public ResponseEntity<GameDTO> createGame(GameDTO gameDTO) {
//        GameDTO createdGame = gameService.createGame(gameDTO);
//        return new ResponseEntity<>(createdGame, HttpStatus.CREATED);
//    }
//
//    public ResponseEntity<GameDTO> updateGame(Long id, GameDTO gameDTO) {
//        GameDTO updatedGame = gameService.updateGame(id, gameDTO);
//        return updatedGame != null ? new ResponseEntity<>(updatedGame, HttpStatus.OK) :
//                new ResponseEntity<>(HttpStatus.NOT_FOUND);
//    }
//
//    public ResponseEntity<Void> deleteGame(Long id) {
//        gameService.deleteGame(id);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
//}
