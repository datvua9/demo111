package com.example.demo.model;
import com.example.demo.dto.GameDTO;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;
@Data
@Entity
@Table(name = "games")
public class Games {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_id")
    private Long gameId;

    @Column(name = "name")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "genre")
    private String genre;

    @Column(name = "platform")
    private String platform;

    @Temporal(TemporalType.DATE)
    @Column(name = "release_date")
    private Date releaseDate;

    @Column(name = "game_update", columnDefinition = "TEXT")
    private String gameUpdate;

    @Column(name = "game_developer")
    private String gameDeveloper;


    @Column(name = "image")
    private String image;

    @Column(name = "video")
    private String video;

    @Column
    private int status;

    @OneToMany
    @JoinColumn(name = "game_id")
    private List<Reviews> reviews;

    public Games() {}

    public void convertToDTO(GameDTO dto) {
      this.gameId = dto.getGameId();
      this.name = dto.getName();
      this.description = dto.getDescription();
      this.genre = dto.getGenre();
      this.platform = dto.getPlatform();
      this.releaseDate = dto.getReleaseDate();
      this.gameDeveloper = dto.getDeveloper();
      this.image = dto.getImage();
      this.video = dto.getVideo();
      this.status = dto.getStatus();
    }
    private GameDTO convertToDTO(Games game) {
        GameDTO dto = new GameDTO();
        dto.setGameId(game.getGameId());
        dto.setName(game.getName());
        dto.setImage(game.getImage());
        dto.setGenre(game.getGenre());
        return dto;
    }
}