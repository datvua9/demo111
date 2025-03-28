package com.example.demo.implementation;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class passRegenImple {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "111111";
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println(encodedPassword);
    }
}
