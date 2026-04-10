package com.photomanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.photomanagement.dto.LoginRequest;
import com.photomanagement.dto.RegisterRequest;
import com.photomanagement.dto.UserDTO;
import com.photomanagement.entity.User;

public interface UserService extends IService<User> {
    
    UserDTO login(LoginRequest request);
    
    UserDTO register(RegisterRequest request);
    
    UserDTO getCurrentUser(Long userId);
}
