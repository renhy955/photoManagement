package com.photo.album.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.photo.album.dto.PhotoCopyRequest;
import com.photo.album.dto.PhotoMoveRequest;
import com.photo.album.dto.PhotoRenameRequest;
import com.photo.album.entity.Album;
import com.photo.album.entity.Photo;
import com.photo.album.mapper.AlbumMapper;
import com.photo.album.mapper.PhotoMapper;
import com.photo.album.service.PhotoService;
import com.photo.album.utils.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PhotoServiceImpl implements PhotoService {
    
    @Autowired
    private PhotoMapper photoMapper;
    
    @Autowired
    private AlbumMapper albumMapper;
    
    @Autowired
    private FileUploadUtil fileUploadUtil;
    
    @Override
    @Transactional
    public Photo uploadPhoto(Long userId, Long albumId, MultipartFile file) {
        try {
            String fileUrl = fileUploadUtil.uploadFile(file, userId);
            String originalName = file.getOriginalFilename();
            String format = getFileExtension(originalName);
            int[] dimensions = fileUploadUtil.getImageDimensions(file);
            
            Photo photo = new Photo();
            photo.setUserId(userId);
            photo.setAlbumId(albumId);
            photo.setName(originalName);
            photo.setOriginalName(originalName);
            photo.setUrl(fileUrl);
            photo.setThumbnailUrl(fileUrl);
            photo.setSize(file.getSize());
            photo.setFormat(format);
            photo.setWidth(dimensions[0]);
            photo.setHeight(dimensions[1]);
            
            photoMapper.insert(photo);
            
            if (albumId != null) {
                updateAlbumPhotoCount(albumId, 1);
            }
            
            return photo;
        } catch (IOException e) {
            throw new IllegalArgumentException("文件上传失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public List<Photo> uploadPhotos(Long userId, Long albumId, List<MultipartFile> files) {
        List<Photo> photos = new ArrayList<>();
        for (MultipartFile file : files) {
            photos.add(uploadPhoto(userId, albumId, file));
        }
        return photos;
    }
    
    @Override
    public Photo renamePhoto(Long id, Long userId, PhotoRenameRequest request) {
        Photo photo = photoMapper.selectOne(
            new LambdaQueryWrapper<Photo>()
                .eq(Photo::getId, id)
                .eq(Photo::getUserId, userId)
        );
        
        if (photo == null) {
            throw new IllegalArgumentException("照片不存在");
        }
        
        photo.setName(request.getName());
        photoMapper.updateById(photo);
        return photo;
    }
    
    @Override
    @Transactional
    public void deletePhoto(Long id, Long userId) {
        Photo photo = photoMapper.selectOne(
            new LambdaQueryWrapper<Photo>()
                .eq(Photo::getId, id)
                .eq(Photo::getUserId, userId)
        );
        
        if (photo == null) {
            throw new IllegalArgumentException("照片不存在");
        }
        
        fileUploadUtil.deleteFile(photo.getUrl());
        
        photoMapper.deleteById(id);
        
        if (photo.getAlbumId() != null) {
            updateAlbumPhotoCount(photo.getAlbumId(), -1);
        }
    }
    
    @Override
    @Transactional
    public void deletePhotos(List<Long> ids, Long userId) {
        for (Long id : ids) {
            deletePhoto(id, userId);
        }
    }
    
    @Override
    @Transactional
    public void movePhotos(PhotoMoveRequest request, Long userId) {
        Long targetAlbumId = request.getAlbumId();
        
        if (targetAlbumId != null) {
            Album album = albumMapper.selectById(targetAlbumId);
            if (album == null || !album.getUserId().equals(userId)) {
                throw new IllegalArgumentException("目标相册不存在");
            }
        }
        
        for (Long photoId : request.getPhotoIds()) {
            Photo photo = photoMapper.selectOne(
                new LambdaQueryWrapper<Photo>()
                    .eq(Photo::getId, photoId)
                    .eq(Photo::getUserId, userId)
            );
            
            if (photo != null) {
                Long oldAlbumId = photo.getAlbumId();
                
                photo.setAlbumId(targetAlbumId);
                photoMapper.updateById(photo);
                
                if (oldAlbumId != null) {
                    updateAlbumPhotoCount(oldAlbumId, -1);
                }
                if (targetAlbumId != null) {
                    updateAlbumPhotoCount(targetAlbumId, 1);
                }
            }
        }
    }
    
    @Override
    @Transactional
    public void copyPhotos(PhotoCopyRequest request, Long userId) {
        if (request.getAlbumId() == null) {
            throw new IllegalArgumentException("目标相册不能为空");
        }
        
        Album album = albumMapper.selectById(request.getAlbumId());
        if (album == null || !album.getUserId().equals(userId)) {
            throw new IllegalArgumentException("目标相册不存在");
        }
        
        for (Long photoId : request.getPhotoIds()) {
            Photo originalPhoto = photoMapper.selectOne(
                new LambdaQueryWrapper<Photo>()
                    .eq(Photo::getId, photoId)
                    .eq(Photo::getUserId, userId)
            );
            
            if (originalPhoto != null) {
                Photo newPhoto = new Photo();
                newPhoto.setUserId(userId);
                newPhoto.setAlbumId(request.getAlbumId());
                newPhoto.setName(originalPhoto.getName());
                newPhoto.setOriginalName(originalPhoto.getOriginalName());
                newPhoto.setUrl(originalPhoto.getUrl());
                newPhoto.setThumbnailUrl(originalPhoto.getThumbnailUrl());
                newPhoto.setSize(originalPhoto.getSize());
                newPhoto.setFormat(originalPhoto.getFormat());
                newPhoto.setWidth(originalPhoto.getWidth());
                newPhoto.setHeight(originalPhoto.getHeight());
                
                photoMapper.insert(newPhoto);
                updateAlbumPhotoCount(request.getAlbumId(), 1);
            }
        }
    }
    
    @Override
    public Photo getPhotoById(Long id, Long userId) {
        return photoMapper.selectOne(
            new LambdaQueryWrapper<Photo>()
                .eq(Photo::getId, id)
                .eq(Photo::getUserId, userId)
        );
    }
    
    @Override
    public Page<Photo> getUserPhotos(Long userId, int page, int size) {
        Page<Photo> pageParam = new Page<>(page, size);
        return photoMapper.selectPage(pageParam,
            new LambdaQueryWrapper<Photo>()
                .eq(Photo::getUserId, userId)
                .orderByDesc(Photo::getCreatedAt)
        );
    }
    
    @Override
    public Page<Photo> getAlbumPhotos(Long albumId, Long userId, int page, int size) {
        Page<Photo> pageParam = new Page<>(page, size);
        return photoMapper.selectPage(pageParam,
            new LambdaQueryWrapper<Photo>()
                .eq(Photo::getUserId, userId)
                .eq(Photo::getAlbumId, albumId)
                .orderByDesc(Photo::getCreatedAt)
        );
    }
    
    private void updateAlbumPhotoCount(Long albumId, int delta) {
        Album album = albumMapper.selectById(albumId);
        if (album != null) {
            album.setPhotoCount(album.getPhotoCount() + delta);
            albumMapper.updateById(album);
        }
    }
    
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
}
