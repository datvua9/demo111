package com.example.demo.news.controller;

import com.example.demo.news.NewsService;
import com.example.demo.news.data.NewsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/news")
public class NewsApiController {

    @Autowired
    private NewsService newsService;

    @GetMapping("/featured")
    public ResponseEntity<NewsDTO> getFeaturedNews() {
        NewsDTO latestNews = newsService.getLatestNews();
        if (latestNews != null) {
            return new ResponseEntity<>(latestNews, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping
    public ResponseEntity<Page<NewsDTO>> getAllNews(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "6") int perPage) {
        Pageable pageable = PageRequest.of(page - 1, perPage); // Spring uses 0-based page index
        Page<NewsDTO> newsPage = newsService.getAllNews(pageable);
        return new ResponseEntity<>(newsPage, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewsDTO> getNewsById(@PathVariable Long id) {
        NewsDTO newsDTO = newsService.getNewsById(id);
        if (newsDTO != null) {
            return new ResponseEntity<>(newsDTO, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<NewsDTO> createNews(@RequestBody NewsDTO newsDTO) {
        if (newsDTO == null || newsDTO.getTitle() == null || newsDTO.getContent() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        NewsDTO createdNews = newsService.createNews(newsDTO);
        return new ResponseEntity<>(createdNews, HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<NewsDTO> updateNews(@PathVariable Long id, @RequestBody NewsDTO newsDTO) {
        if (newsDTO == null || newsDTO.getTitle() == null || newsDTO.getContent() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        NewsDTO updatedNews = newsService.updateNews(id, newsDTO);
        if (updatedNews != null) {
            return new ResponseEntity<>(updatedNews, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNews(@PathVariable Long id) {
        boolean deleted = newsService.deleteNews(id);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}