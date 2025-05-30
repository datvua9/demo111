package com.example.demo.news;

import com.example.demo.news.data.NewsDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsRestImple {
    private NewsService newsService;
    
    public NewsRestImple(NewsService newsService) {
        this.newsService = newsService;
    }



    public ResponseEntity<NewsDTO> createNews(NewsDTO newsDTO) {
        NewsDTO creatednews = newsService.createNews(newsDTO);
        return new ResponseEntity<>(creatednews, HttpStatus.CREATED);
    }

    public ResponseEntity<NewsDTO> updatenews(Long id, NewsDTO newsDTO) {
        NewsDTO updatednews = newsService.updateNews(id, newsDTO);
        return updatednews != null ? new ResponseEntity<>(updatednews, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Void> deletenews(Long id) {
        newsService.deleteNews(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
