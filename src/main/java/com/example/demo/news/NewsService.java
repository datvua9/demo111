package com.example.demo.news;

import com.example.demo.news.data.NewsDTO;
import com.example.demo.news.data.News;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // <-- THÊM IMPORT NÀY

@Service
public class NewsService implements INewsService {
    @Autowired
    private NewsRepository newsRepository;

    @Override
    @Transactional(readOnly = true)
    public NewsDTO getNewsById(Long id) {
        return newsRepository.findById(id)
                .map(NewsDTO::new)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public Page<NewsDTO> getAllNewsAdmin(Pageable pageable, String titleQuery, Date dateQuery) {
        Specification<News> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(titleQuery)) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("title")),
                        "%" + titleQuery.toLowerCase() + "%"
                ));
            }

            if (dateQuery != null) {
                // Bạn có thể cần điều chỉnh logic này cho phù hợp với cách bạn muốn lọc theo ngày
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.function("CONVERT", Date.class, criteriaBuilder.literal("DATE"), root.get("createdAt")),
                        dateQuery
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        return newsRepository.findAll(spec, pageable).map(NewsDTO::new);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NewsDTO> getAllNews(Pageable pageable) {
        return newsRepository.findAll(pageable).map(NewsDTO::new);
    }



    @Override
    @Transactional
    public NewsDTO createNews(NewsDTO newsDTO) {
        News news = new News();
        news.setTitle(newsDTO.getTitle());
        news.setContent(newsDTO.getContent());
        news.setImage(newsDTO.getImage());
        if (newsDTO.getCreated_at() != null) {
            news.setCreatedAt(newsDTO.getCreated_at());
        } else {
            news.setCreatedAt(new Date());
        }
        News savedNews = newsRepository.save(news);
        return new NewsDTO(savedNews);
    }

    @Override
    @Transactional
    public NewsDTO updateNews(Long id, NewsDTO newsDTO) {
        Optional<News> existingNewsOpt = newsRepository.findById(id);
        if (existingNewsOpt.isPresent()) {
            News existingNews = existingNewsOpt.get();
            existingNews.updateFromDTO(newsDTO); // Giả sử bạn đã có phương thức này trong News entity
            News updatedNews = newsRepository.save(existingNews);
            return new NewsDTO(updatedNews);
        }
        return null;
    }

    @Override
    @Transactional
    public boolean deleteNews(Long id) {
        if (newsRepository.existsById(id)) {
            newsRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override // <-- THÊM @Override VÌ PHƯƠNG THỨC NÀY CÓ TRONG INTERFACE
    @Transactional(readOnly = true)
    public NewsDTO getLatestNews() {
        News latestNews = newsRepository.findTopByOrderByCreatedAtDesc();
        return (latestNews != null) ? new NewsDTO(latestNews) : null;
    }
}