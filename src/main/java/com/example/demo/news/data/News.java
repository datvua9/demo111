package com.example.demo.news.data;

import com.example.demo.comment.data.Comment;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "news")
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "news_id")
    private Long newsId;

    private String title;
    @Column(columnDefinition = "TEXT") // Đảm bảo content có thể dài
    private String content;

    @Temporal(TemporalType.TIMESTAMP) // Nếu muốn lưu cả giờ phút giây
    @Column(name = "created_at", nullable = false, updatable = false) // Thường không cho update ngày tạo
    private Date createdAt;

    private String image; // Đường dẫn tới ảnh

    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true) // Thêm mappedBy nếu Comment có trường News news;
    // @JoinColumn(name = "news_id") // Dùng JoinColumn nếu Comment không có mappedBy
    private List<Comment> comments;

    public News() {
        this.createdAt = new Date(); // Mặc định ngày tạo là hiện tại khi tạo mới object
    }

    // Đổi tên phương thức này cho rõ ràng
    public void updateFromDTO(NewsDTO dto) {
        // Không nên cho phép cập nhật newsId từ DTO
        this.title = dto.getTitle();
        this.content = dto.getContent();
        if (dto.getCreated_at() != null) { // Cho phép admin sửa ngày đăng nếu muốn
            this.createdAt = dto.getCreated_at();
        }
        this.image = dto.getImage();
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = new Date();
        }
    }

    @Override
    public String toString() {
        return "News{" +
                "newsId=" + newsId +
                ", title='" + title + '\'' +
                ", createdAt=" + createdAt +
                ", image='" + image + '\'' +
                ", commentsCount=" + (comments != null && org.hibernate.Hibernate.isInitialized(comments) ? comments.size() : "not_initialized") +
                '}';
    }
}