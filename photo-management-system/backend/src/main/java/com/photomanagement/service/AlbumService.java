package com.photomanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.photomanagement.dto.AlbumDTO;
import com.photomanagement.entity.Album;

import java.util.List;

public interface AlbumService extends IService<Album> {
    
    List<AlbumDTO> getUserAlbums(Long userId);
    
    AlbumDTO createAlbum(Long userId, String name, String description);
    
    AlbumDTO updateAlbum(Long albumId, Long userId, String name, String description);
    
    void deleteAlbum(Long albumId, Long userId);
    
    void updatePhotoCount(Long albumId, int count);
}
