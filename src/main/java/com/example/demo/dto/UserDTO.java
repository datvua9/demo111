package com.example.demo.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class UserDTO {
    public UserDTO() {}

    private String username;
    private String password;
    private String email;
    private String role;
    private LocalDate createTime;
}
