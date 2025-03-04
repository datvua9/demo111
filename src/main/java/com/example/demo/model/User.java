package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

import javax.management.ObjectName;
import java.time.LocalDate;
import java.util.BitSet;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "user_new")
public class User  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long user_id;
    private String username;
    private String mail;
    private String password;
    private LocalDate created_at;
    private String role;

    public User() {}

}
