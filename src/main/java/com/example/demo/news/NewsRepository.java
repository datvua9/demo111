package com.example.demo.news;

import com.example.demo.news.data.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // <-- THÊM IMPORT
import org.springframework.stereotype.Repository;

// Bỏ List<News> findByTitle(String title); và List<News> findByTitleContainingIgnoreCase(String title);
// vì JpaSpecificationExecutor sẽ xử lý việc này một cách linh hoạt hơn.
// News findTopByOrderByCreatedAtDesc(); có thể giữ lại nếu bạn vẫn dùng nó ở đâu đó.

@Repository
public interface NewsRepository extends JpaRepository<News, Long>, JpaSpecificationExecutor<News> { // <-- THÊM JpaSpecificationExecutor
    News findTopByOrderByCreatedAtDesc(); // Giữ lại nếu cần cho getFeaturedNews
    // Không cần Page<News> findAll(Pageable pageable); vì JpaRepository đã có
}