package com.photo.album.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.photo.album.dto.PhotoCopyRequest;
import com.photo.album.dto.PhotoMoveRequest;
import com.photo.album.dto.PhotoRenameRequest;
import com.photo.album.entity.Photo;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface PhotoService {
    Photo uploadPhoto(Long userId, Long albumId, MultipartFile file);
    
    List<Photo> uploadPhotos(Long userId, Long albumId, List<MultipartFile> files);
    
    Photo renamePhoto(Long id, Long userId, PhotoRenameRequest request);
    
    void deletePhoto(Long id, Long userId);
    
    void deletePhotos(List<Long> ids, Long userId);
    
    void movePhotos(PhotoMoveRequest request, Long userId);
    
    void copyPhotos(PhotoCopyRequest request, Long userId);
    
    Photo getPhotoById(Long id, Long userId);
    
    Page<Photo> getUserPhotos(Long userId, int page, int size);
    
    Page<Photo> getAlbumPhotos(Long albumId, Long userId, int page, int size);
}
