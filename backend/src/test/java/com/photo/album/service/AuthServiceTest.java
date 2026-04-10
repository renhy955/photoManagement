package com.photo.album.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.photo.album.dto.LoginRequest;
import com.photo.album.dto.RegisterRequest;
import com.photo.album.entity.User;
import com.photo.album.mapper.UserMapper;
import com.photo.album.security.CustomUserDetailsService;
import com.photo.album.security.JwtTokenUtil;
import com.photo.album.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setEmail("test@example.com");
        testUser.setStatus(1);

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("password123");
        registerRequest.setEmail("test@example.com");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");
    }

    @Test
    void testRegister_Success() {
        when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userMapper.insert(any(User.class))).thenReturn(1);

        assertDoesNotThrow(() -> authService.register(registerRequest));
        
        verify(userMapper, times(1)).insert(any(User.class));
        verify(passwordEncoder, times(1)).encode(registerRequest.getPassword());
    }

    @Test
    void testRegister_UsernameExists() {
        when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        assertThrows(IllegalArgumentException.class, () -> {
            authService.register(registerRequest);
        });
        
        verify(userMapper, never()).insert(any(User.class));
    }

    @Test
    void testLogin_Success() {
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtTokenUtil.generateToken(any(UserDetails.class))).thenReturn("test-token");
        when(userDetailsService.getUserByUsername(anyString())).thenReturn(testUser);

        Map<String, Object> result = authService.login(loginRequest);
        
        assertNotNull(result);
        assertEquals("test-token", result.get("token"));
        assertEquals(testUser, result.get("user"));
        
        verify(authenticationManager, times(1)).authenticate(any());
        verify(jwtTokenUtil, times(1)).generateToken(any());
    }

    @Test
    void testGetCurrentUser() {
        when(userDetailsService.getUserByUsername(anyString())).thenReturn(testUser);

        User result = authService.getCurrentUser("testuser");
        
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }
}
