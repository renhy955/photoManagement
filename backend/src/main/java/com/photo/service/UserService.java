package com.photo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.photo.common.Result;
import com.photo.dto.LoginRequest;
import com.photo.dto.RegisterRequest;
import com.photo.entity.User;
import com.photo.mapper.UserMapper;
import com.photo.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    public Result<Map<String, Object>> login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        String token = jwtUtil.generateToken(request.getUsername());
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, request.getUsername()));

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("nickname", user.getNickname());

        return Result.success(data);
    }

    public Result<?> register(RegisterRequest request) {
        User existingUser = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, request.getUsername()));
        if (existingUser != null) {
            return Result.error("用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setEnabled(true);

        userMapper.insert(user);
        return Result.success();
    }

    public User getByUsername(String username) {
        return userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
    }
}
