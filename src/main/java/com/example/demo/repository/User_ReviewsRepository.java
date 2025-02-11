package com.example.demo.repository;

import com.example.demo.model.Games;
import com.example.demo.model.User_Reviews;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface User_ReviewsRepository extends JpaRepository<User_Reviews, Long> {
    List<User_Reviews> findByGame(Games game); // Find reviews by game
}
