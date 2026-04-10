package com.photo.album.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.photo.album.dto.AlbumRequest;
import com.photo.album.entity.Album;
import com.photo.album.entity.Photo;
import com.photo.album.mapper.AlbumMapper;
import com.photo.album.mapper.PhotoMapper;
import com.photo.album.service.impl.AlbumServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {

    @Mock
    private AlbumMapper albumMapper;

    @Mock
    private PhotoMapper photoMapper;

    @InjectMocks
    private AlbumServiceImpl albumService;

    private Album testAlbum;
    private AlbumRequest albumRequest;
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        testAlbum = new Album();
        testAlbum.setId(1L);
        testAlbum.setUserId(userId);
        testAlbum.setName("Test Album");
        testAlbum.setDescription("Test Description");
        testAlbum.setPhotoCount(5);

        albumRequest = new AlbumRequest();
        albumRequest.setName("New Album");
        albumRequest.setDescription("New Description");
    }

    @Test
    void testCreateAlbum_Success() {
        when(albumMapper.insert(any(Album.class))).thenReturn(1);

        Album result = albumService.createAlbum(userId, albumRequest);
        
        assertNotNull(result);
        assertEquals(albumRequest.getName(), result.getName());
        assertEquals(albumRequest.getDescription(), result.getDescription());
        assertEquals(userId, result.getUserId());
        assertEquals(0, result.getPhotoCount());
        
        verify(albumMapper, times(1)).insert(any(Album.class));
    }

    @Test
    void testUpdateAlbum_Success() {
        when(albumMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testAlbum);
        when(albumMapper.updateById(any(Album.class))).thenReturn(1);

        Album result = albumService.updateAlbum(1L, userId, albumRequest);
        
        assertNotNull(result);
        assertEquals(albumRequest.getName(), result.getName());
        assertEquals(albumRequest.getDescription(), result.getDescription());
        
        verify(albumMapper, times(1)).updateById(any(Album.class));
    }

    @Test
    void testUpdateAlbum_NotFound() {
        when(albumMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> {
            albumService.updateAlbum(1L, userId, albumRequest);
        });
        
        verify(albumMapper, never()).updateById(any(Album.class));
    }

    @Test
    void testDeleteAlbum_Success() {
        when(albumMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testAlbum);
        when(photoMapper.update(any(), any(LambdaUpdateWrapper.class))).thenReturn(1);
        when(albumMapper.deleteById(anyLong())).thenReturn(1);

        assertDoesNotThrow(() -> albumService.deleteAlbum(1L, userId));
        
        verify(photoMapper, times(1)).update(any(), any(LambdaUpdateWrapper.class));
        verify(albumMapper, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteAlbum_NotFound() {
        when(albumMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> {
            albumService.deleteAlbum(1L, userId);
        });
        
        verify(albumMapper, never()).deleteById(anyLong());
    }

    @Test
    void testGetAlbumById_Success() {
        when(albumMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testAlbum);

        Album result = albumService.getAlbumById(1L, userId);
        
        assertNotNull(result);
        assertEquals(testAlbum.getId(), result.getId());
    }

    @Test
    void testGetUserAlbums_Success() {
        Page<Album> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(testAlbum));
        
        when(albumMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        Page<Album> result = albumService.getUserAlbums(userId, 1, 10);
        
        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
    }

    @Test
    void testGetAllUserAlbums_Success() {
        List<Album> albums = Arrays.asList(testAlbum);
        
        when(albumMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(albums);

        List<Album> result = albumService.getAllUserAlbums(userId);
        
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
