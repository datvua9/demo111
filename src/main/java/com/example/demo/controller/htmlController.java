package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class htmlController {

    private static final String VALID_USERNAME = "user";
    private static final String VALID_PASSWORD = "password"; // Không khuyến nghị: Chỉ dùng cho ví dụ

    @GetMapping("/game_home")
    public String gamehome() {
        return "game_home";
    }

    @GetMapping("/login")
    public String signIn() {
        return "login";
    }
    @GetMapping("/home")
    public String home() {
        return "home";
    }

}