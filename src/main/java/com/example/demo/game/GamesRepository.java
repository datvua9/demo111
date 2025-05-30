package com.example.demo.game;

import com.example.demo.game.data.Games;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface GamesRepository extends JpaRepository<Games, Long>, JpaSpecificationExecutor {
    List<Games> findByGenre(String genre);
    List<Games> findByStatus(int status);
    List<Games> findByStatusNot(int status, Sort sort);
    Page<Games> findAll(Pageable pageable);
    Optional<Games> findByName(String name);
    Optional<Games> findBySteamAppId(Long steamAppId);
    List<Games> findByNameContainingIgnoreCase(String name);
    List<Games> findByReleaseDateAfterAndStatusNot(LocalDate date, int status, Sort sort);
}