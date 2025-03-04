package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Criteria_Scores")
public class Criteria_Scores {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
}
