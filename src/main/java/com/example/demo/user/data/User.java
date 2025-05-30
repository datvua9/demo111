package com.example.demo.user.data;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "user_new")
public class User  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long user_id;
    private String username;
    private String email;
    private String password;
    private LocalDate created_at;
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
    public User() {}

}
