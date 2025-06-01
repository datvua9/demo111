package com.example.demo.user.data;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name ="user_profiles")
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private long profileId;

    private Date birthdate;
    private String hobbies;
    private String gender;
    private String bio;

    @Column(name="avatar_url")
    private String avatar;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public UserProfile() {}

    public String getAvatar() {
        if (avatar == null || avatar.isEmpty()) {
            return null;
        }
        String normalizedAvatar = avatar;
        if (normalizedAvatar.startsWith("//")) {
            normalizedAvatar = "http:" + normalizedAvatar;
        }
        normalizedAvatar = normalizedAvatar.replaceAll("(?<!:)//+", "/");
        if (!normalizedAvatar.startsWith("http://") && !normalizedAvatar.startsWith("https://")) {
            return "http://localhost:8081" + (normalizedAvatar.startsWith("/") ? "" : "/") + normalizedAvatar;
        }
        String baseURL = "http://localhost:8081";
        if (normalizedAvatar.contains(baseURL + baseURL)) {
            normalizedAvatar = normalizedAvatar.replace(baseURL + baseURL, baseURL);
        }
        return normalizedAvatar;
    }
}
