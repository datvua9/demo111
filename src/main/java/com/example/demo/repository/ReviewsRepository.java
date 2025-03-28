package com.example.demo.repository;

import com.example.demo.model.Games;
import com.example.demo.model.Reviews;
import com.example.demo.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewsRepository extends JpaRepository<Reviews, Long> {
    Page<Reviews> findAll(Pageable pageable);
    List<Reviews> findByGameId(Long gameId);
    Optional<Reviews> findByUserAndGameId(User user, Long gameId); // Sửa tên phương thức
}