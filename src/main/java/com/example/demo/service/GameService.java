package com.example.demo.service;

import com.example.demo.IService.IGameService;
import com.example.demo.component.RatingCalculator;
import com.example.demo.dto.GameDTO;
import com.example.demo.model.Games;
import com.example.demo.model.Reviews;
import com.example.demo.repository.GamesRepository;
import com.example.demo.repository.ReviewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameService implements IGameService {
    @Autowired
    private GamesRepository gamesRepository;

    @Autowired
    private ReviewsRepository reviewsRepository;

    @Autowired
    private RatingCalculator ratingCalculator;

    @Override
    public GameDTO getGameById(Long id) {
        return gamesRepository.findById(id)
                .map(game -> {
                    GameDTO dto = new GameDTO();
                    dto.convertToEntity(game);
                    return dto;
                })
                .orElse(null);
    }

    @Override
    public List<GameDTO> getRelatedGames(String genre) {
        return gamesRepository.findByGenre(genre).stream()
                .map(game -> new GameDTO(game.getGameId(), game.getName(), game.getImage(), game.getGenre()))
                .collect(Collectors.toList());
    }

    @Override
    public Page<GameDTO> getAllGames(Pageable pageable) {
        return gamesRepository.findAll(pageable)
                .map(game -> {
                    GameDTO dto = new GameDTO();
                    dto.convertToEntity(game);
                    return dto;
                });
    }

    @Override
    public List<GameDTO> getAllGamesList() {
        return gamesRepository.findAll().stream()
                .map(game -> {
                    GameDTO dto = new GameDTO();
                    dto.convertToEntity(game);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<GameDTO> getNewReleaseGames() {
        return gamesRepository.findByStatusNot(0, Sort.by(Sort.Direction.DESC, "releaseDate")).stream()
                .map(game -> {
                    GameDTO dto = new GameDTO();
                    dto.convertToEntity(game);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<GameDTO> getMustPlayGames() {
        List<GameDTO> games = getAllGamesList().stream()
                .filter(game -> game.getStatus() != 0)
                .collect(Collectors.toList());

        games.forEach(game -> {
            List<Reviews> reviews = reviewsRepository.findByGameId(game.getGameId());
            float avgRating = ratingCalculator.calculateOverallAverage(reviews);
            game.setRating(avgRating);
        });
        return games.stream()
                .sorted(Comparator.comparing(GameDTO::getRating).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<GameDTO> getCommingGames() {
        return gamesRepository.findByStatus(0).stream()
                .map(game -> new GameDTO(game.getGameId(), game.getName(), game.getImage(), game.getGenre()))
                .collect(Collectors.toList());
    }

    @Override
    public GameDTO createGame(GameDTO gameDTO) {
        Games game = new Games();
        game.convertToDTO(gameDTO);
        Games savedGame = gamesRepository.save(game);
        GameDTO savedDTO = new GameDTO();
        savedDTO.convertToEntity(savedGame);
        return savedDTO;
    }

    @Override
    public GameDTO updateGame(Long id, GameDTO gameDTO) {
        Games existingGame = gamesRepository.findById(id).orElse(null);

        if (existingGame != null) {
            List<Reviews> existingReviews = existingGame.getReviews();
            existingGame.convertToDTO(gameDTO);
            existingGame.setReviews(existingReviews);
            Games updatedGame = gamesRepository.save(existingGame);

            GameDTO updatedDTO = new GameDTO();
            updatedDTO.convertToEntity(updatedGame);
            return updatedDTO;
        }
        return null;
    }

    @Override
    public boolean deleteGame(Long id) {
        if (gamesRepository.existsById(id)) {
            gamesRepository.deleteById(id);
            return true;
        }
        return false;
    }
}