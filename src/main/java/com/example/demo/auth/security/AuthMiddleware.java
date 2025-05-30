package com.example.demo.auth.security;


import com.example.demo.user.UserService;
import com.example.demo.user.data.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class AuthMiddleware implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("{\"message\": \"Missing or invalid Authorization header\"}");
            return false;
        }

        String token = authHeader.substring(7); // Bỏ "Bearer " để lấy token
        try {
            // Giả sử UserService có method validateToken để kiểm tra token và trả về user
            User user = userService.validateToken(token);
            if (user == null) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("{\"message\": \"Invalid token\"}");
                return false;
            }

            // Lưu user vào request attribute để controller sử dụng
            request.setAttribute("authenticatedUser", user);
            return true;
        } catch (Exception e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("{\"message\": \"Token validation failed: " + e.getMessage() + "\"}");
            return false;
        }
    }
}