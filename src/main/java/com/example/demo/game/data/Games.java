package com.example.demo.game.data;

import com.example.demo.reviews.data.Reviews;
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

    @Column(name = "steam_app_id")
    private Long steamAppId;

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

    @OneToMany(mappedBy = "game", fetch = FetchType.LAZY) // Giữ LAZY để tối ưu hiệu suất
    private List<Reviews> reviews;

    public Games() {}

    public void convertToDTO(GameDTO dto) {
        this.steamAppId = dto.getGameId();
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

    public String getImage() {
        if (image == null || image.isEmpty()) {
            return null;
        }
        String normalizedImage = image.replaceAll("(?<!:)//+", "/");
        if (!normalizedImage.startsWith("http://") && !normalizedImage.startsWith("https://")) {
            return "http://localhost:8081" + (normalizedImage.startsWith("/") ? "" : "/") + normalizedImage;
        }
        String baseURL = "http://localhost:8081";
        if (normalizedImage.contains(baseURL + baseURL)) {
            normalizedImage = normalizedImage.replace(baseURL + baseURL, baseURL);
        }
        return normalizedImage;
    }
}