package com.example.demo.news;

import com.example.demo.news.data.NewsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface INewsService {
    NewsDTO getNewsById(Long id);
    Page<NewsDTO> getAllNews(Pageable pageable);
    NewsDTO createNews(NewsDTO newsDTO);
    NewsDTO updateNews(Long id, NewsDTO newsDTO);
    boolean deleteNews(Long id);

    NewsDTO getLatestNews();
}
