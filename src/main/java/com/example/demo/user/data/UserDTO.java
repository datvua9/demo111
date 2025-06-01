package com.example.demo.user.data;

import lombok.Data;

import java.time.LocalDate;

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
        this.user_id = user.getUserId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.created_at = user.getCreated_at();
    }
}
