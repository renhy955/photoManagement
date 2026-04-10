package com.photo.album.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.photo.album.dto.PhotoMoveRequest;
import com.photo.album.dto.PhotoRenameRequest;
import com.photo.album.entity.Album;
import com.photo.album.entity.Photo;
import com.photo.album.mapper.AlbumMapper;
import com.photo.album.mapper.PhotoMapper;
import com.photo.album.service.impl.PhotoServiceImpl;
import com.photo.album.utils.FileUploadUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PhotoServiceTest {

    @Mock
    private PhotoMapper photoMapper;

    @Mock
    private AlbumMapper albumMapper;

    @Mock
    private FileUploadUtil fileUploadUtil;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private PhotoServiceImpl photoService;

    private Photo testPhoto;
    private Album testAlbum;
    private final Long userId = 1L;
    private final Long albumId = 1L;

    @BeforeEach
    void setUp() {
        testPhoto = new Photo();
        testPhoto.setId(1L);
        testPhoto.setUserId(userId);
        testPhoto.setAlbumId(albumId);
        testPhoto.setName("test.jpg");
        testPhoto.setOriginalName("test.jpg");
        testPhoto.setUrl("/uploads/1/2024/04/test.jpg");
        testPhoto.setSize(1024L);
        testPhoto.setFormat("jpg");
        testPhoto.setWidth(1920);
        testPhoto.setHeight(1080);

        testAlbum = new Album();
        testAlbum.setId(albumId);
        testAlbum.setUserId(userId);
        testAlbum.setName("Test Album");
        testAlbum.setPhotoCount(5);
    }

    @Test
    void testRenamePhoto_Success() {
        PhotoRenameRequest request = new PhotoRenameRequest();
        request.setName("new-name.jpg");

        when(photoMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testPhoto);
        when(photoMapper.updateById(any(Photo.class))).thenReturn(1);

        Photo result = photoService.renamePhoto(1L, userId, request);
        
        assertNotNull(result);
        assertEquals("new-name.jpg", result.getName());
        
        verify(photoMapper, times(1)).updateById(any(Photo.class));
    }

    @Test
    void testRenamePhoto_NotFound() {
        PhotoRenameRequest request = new PhotoRenameRequest();
        request.setName("new-name.jpg");

        when(photoMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> {
            photoService.renamePhoto(1L, userId, request);
        });
        
        verify(photoMapper, never()).updateById(any(Photo.class));
    }

    @Test
    void testDeletePhoto_Success() {
        when(photoMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testPhoto);
        when(photoMapper.deleteById(anyLong())).thenReturn(1);
        when(albumMapper.selectById(anyLong())).thenReturn(testAlbum);
        when(albumMapper.updateById(any(Album.class))).thenReturn(1);

        assertDoesNotThrow(() -> photoService.deletePhoto(1L, userId));
        
        verify(photoMapper, times(1)).deleteById(1L);
        verify(albumMapper, times(1)).updateById(any(Album.class));
    }

    @Test
    void testDeletePhoto_NotFound() {
        when(photoMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> {
            photoService.deletePhoto(1L, userId);
        });
        
        verify(photoMapper, never()).deleteById(anyLong());
    }

    @Test
    void testGetPhotoById_Success() {
        when(photoMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testPhoto);

        Photo result = photoService.getPhotoById(1L, userId);
        
        assertNotNull(result);
        assertEquals(testPhoto.getId(), result.getId());
    }

    @Test
    void testGetUserPhotos_Success() {
        Page<Photo> page = new Page<>(1, 20);
        page.setRecords(Arrays.asList(testPhoto));
        
        when(photoMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        Page<Photo> result = photoService.getUserPhotos(userId, 1, 20);
        
        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
    }

    @Test
    void testGetAlbumPhotos_Success() {
        Page<Photo> page = new Page<>(1, 20);
        page.setRecords(Arrays.asList(testPhoto));
        
        when(photoMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        Page<Photo> result = photoService.getAlbumPhotos(albumId, userId, 1, 20);
        
        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
    }

    @Test
    void testMovePhotos_Success() {
        PhotoMoveRequest request = new PhotoMoveRequest();
        request.setAlbumId(2L);
        request.setPhotoIds(Arrays.asList(1L));

        Album targetAlbum = new Album();
        targetAlbum.setId(2L);
        targetAlbum.setUserId(userId);
        targetAlbum.setPhotoCount(0);

        when(albumMapper.selectById(anyLong())).thenReturn(targetAlbum);
        when(photoMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testPhoto);
        when(photoMapper.updateById(any(Photo.class))).thenReturn(1);
        when(albumMapper.updateById(any(Album.class))).thenReturn(1);

        assertDoesNotThrow(() -> photoService.movePhotos(request, userId));
        
        verify(photoMapper, times(1)).updateById(any(Photo.class));
    }

    @Test
    void testDeletePhotos_Success() {
        List<Long> ids = Arrays.asList(1L, 2L);

        when(photoMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testPhoto);
        when(photoMapper.deleteById(anyLong())).thenReturn(1);
        when(albumMapper.selectById(anyLong())).thenReturn(testAlbum);
        when(albumMapper.updateById(any(Album.class))).thenReturn(1);

        assertDoesNotThrow(() -> photoService.deletePhotos(ids, userId));
        
        verify(photoMapper, times(2)).deleteById(anyLong());
    }
}
