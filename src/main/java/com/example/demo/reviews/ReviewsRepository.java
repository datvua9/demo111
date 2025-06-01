package com.example.demo.reviews;

import com.example.demo.reviews.data.Reviews;
import com.example.demo.user.data.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewsRepository extends JpaRepository<Reviews, Long> {
    Page<Reviews> findAll(Pageable pageable);
    List<Reviews> findByGame_GameId(Long gameId); // Sử dụng game.gameId
    Optional<Reviews> findByUserAndGame_GameId(User user, Long gameId);
}