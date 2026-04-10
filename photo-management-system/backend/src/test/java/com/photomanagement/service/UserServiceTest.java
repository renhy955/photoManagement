package com.photomanagement.service;

import com.photomanagement.dto.LoginRequest;
import com.photomanagement.dto.RegisterRequest;
import com.photomanagement.dto.UserDTO;
import com.photomanagement.entity.User;
import com.photomanagement.mapper.UserMapper;
import com.photomanagement.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setNickname("测试用户");
        testUser.setEmail("test@example.com");
        testUser.setStatus(1);
    }

    @Test
    void testLogin_Success() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        when(userMapper.selectOne(any())).thenReturn(testUser);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        UserDTO result = userService.login(request);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("测试用户", result.getNickname());
    }

    @Test
    void testLogin_WrongPassword() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("wrongPassword");

        when(userMapper.selectOne(any())).thenReturn(testUser);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> userService.login(request));
    }

    @Test
    void testLogin_UserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setUsername("nonexistent");
        request.setPassword("password123");

        when(userMapper.selectOne(any())).thenReturn(null);

        assertThrows(RuntimeException.class, () -> userService.login(request));
    }

    @Test
    void testLogin_UserDisabled() {
        testUser.setStatus(0);
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        when(userMapper.selectOne(any())).thenReturn(testUser);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> userService.login(request));
    }

    @Test
    void testRegister_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("password123");
        request.setNickname("新用户");
        request.setEmail("new@example.com");

        when(userMapper.selectOne(any())).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userMapper.insert(any())).thenReturn(1);

        UserDTO result = userService.register(request);

        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        verify(userMapper).insert(any(User.class));
    }

    @Test
    void testRegister_UsernameExists() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existinguser");
        request.setPassword("password123");

        when(userMapper.selectOne(any())).thenReturn(testUser);

        assertThrows(RuntimeException.class, () -> userService.register(request));
    }

    @Test
    void testGetCurrentUser_Success() {
        when(userMapper.selectById(1L)).thenReturn(testUser);

        UserDTO result = userService.getCurrentUser(1L);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void testGetCurrentUser_UserNotFound() {
        when(userMapper.selectById(999L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> userService.getCurrentUser(999L));
    }
}
