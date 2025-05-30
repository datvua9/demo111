package com.example.demo.news.data;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;

@Data
public class NewsDTO {
    private Long newsId;
    private String title;
    private String content; // Có thể bạn muốn một trường "summary" cho danh sách admin
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // Thêm giờ phút giây nếu createdAt lưu cả thời gian
    private Date created_at;
    private String image;

    public NewsDTO() {}

    // Constructor hiện tại của bạn (có thể giữ lại nếu cần)
    public NewsDTO(Long newsId, String title, String content, String image) {
        this.newsId = newsId;
        this.title = title;
        this.content = content;
        this.image = image;
    }

    // CONSTRUCTOR MỚI TỪ ENTITY (quan trọng)
    public NewsDTO(News newsEntity) {
        if (newsEntity != null) {
            this.newsId = newsEntity.getNewsId();
            this.title = newsEntity.getTitle();
            this.content = newsEntity.getContent(); // Xem xét chỉ lấy tóm tắt ở đây nếu nội dung quá dài
            this.created_at = newsEntity.getCreatedAt();
            this.image = newsEntity.getImage();
        }
    }

    public News toEntity() {
        News news = new News();
        news.setNewsId(this.newsId); // Nếu là tạo mới, ID thường do DB tự sinh, nên không set ở đây.
        // Nếu là cập nhật, ID sẽ được lấy từ path variable.
        news.setTitle(this.title);
        news.setContent(this.content);
        news.setImage(this.image);
        news.setCreatedAt(this.created_at); // Khi tạo mới, ngày tạo thường là now() ở backend.
        return news;
    }

    // Bỏ phương thức convertToEntity(News news) vì đã có constructor NewsDTO(News newsEntity)
}