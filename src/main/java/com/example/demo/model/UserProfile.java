package com.example.demo.model;

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
}
