package com.photo.album.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.photo.album.dto.AlbumRequest;
import com.photo.album.entity.Album;
import com.photo.album.service.AlbumService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AlbumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AlbumService albumService;

    private Album testAlbum;
    private AlbumRequest albumRequest;

    @BeforeEach
    void setUp() {
        testAlbum = new Album();
        testAlbum.setId(1L);
        testAlbum.setUserId(1L);
        testAlbum.setName("Test Album");
        testAlbum.setDescription("Test Description");
        testAlbum.setPhotoCount(5);

        albumRequest = new AlbumRequest();
        albumRequest.setName("New Album");
        albumRequest.setDescription("New Description");
    }

    @Test
    @WithMockUser(username = "testuser")
    void testCreateAlbum_Success() throws Exception {
        when(albumService.createAlbum(anyLong(), any(AlbumRequest.class))).thenReturn(testAlbum);

        mockMvc.perform(post("/api/albums")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(albumRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("创建成功"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testUpdateAlbum_Success() throws Exception {
        when(albumService.updateAlbum(anyLong(), anyLong(), any(AlbumRequest.class))).thenReturn(testAlbum);

        mockMvc.perform(put("/api/albums/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(albumRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("更新成功"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testDeleteAlbum_Success() throws Exception {
        mockMvc.perform(delete("/api/albums/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("删除成功"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetAlbum_Success() throws Exception {
        when(albumService.getAlbumById(anyLong(), anyLong())).thenReturn(testAlbum);

        mockMvc.perform(get("/api/albums/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("Test Album"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetUserAlbums_Success() throws Exception {
        Page<Album> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(testAlbum));
        
        when(albumService.getUserAlbums(anyLong(), anyInt(), anyInt())).thenReturn(page);

        mockMvc.perform(get("/api/albums")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records").isArray());
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetAllUserAlbums_Success() throws Exception {
        List<Album> albums = Arrays.asList(testAlbum);
        
        when(albumService.getAllUserAlbums(anyLong())).thenReturn(albums);

        mockMvc.perform(get("/api/albums/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }
}
