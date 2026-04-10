package com.photomanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.photomanagement.dto.LoginRequest;
import com.photomanagement.dto.RegisterRequest;
import com.photomanagement.dto.UserDTO;
import com.photomanagement.security.UserDetailsImpl;
import com.photomanagement.service.UserService;
import com.photomanagement.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtil jwtUtil;

    private UserDTO testUserDTO;
    private UserDetailsImpl testUserDetails;

    @BeforeEach
    void setUp() {
        testUserDTO = new UserDTO();
        testUserDTO.setId(1L);
        testUserDTO.setUsername("testuser");
        testUserDTO.setNickname("测试用户");
        testUserDTO.setEmail("test@example.com");

        testUserDetails = new UserDetailsImpl(
            1L,
            "testuser",
            "测试用户",
            "encodedPassword",
            Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER"))
        );

        Authentication auth = new UsernamePasswordAuthenticationToken(
            testUserDetails, null, testUserDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void testLogin_Success() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        when(userService.login(any())).thenReturn(testUserDTO);
        when(jwtUtil.generateToken(anyLong(), anyString())).thenReturn("mock-jwt-token");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").value("mock-jwt-token"))
                .andExpect(jsonPath("$.data.user.username").value("testuser"));
    }

    @Test
    void testLogin_InvalidRequest() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegister_Success() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("password123");
        request.setNickname("新用户");
        request.setEmail("new@example.com");

        when(userService.register(any())).thenReturn(testUserDTO);
        when(jwtUtil.generateToken(anyLong(), anyString())).thenReturn("mock-jwt-token");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").exists());
    }

    @Test
    void testGetCurrentUser() throws Exception {
        when(userService.getCurrentUser(1L)).thenReturn(testUserDTO);

        mockMvc.perform(get("/api/auth/me")
                .principal(new UsernamePasswordAuthenticationToken(
                    testUserDetails, null, testUserDetails.getAuthorities())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.nickname").value("测试用户"));
    }
}
