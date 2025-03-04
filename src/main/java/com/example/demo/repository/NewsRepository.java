package com.example.demo.repository;

import com.example.demo.model.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    List<News> findByTitle(String title);
    Page<News> findAll(Pageable pageable);
    News findTopByOrderByCreatedAtDesc( );
}
