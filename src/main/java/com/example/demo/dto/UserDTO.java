package com.example.demo.dto;

import com.example.demo.model.User;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class UserDTO {
    public UserDTO() {}
    private Long user_id;
    private String username;
    private String password;
    private String confirmPassword;
    private String email;
    private LocalDate created_at;

    public void convertToEntity(User user) {
        this.user_id = user.getUser_id();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.created_at = user.getCreated_at();
    }
}
