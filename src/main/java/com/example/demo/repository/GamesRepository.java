package com.example.demo.repository;

import com.example.demo.model.Games;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GamesRepository extends JpaRepository<Games, Long> {
    // Các phương thức truy vấn khác nếu cần
}