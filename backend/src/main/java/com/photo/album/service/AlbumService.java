package com.photo.album.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.photo.album.dto.AlbumRequest;
import com.photo.album.entity.Album;
import java.util.List;

public interface AlbumService {
    Album createAlbum(Long userId, AlbumRequest request);
    
    Album updateAlbum(Long id, Long userId, AlbumRequest request);
    
    void deleteAlbum(Long id, Long userId);
    
    Album getAlbumById(Long id, Long userId);
    
    Page<Album> getUserAlbums(Long userId, int page, int size);
    
    List<Album> getAllUserAlbums(Long userId);
}
