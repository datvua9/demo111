package com.example.demo.news.controller;

import com.example.demo.news.INewsService;
import com.example.demo.news.data.NewsDTO;
import com.example.demo.news.NewsViewImple;
import com.example.demo.comment.data.Comment;
// import com.example.demo.news.NewsService; // Không thấy NewsService được sử dụng trực tiếp, có thể xóa import
import com.example.demo.user.UserProfileRepository;
import com.example.demo.user.data.User;
import com.example.demo.user.data.UserProfile;
import com.example.demo.user.data.UserProfileDTO; // Có thể cần DTO nếu bạn muốn hiển thị thông tin profile chi tiết hơn
import org.modelmapper.ModelMapper; // Nếu bạn quyết định dùng DTO cho UserProfile trong view
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional; // Import Optional

@Controller
@RequestMapping("/news")
public class NewsController {
    private final INewsService newsService;
    private final NewsViewImple newsViewImple;
    private final UserProfileRepository userProfileRepository;
    private final ModelMapper modelMapper; // Thêm ModelMapper

    @Value("${app.base-url:http://localhost:8081}") // Để xử lý URL avatar nếu cần
    private String appBaseUrl;

    @Autowired
    public NewsController(NewsViewImple newsViewImple,
                          INewsService newsService,
                          UserProfileRepository userProfileRepository,
                          ModelMapper modelMapper) { // Inject ModelMapper
        this.newsViewImple = newsViewImple;
        this.newsService = newsService;
        this.userProfileRepository = userProfileRepository;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/{id}")
    public String newsDetail(@PathVariable Long id, Model model) {
        NewsDTO newsDTO = newsViewImple.getNewsDetails(id); // Giả sử getNewsDetails đã được cập nhật để trả về DTO

        if (newsDTO == null) {
            return "error/404"; // Hoặc một trang lỗi tùy chỉnh
        }



        List<String> paragraphs = Arrays.asList(newsDTO.getContent().split("\n\n|\r\n\r\n")); // Tách đoạn tốt hơn
        model.addAttribute("news", newsDTO);
        model.addAttribute("paragraphs", paragraphs);

        // Lấy và xử lý comments
        // Giả sử getComments trả về List<Comment> entities
        // Nếu bạn muốn hiển thị thông tin chi tiết hơn, nên chuyển Comment sang CommentDTO
        List<Comment> comments = newsViewImple.getComments(id);
        model.addAttribute("totalComments", comments.size()); // Sửa tên attribute cho nhất quán
        model.addAttribute("comments", comments); // Truyền entities, Thymeleaf sẽ truy cập qua getter

        // Tạo map UserProfileDTOs để tránh truyền entity UserProfile vào view trực tiếp
        // và để xử lý avatar URL
        Map<Long, UserProfileDTO> userProfileDTOs = new HashMap<>();
        for (Comment comment : comments) {
            User user = comment.getUser();
            if (user != null && !userProfileDTOs.containsKey(user.getUserId())) { // Chỉ lấy profile một lần cho mỗi user
                Optional<UserProfile> userProfileOptional = userProfileRepository.findByUser(user);
                userProfileOptional.ifPresent(profile -> {
                    UserProfileDTO profileDTO = modelMapper.map(profile, UserProfileDTO.class);
                    // Xử lý avatar URL
                    if (profileDTO.getAvatar() != null && !profileDTO.getAvatar().startsWith("http")) {
                        if(!profileDTO.getAvatar().startsWith("/")){
                            profileDTO.setAvatar(appBaseUrl + "/" + profileDTO.getAvatar());
                        } else {
                            profileDTO.setAvatar(appBaseUrl + profileDTO.getAvatar());
                        }
                    } else if (profileDTO.getAvatar() == null || profileDTO.getAvatar().isEmpty()){
                        profileDTO.setAvatar(appBaseUrl + "/img/alec.png"); // Avatar mặc định nếu không có
                    }
                    userProfileDTOs.put(user.getUserId(), profileDTO);
                });
                // Nếu user không có profile, có thể thêm một DTO mặc định vào map
                if (userProfileOptional.isEmpty()) {
                    UserProfileDTO defaultProfileDTO = new UserProfileDTO();
                    defaultProfileDTO.setAvatar(appBaseUrl + "/img/alec.png"); // Avatar mặc định
                    // bạn có thể set các trường khác cho defaultProfileDTO nếu muốn
                    userProfileDTOs.put(user.getUserId(), defaultProfileDTO);
                }
            }
        }
        model.addAttribute("userProfileDTOs", userProfileDTOs); // Truyền map DTOs
        // model.addAttribute("defaultAvatar", appBaseUrl + "/img/alec.png"); // Không cần nếu DTO đã có avatar

        return "game_news"; // Tên template Thymeleaf
    }
}