package com.example.demo.news;

import com.example.demo.comment.CommentService;
import com.example.demo.news.data.NewsDTO;
import com.example.demo.comment.data.Comment;
import com.example.demo.news.data.News;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NewsService implements INewsService{
    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private CommentService commentService;

    @Override
    public NewsDTO getNewsById(Long id) {
        return newsRepository.findById(id)
                .map(news -> {
                    NewsDTO dto = new NewsDTO();
                    dto.convertToEntity(news);
                    return dto;
                }).orElse(null);
    }
    @Override   
    public Page<NewsDTO> getAllNews(Pageable pageable) {
        return newsRepository.findAll(pageable)
                .map(news -> {
                    NewsDTO dto = new NewsDTO();
                    dto.convertToEntity(news);
                    return dto;
                });
    }
    @Override
    public List<NewsDTO> getAllNewsList() {
        return newsRepository.findAll().stream()
                .map(news -> {
                    NewsDTO dto = new NewsDTO();
                    dto.convertToEntity(news);
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public NewsDTO createNews(NewsDTO newsDTO) {
        News news = new News();
        news.convertToDTO(newsDTO);
        News savedNews = newsRepository.save(news);
        NewsDTO savedDTO = new NewsDTO();
        savedDTO.convertToEntity(savedNews);
        return savedDTO;
    }
    @Override
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
    @Override
    public boolean deleteNews(Long id) {
        if(newsRepository.existsById(id)) {
            newsRepository.deleteById(id);
            return true;
        }
        return false;
    }


    public NewsDTO getLatestNews() {
        News latestNews = newsRepository.findTopByOrderByCreatedAtDesc();
        if (latestNews != null) {
            NewsDTO dto = new NewsDTO();
            dto.convertToEntity(latestNews);
            return dto;
        }
        return null;
    }

   
}
