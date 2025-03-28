package com.example.demo.service;

import com.example.demo.dto.GameDTO;
import com.example.demo.dto.NewsDTO;
import com.example.demo.model.Comment;
import com.example.demo.model.Games;
import com.example.demo.model.News;
import com.example.demo.model.Reviews;
import com.example.demo.repository.NewsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NewsService {
    private NewsRepository newsRepository;

    public NewsService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    public NewsDTO getNewsById(Long id) {
       News news = newsRepository.findById(id).orElse(null);
       if( news != null ) {
           return convertToDTO(news);
       }
       return null;
    }

    public List<NewsDTO> getRelatedNews(String title) {
        List<News> otherNews = newsRepository.findByTitle(title);
        return otherNews.stream()
                .map(news -> new NewsDTO(news.getNewsId(),news.getTitle(),news.getContent(),news.getImage()))
                .collect(Collectors.toList());
    }

    public Page<NewsDTO> getAllNews(Pageable pageable) {
        Page<News> newsPage = newsRepository.findAll(pageable);
        return newsPage.map(this::convertToDTO);
    }

    public List<NewsDTO> getAllNewsList() {
        List<News> newsList = newsRepository.findAll();
        return newsList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public NewsDTO createNews(NewsDTO newsDTO) {
        News news = convertToEntity(newsDTO);
        News savedNews = newsRepository.save(news);
        return convertToDTO(savedNews);
    }

    public NewsDTO updateNews(Long id, NewsDTO newsDTO) {
        News existingNews = newsRepository.findById(id).orElse(null);

        if (existingNews != null) {
            List<Comment> existingComment = existingNews.getComments();
            existingNews.convertToDTO(newsDTO);
            existingNews.setComments(existingComment);
            News updatedNews = newsRepository.save(existingNews);

            NewsDTO updatedDTO = new NewsDTO();
            updatedDTO.convertToEntity(updatedNews);
            return updatedDTO;
        }
        return null;
    }

    public void deleteNews(Long id) {
        newsRepository.deleteById(id);
    }

    public NewsDTO getLatestNews() {
        News news = newsRepository.findTopByOrderByCreatedAtDesc();
        return news != null ? convertToDTO(news) : null;
    }

    private NewsDTO convertToDTO(News news) {
        NewsDTO newsDTO = new NewsDTO();
        newsDTO.setNewsId(news.getNewsId());
        newsDTO.setTitle(news.getTitle());
        newsDTO.setContent(news.getContent());
        newsDTO.setImage(news.getImage());
        newsDTO.setCreated_at(news.getCreatedAt());
        return newsDTO;
    }

    private News convertToEntity(NewsDTO newsDTO) {
        News news = new News();
        news.setNewsId(newsDTO.getNewsId());
        news.setTitle(newsDTO.getTitle());
        news.setContent(newsDTO.getContent());
        news.setImage(newsDTO.getImage());
        news.setCreatedAt(newsDTO.getCreated_at());
        return news;
    }
}
