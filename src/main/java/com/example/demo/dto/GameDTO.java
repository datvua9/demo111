package com.example.demo.dto;

import com.example.demo.model.Games;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
public class GameDTO {
    private Long gameId;
    private String name;
    private String description;
    private String genre;
    private String platform;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date releaseDate;
    private String developer;
    private String image;
    private String video;
    private int status;
    private float rating;
    private List<ReviewDTO> reviews;

    public GameDTO() {}

    public GameDTO(Long gameId, String name, String image, String genre) {
        this.gameId = gameId;
        this.name = name;
        this.image = image;
        this.genre = genre;
    }

    public Games toEntity() {
        Games game = new Games();
        game.setSteamAppId(this.gameId); // Gán steam_app_id từ gameId của DTO
        game.setName(this.name);
        game.setDescription(this.description);
        game.setGenre(this.genre);
        game.setPlatform(this.platform);
        game.setReleaseDate(this.releaseDate);
        game.setGameDeveloper(this.developer);
        game.setImage(this.image);
        game.setVideo(this.video);
        game.setStatus(this.status);
        return game;
    }

    public void convertToEntity(Games games) {
        this.gameId = games.getGameId();
        this.name = games.getName();
        this.description = games.getDescription();
        this.genre = games.getGenre();
        this.platform = games.getPlatform();
        this.releaseDate = games.getReleaseDate();
        this.developer = games.getGameDeveloper();
        this.image = games.getImage();
        this.video = games.getVideo();
        this.status = games.getStatus();
    }
}