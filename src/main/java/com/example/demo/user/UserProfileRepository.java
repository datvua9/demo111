package com.example.demo.user;

import com.example.demo.user.data.User;
import com.example.demo.user.data.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    UserProfile findByUser(User user);
}