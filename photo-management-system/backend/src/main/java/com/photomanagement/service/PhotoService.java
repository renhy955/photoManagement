package com.photomanagement.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.photomanagement.dto.PhotoDTO;
import com.photomanagement.entity.Photo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PhotoService extends IService<Photo> {
    
    PhotoDTO uploadPhoto(Long userId, Long albumId, MultipartFile file) throws IOException;
    
    List<PhotoDTO> uploadPhotos(Long userId, Long albumId, MultipartFile[] files) throws IOException;
    
    IPage<PhotoDTO> getUserPhotos(Long userId, Page<PhotoDTO> page);
    
    IPage<PhotoDTO> getAlbumPhotos(Long albumId, Long userId, Page<PhotoDTO> page);
    
    PhotoDTO getPhotoById(Long photoId, Long userId);
    
    PhotoDTO renamePhoto(Long photoId, Long userId, String newName);
    
    void movePhoto(Long photoId, Long userId, Long newAlbumId);
    
    void copyPhoto(Long photoId, Long userId, Long targetAlbumId);
    
    void deletePhoto(Long photoId, Long userId);
    
    void batchDeletePhotos(List<Long> photoIds, Long userId);
}
