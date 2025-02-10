package com.example.demo.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller

public class htmlController {
    @GetMapping("/home")
    public String htmlhome() {
        return "home"; // Trả về tên tệp HTML (không cần .html)
    }
}
