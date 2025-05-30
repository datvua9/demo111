package com.example.demo.auth.data;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
