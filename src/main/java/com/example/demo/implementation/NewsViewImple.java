package com.example.demo.implementation;

import com.example.demo.IService.CommentServiceImple;
import com.example.demo.dto.NewsDTO;
import com.example.demo.model.Comment;
import com.example.demo.service.NewsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsViewImple {
    private final NewsService newsService;
    private final CommentServiceImple commentServiceImple;

    public NewsViewImple(NewsService newsService, CommentServiceImple commentServiceImple) {
        this.newsService = newsService;
        this.commentServiceImple = commentServiceImple;
    }
   public List<NewsDTO> getAllNews(){
        return newsService.getAllNewsList();
   }

   public NewsDTO getNewsDetails(Long id){
        return newsService.getNewsById(id);
   }

   public List<Comment> getComments(Long newsId){
        return commentServiceImple.findByNewsId(newsId);
   }


}
