package com.example.demo.service;

import com.example.demo.dto.GameDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IGameService {
    GameDTO getGameById(Long id);
    List<GameDTO> getRelatedGames(String genre);
    Page<GameDTO> getAllGames(Pageable pageable);
    List<GameDTO> getAllGamesList();
    List<GameDTO> getNewReleaseGames();
    List<GameDTO> getMustPlayGames();
    List<GameDTO> getCommingGames();
    GameDTO createGame(GameDTO gameDTO);
    GameDTO updateGame(Long id, GameDTO gameDTO);
    void deleteGame(Long id);
}