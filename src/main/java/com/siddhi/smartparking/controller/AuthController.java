package com.siddhi.smartparking.controller;

import com.siddhi.smartparking.dto.LoginRequest;
import com.siddhi.smartparking.dto.RegisterRequest;
import com.siddhi.smartparking.entity.User;
import com.siddhi.smartparking.security.JwtUtil;
import com.siddhi.smartparking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

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

        String response =
                userService.login(request);

        // If login successful
        if (response.equals("Login successful")) {

            return jwtUtil.generateToken(
                    request.getUsername()
            );
        }

        return response;
    }
}