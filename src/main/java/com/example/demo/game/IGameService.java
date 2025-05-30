package com.example.demo.game;

import com.example.demo.game.data.GameDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IGameService {
    Page<GameDTO> getFilteredGames(String category, String year, String platform, Pageable pageable);

    GameDTO getGameById(Long id);
    List<GameDTO> getRelatedGames(String genre);
    Page<GameDTO> getAllGames(Pageable pageable);
    List<GameDTO> getAllGamesList();
    List<GameDTO> getNewReleaseGames();
    List<GameDTO> getMustPlayGames();
    List<GameDTO> getCommingGames();

    Page<GameDTO> getAllGames(Pageable pageable, String genre, String platform, String year);

    GameDTO createGame(GameDTO gameDTO);
    GameDTO updateGame(Long id, GameDTO gameDTO);
    boolean deleteGame(Long id);
}