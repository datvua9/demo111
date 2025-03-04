package com.example.demo.repository;

import com.example.demo.dto.GameDTO;
import com.example.demo.model.Games;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface GamesRepository extends JpaRepository<Games, Long> {
    List<Games> findByGenre(String genre);
    List<Games> findByStatus(int status);
    List<Games> findByStatusNot(int status, Sort sort);

    Page<Games> findAll(Pageable pageable);
}