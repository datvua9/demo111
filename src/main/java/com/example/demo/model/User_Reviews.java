package com.example.demo.model;

import jakarta.persistence.*;
import java.math.BigDecimal; // Use BigDecimal for DECIMAL

@Entity
public class User_Reviews {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long review_id;

    @ManyToOne
    @JoinColumn(name = "user_id") // Correctly map foreign key
    private User user;

    @ManyToOne
    @JoinColumn(name = "game_id") // Correctly map foreign key
    private Games game;

    private BigDecimal rating; // Use BigDecimal
    private String comment;
    private String platform;



}