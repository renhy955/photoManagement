package com.photomanagement.controller;

import com.photomanagement.dto.*;
import com.photomanagement.security.UserDetailsImpl;
import com.photomanagement.service.UserService;
import com.photomanagement.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    private final JwtUtil jwtUtil;
    
    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        UserDTO user = userService.login(request);
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);
        
        return ApiResponse.success(result);
    }
    
    @PostMapping("/register")
    public ApiResponse<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        UserDTO user = userService.register(request);
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);
        
        return ApiResponse.success(result);
    }
    
    @GetMapping("/me")
    public ApiResponse<UserDTO> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UserDTO user = userService.getCurrentUser(userDetails.getId());
        return ApiResponse.success(user);
    }
}
