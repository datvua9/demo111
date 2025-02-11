package com.example.demo.model;

import java.util.Date;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "games")
public class Games {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String game_id;
    private String game_name;

    @Column(columnDefinition = "TEXT")
    private String game_description;
    private String game_developer;
    private String game_genre;
    private String game_platform;
    private Date release_date;

    @Column(columnDefinition = "TEXT")
    private String game_update;

    @OneToMany(mappedBy = "game")
    private List<User_Reviews> reviews;
    public String getGame_id() {
        return game_id;
    }

    public void setGame_id(String game_id) {
        this.game_id = game_id;
    }

    public String getGame_name() {
        return game_name;
    }

    public void setGame_name(String game_name) {
        this.game_name = game_name;
    }

    public String getGame_description() {
        return game_description;
    }

    public void setGame_description(String game_description) {
        this.game_description = game_description;
    }

    public String getGame_developer() {
        return game_developer;
    }

    public void setGame_developer(String game_developer) {
        this.game_developer = game_developer;
    }

    public String getGame_genre() {
        return game_genre;
    }

    public void setGame_genre(String game_genre) {
        this.game_genre = game_genre;
    }

    public String getGame_platform() {
        return game_platform;
    }

    public void setGame_platform(String game_platform) {
        this.game_platform = game_platform;
    }

    public Date getRelease_date() {
        return release_date;
    }

    public void setRelease_date(Date release_date) {
        this.release_date = release_date;
    }

    public String getGame_update() {
        return game_update;
    }

    public void setGame_update(String game_update) {
        this.game_update = game_update;
    }
}
