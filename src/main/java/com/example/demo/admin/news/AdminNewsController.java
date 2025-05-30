package com.example.demo.admin.news;

import com.example.demo.news.data.NewsDTO;
import com.example.demo.news.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/news")
public class AdminNewsController {
    private final NewsService newsService;

    @Autowired
    public AdminNewsController( NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping("")
    public String adminNews(@RequestParam(defaultValue = "1") int page, Model model) {
        Pageable pageable = PageRequest.of(page, 4);
        Page<NewsDTO> newsPage = newsService.getAllNews(pageable);
        model.addAttribute("news", newsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", newsPage.getTotalPages());
        return "admin_news";
    }

    @PostMapping("/add")
    public String addNews(@ModelAttribute NewsDTO newsDTO) {
        newsService.createNews(newsDTO);
        return "redirect:/admin/news";
    }

    @GetMapping("/edit/{id}")
    public String showEditNewsForm(@PathVariable Long id, Model model) {
        NewsDTO newsDTO = newsService.getNewsById(id);
        if (newsDTO != null) {
            model.addAttribute("news", newsDTO);
            return "edit_news";
        }
        return "error/404";
    }

    @PostMapping("/edit/{id}")
    public String editNews(@PathVariable Long id, @ModelAttribute NewsDTO newsDTO) {
        newsService.updateNews(id, newsDTO);
        return "redirect:/admin/news";
    }

    @PostMapping("/delete/{id}")
    public String deleteNews(@PathVariable Long id) {
        newsService.deleteNews(id);
        return "redirect:/admin/news";
    }
}
