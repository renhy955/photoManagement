package com.photomanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.photomanagement.dto.LoginRequest;
import com.photomanagement.dto.RegisterRequest;
import com.photomanagement.dto.UserDTO;
import com.photomanagement.entity.User;
import com.photomanagement.mapper.UserMapper;
import com.photomanagement.service.UserService;
import com.photomanagement.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    @Override
    public UserDTO login(LoginRequest request) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());
        User user = getOne(wrapper);
        
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }
        
        return convertToDTO(user);
    }
    
    @Override
    public UserDTO register(RegisterRequest request) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());
        if (getOne(wrapper) != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setEmail(request.getEmail());
        user.setStatus(1);
        
        save(user);
        return convertToDTO(user);
    }
    
    @Override
    public UserDTO getCurrentUser(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return convertToDTO(user);
    }
    
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(user, dto);
        return dto;
    }
}
