package com.example.demo.service;

import com.example.demo.game.data.GameDTO;
import com.example.demo.game.data.Games;
import com.example.demo.game.GamesRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class SteamApiService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final GamesRepository gamesRepository;

    private static final String STEAM_API_URL = "https://store.steampowered.com/api/appdetails?appids={appId}";

    public SteamApiService(RestTemplate restTemplate, ObjectMapper objectMapper, GamesRepository gamesRepository) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.gamesRepository = gamesRepository;
    }

    public GameDTO getGameData(Long appId) {
        try {
            // First check if the game already exists in the database
            Optional<Games> existingGame = gamesRepository.findBySteamAppId(appId);
            if (existingGame.isPresent()) {
                System.out.println("Game with steam_app_id " + appId + " already exists in database.");
                GameDTO gameDTO = new GameDTO();
                gameDTO.convertToEntity(existingGame.get());
                return gameDTO;
            }

            // If game doesn't exist, fetch from Steam API
            String response = restTemplate.getForObject(STEAM_API_URL, String.class, appId);
            System.out.println("Steam API response: " + response);
            GameDTO gameDTO = parseGameData(response, appId);

            if (gameDTO != null && gameDTO.getName() != null) {
                try {
                    // Set default platform to PC and status to active (1)
                    gameDTO.setPlatform("PC");
                    gameDTO.setStatus(1);

                    Games gameEntity = gameDTO.toEntity();
                    gameEntity.setSteamAppId(appId);

                    // Double-check to prevent race conditions
                    if (gamesRepository.findBySteamAppId(appId).isEmpty()) {
                        System.out.println("Saving new game to database: " + gameDTO.getName());
                        Games savedGame = gamesRepository.save(gameEntity);
                        System.out.println("Game saved with game_id: " + savedGame.getGameId());
                        // Update the gameDTO with the saved game's ID
                        gameDTO.setGameId(savedGame.getGameId());
                    } else {
                        System.out.println("Game with steam_app_id " + appId + " already exists in database.");
                        // Get the existing game and convert to DTO
                        Optional<Games> game = gamesRepository.findBySteamAppId(appId);
                        if (game.isPresent()) {
                            gameDTO.convertToEntity(game.get());
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error saving to database: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.err.println("GameDTO is invalid or has no name.");
            }
            return gameDTO;
        } catch (Exception e) {
            System.err.println("Error fetching Steam API: " + e.getMessage());
            return null;
        }
    }

    private GameDTO parseGameData(String response, Long appId) {
        GameDTO gameDTO = new GameDTO();
        gameDTO.setGameId(appId);
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode appNode = root.path(String.valueOf(appId));
            if (appNode.path("success").asBoolean()) {
                JsonNode gameData = appNode.path("data");

                gameDTO.setName(gameData.path("name").asText());
                gameDTO.setDescription(gameData.path("short_description").asText());
                JsonNode genresNode = gameData.path("genres");
                if (genresNode.isArray()) {
                    String genres = StreamSupport.stream(genresNode.spliterator(), false)
                            .map(genre -> genre.path("description").asText())
                            .collect(Collectors.joining(", "));
                    gameDTO.setGenre(genres);
                } else {
                    gameDTO.setGenre("N/A");
                }
                gameDTO.setDeveloper(gameData.path("developers").get(0).asText());

                JsonNode movies = gameData.path("movies");
                if (movies.isArray() && movies.size() > 0) {
                    String videoUrl = movies.get(0).path("webm").path("480").asText();
                    gameDTO.setVideo(videoUrl != null ? videoUrl : movies.get(0).path("mp4").path("480").asText());
                }

                String releaseDateStr = gameData.path("release_date").path("date").asText();
                if (releaseDateStr != null && !releaseDateStr.isEmpty()) {
                    SimpleDateFormat formatter = new SimpleDateFormat("d MMM, yyyy", Locale.ENGLISH);
                    Date releaseDate = formatter.parse(releaseDateStr);
                    gameDTO.setReleaseDate(releaseDate);
                }
            } else {
                System.err.println("Steam API returned success=false for appId: " + appId);
            }
        } catch (Exception e) {
            System.err.println("Error parsing Steam API response: " + e.getMessage());
        }
        return gameDTO;
    }
}
