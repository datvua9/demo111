package com.example.demo.controller;

import com.example.demo.game.data.GameDTO;
import com.example.demo.service.SteamApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/game")
public class SteamApiController {

    @Autowired
    private SteamApiService steamApiService;

    @GetMapping("/game/{appId}")
    public GameDTO getGame(@PathVariable Long appId) {
        return steamApiService.getGameData(appId);
    }
}