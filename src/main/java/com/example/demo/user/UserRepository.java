package com.example.demo.user;

import com.example.demo.user.data.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    // Thêm các phương thức mới để kiểm tra trùng lặp
    Optional<User> findByUsernameAndUserIdNot(String username, Long userId);
    Optional<User> findByEmailAndUserIdNot(String email, Long userId);

    // Nếu bạn cần tìm User bằng username và trả về Optional (khuyến khích)
    Optional<User> findOptionalByUsername(String username);
}