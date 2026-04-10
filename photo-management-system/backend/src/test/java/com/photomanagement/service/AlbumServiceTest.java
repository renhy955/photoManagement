package com.photomanagement.service;

import com.photomanagement.dto.AlbumDTO;
import com.photomanagement.entity.Album;
import com.photomanagement.mapper.AlbumMapper;
import com.photomanagement.service.impl.AlbumServiceImpl;
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

    @InjectMocks
    private AlbumServiceImpl albumService;

    private Album testAlbum;

    @BeforeEach
    void setUp() {
        testAlbum = new Album();
        testAlbum.setId(1L);
        testAlbum.setUserId(1L);
        testAlbum.setName("测试相册");
        testAlbum.setDescription("这是测试相册");
        testAlbum.setPhotoCount(10);
        testAlbum.setSortOrder(0);
    }

    @Test
    void testGetUserAlbums_Success() {
        Album album1 = new Album();
        album1.setId(1L);
        album1.setUserId(1L);
        album1.setName("相册1");
        album1.setPhotoCount(5);

        Album album2 = new Album();
        album2.setId(2L);
        album2.setUserId(1L);
        album2.setName("相册2");
        album2.setPhotoCount(10);

        when(albumMapper.selectList(any())).thenReturn(Arrays.asList(album1, album2));

        List<AlbumDTO> result = albumService.getUserAlbums(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testGetUserAlbums_Empty() {
        when(albumMapper.selectList(any())).thenReturn(Arrays.asList());

        List<AlbumDTO> result = albumService.getUserAlbums(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testCreateAlbum_Success() {
        when(albumMapper.insert(any(Album.class))).thenReturn(1);

        AlbumDTO result = albumService.createAlbum(1L, "新相册", "相册描述");

        assertNotNull(result);
        assertEquals("新相册", result.getName());
        assertEquals("相册描述", result.getDescription());
        assertEquals(0, result.getPhotoCount());
        verify(albumMapper).insert(any(Album.class));
    }

    @Test
    void testUpdateAlbum_Success() {
        when(albumMapper.selectById(1L)).thenReturn(testAlbum);
        when(albumMapper.updateById(any(Album.class))).thenReturn(true);

        AlbumDTO result = albumService.updateAlbum(1L, 1L, "更新后的相册", "新的描述");

        assertNotNull(result);
        assertEquals("更新后的相册", result.getName());
        assertEquals("新的描述", result.getDescription());
    }

    @Test
    void testUpdateAlbum_NotFound() {
        when(albumMapper.selectById(999L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> 
            albumService.updateAlbum(999L, 1L, "名称", "描述"));
    }

    @Test
    void testUpdateAlbum_NotOwner() {
        when(albumMapper.selectById(1L)).thenReturn(testAlbum);

        assertThrows(RuntimeException.class, () -> 
            albumService.updateAlbum(1L, 999L, "名称", "描述"));
    }

    @Test
    void testDeleteAlbum_Success() {
        when(albumMapper.selectById(1L)).thenReturn(testAlbum);
        when(albumMapper.deleteById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> albumService.deleteAlbum(1L, 1L));
        verify(albumMapper).deleteById(1L);
    }

    @Test
    void testDeleteAlbum_NotFound() {
        when(albumMapper.selectById(999L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> 
            albumService.deleteAlbum(999L, 1L));
    }

    @Test
    void testDeleteAlbum_NotOwner() {
        when(albumMapper.selectById(1L)).thenReturn(testAlbum);

        assertThrows(RuntimeException.class, () -> 
            albumService.deleteAlbum(1L, 999L));
    }

    @Test
    void testUpdatePhotoCount() {
        doNothing().when(albumMapper).updatePhotoCount(1L, 5);

        assertDoesNotThrow(() -> albumService.updatePhotoCount(1L, 5));
        verify(albumMapper).updatePhotoCount(1L, 5);
    }
}
