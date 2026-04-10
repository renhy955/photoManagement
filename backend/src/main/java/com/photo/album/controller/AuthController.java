package com.photo.album.controller;

import com.photo.album.dto.LoginRequest;
import com.photo.album.dto.RegisterRequest;
import com.photo.album.entity.User;
import com.photo.album.service.AuthService;
import com.photo.album.utils.Result;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        Map<String, Object> result = authService.login(request);
        return Result.success("登录成功", result);
    }
    
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return Result.success("注册成功", null);
    }
    
    @GetMapping("/current")
    public Result<User> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        User user = authService.getCurrentUser(userDetails.getUsername());
        return Result.success(user);
    }
}
