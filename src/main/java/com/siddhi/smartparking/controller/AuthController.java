package com.siddhi.smartparking.controller;

import com.siddhi.smartparking.dto.LoginRequest;
import com.siddhi.smartparking.dto.RegisterRequest;
import com.siddhi.smartparking.entity.User;
import com.siddhi.smartparking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import com.siddhi.smartparking.service.RedisService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisService redisService;

    @PostMapping("/register")
    public User register(
            @RequestBody RegisterRequest request
    ) {
        return userService.register(request);
    }

    @PostMapping("/login")
    public String login(
            @RequestBody LoginRequest request
    ) {
        return userService.login(request);
    }

    @PostMapping("/logout")
    public String logout(
            HttpServletRequest request
    ) {

        String authHeader =
                request.getHeader("Authorization");

        if (authHeader != null &&
                authHeader.startsWith("Bearer ")) {

            String token =
                    authHeader.substring(7);

            redisService.blacklistToken(token);

            return "Logged out successfully";
        }

        return "Token not found";
    }
}