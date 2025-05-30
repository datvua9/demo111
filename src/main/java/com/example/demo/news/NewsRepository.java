package com.example.demo.news;

import com.example.demo.news.data.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    List<News> findByTitle(String title);
    Page<News> findAll(Pageable pageable);
    News findTopByOrderByCreatedAtDesc();
    List<News> findByTitleContainingIgnoreCase(String title);
}
