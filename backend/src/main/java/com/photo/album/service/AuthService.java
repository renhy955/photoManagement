package com.photo.album.service;

import com.photo.album.dto.LoginRequest;
import com.photo.album.dto.RegisterRequest;
import com.photo.album.entity.User;
import java.util.Map;

public interface AuthService {
    Map<String, Object> login(LoginRequest request);
    
    void register(RegisterRequest request);
    
    User getCurrentUser(String username);
}
