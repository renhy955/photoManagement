package com.photomanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.photomanagement.dto.PhotoDTO;
import com.photomanagement.entity.Photo;
import com.photomanagement.mapper.PhotoMapper;
import com.photomanagement.service.AlbumService;
import com.photomanagement.service.PhotoService;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PhotoServiceImpl extends ServiceImpl<PhotoMapper, Photo> implements PhotoService {
    
    private final AlbumService albumService;
    
    @Value("${upload.path}")
    private String uploadPath;
    
    @Value("${upload.allowed-types}")
    private String allowedTypes;
    
    @Value("${upload.max-size}")
    private long maxSize;
    
    @Override
    @Transactional
    public PhotoDTO uploadPhoto(Long userId, Long albumId, MultipartFile file) throws IOException {
        validateFile(file);
        
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String newFilename = UUID.randomUUID().toString() + "." + extension;
        
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        Path uploadDir = Paths.get(uploadPath, datePath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        
        Path filePath = uploadDir.resolve(newFilename);
        file.transferTo(filePath.toFile());
        
        // 生成缩略图
        String thumbnailFilename = "thumb_" + newFilename;
        Path thumbnailPath = uploadDir.resolve(thumbnailFilename);
        generateThumbnail(filePath.toFile(), thumbnailPath.toFile());
        
        // 获取图片尺寸
        BufferedImage image = ImageIO.read(filePath.toFile());
        int width = image != null ? image.getWidth() : 0;
        int height = image != null ? image.getHeight() : 0;
        
        Photo photo = new Photo();
        photo.setUserId(userId);
        photo.setAlbumId(albumId);
        photo.setName(originalFilename != null ? originalFilename.substring(0, originalFilename.lastIndexOf(".")) : newFilename);
        photo.setUrl("/uploads/" + datePath + "/" + newFilename);
        photo.setThumbnailUrl("/uploads/" + datePath + "/" + thumbnailFilename);
        photo.setFileSize(file.getSize());
        photo.setWidth(width);
        photo.setHeight(height);
        photo.setMimeType(file.getContentType());
        
        save(photo);
        
        // 更新相册照片数量
        if (albumId != null) {
            albumService.updatePhotoCount(albumId, 1);
        }
        
        return convertToDTO(photo);
    }
    
    @Override
    @Transactional
    public List<PhotoDTO> uploadPhotos(Long userId, Long albumId, MultipartFile[] files) throws IOException {
        List<PhotoDTO> results = new java.util.ArrayList<>();
        for (MultipartFile file : files) {
            results.add(uploadPhoto(userId, albumId, file));
        }
        return results;
    }
    
    @Override
    public IPage<PhotoDTO> getUserPhotos(Long userId, Page<PhotoDTO> page) {
        LambdaQueryWrapper<Photo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Photo::getUserId, userId)
               .orderByDesc(Photo::getCreateTime);
        Page<Photo> photoPage = new Page<>(page.getCurrent(), page.getSize());
        Page<Photo> result = page(photoPage, wrapper);
        
        List<PhotoDTO> records = result.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        Page<PhotoDTO> dtoPage = new Page<>();
        dtoPage.setCurrent(result.getCurrent());
        dtoPage.setSize(result.getSize());
        dtoPage.setTotal(result.getTotal());
        dtoPage.setRecords(records);
        return dtoPage;
    }
    
    @Override
    public IPage<PhotoDTO> getAlbumPhotos(Long albumId, Long userId, Page<PhotoDTO> page) {
        LambdaQueryWrapper<Photo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Photo::getUserId, userId)
               .eq(Photo::getAlbumId, albumId)
               .orderByDesc(Photo::getCreateTime);
        Page<Photo> photoPage = new Page<>(page.getCurrent(), page.getSize());
        Page<Photo> result = page(photoPage, wrapper);
        
        List<PhotoDTO> records = result.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        Page<PhotoDTO> dtoPage = new Page<>();
        dtoPage.setCurrent(result.getCurrent());
        dtoPage.setSize(result.getSize());
        dtoPage.setTotal(result.getTotal());
        dtoPage.setRecords(records);
        return dtoPage;
    }
    
    @Override
    public PhotoDTO getPhotoById(Long photoId, Long userId) {
        LambdaQueryWrapper<Photo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Photo::getId, photoId)
               .eq(Photo::getUserId, userId);
        Photo photo = getOne(wrapper);
        if (photo == null) {
            throw new RuntimeException("照片不存在");
        }
        return convertToDTO(photo);
    }
    
    @Override
    public PhotoDTO renamePhoto(Long photoId, Long userId, String newName) {
        Photo photo = getById(photoId);
        if (photo == null || !photo.getUserId().equals(userId)) {
            throw new RuntimeException("照片不存在或无权限");
        }
        photo.setName(newName);
        updateById(photo);
        return convertToDTO(photo);
    }
    
    @Override
    @Transactional
    public void movePhoto(Long photoId, Long userId, Long newAlbumId) {
        Photo photo = getById(photoId);
        if (photo == null || !photo.getUserId().equals(userId)) {
            throw new RuntimeException("照片不存在或无权限");
        }
        
        Long oldAlbumId = photo.getAlbumId();
        photo.setAlbumId(newAlbumId);
        updateById(photo);
        
        // 更新相册照片数量
        if (oldAlbumId != null) {
            albumService.updatePhotoCount(oldAlbumId, -1);
        }
        if (newAlbumId != null) {
            albumService.updatePhotoCount(newAlbumId, 1);
        }
    }
    
    @Override
    @Transactional
    public void copyPhoto(Long photoId, Long userId, Long targetAlbumId) {
        Photo photo = getById(photoId);
        if (photo == null || !photo.getUserId().equals(userId)) {
            throw new RuntimeException("照片不存在或无权限");
        }
        
        Photo copy = new Photo();
        BeanUtils.copyProperties(photo, copy);
        copy.setId(null);
        copy.setAlbumId(targetAlbumId);
        save(copy);
        
        if (targetAlbumId != null) {
            albumService.updatePhotoCount(targetAlbumId, 1);
        }
    }
    
    @Override
    @Transactional
    public void deletePhoto(Long photoId, Long userId) {
        Photo photo = getById(photoId);
        if (photo == null || !photo.getUserId().equals(userId)) {
            throw new RuntimeException("照片不存在或无权限");
        }
        
        removeById(photoId);
        
        if (photo.getAlbumId() != null) {
            albumService.updatePhotoCount(photo.getAlbumId(), -1);
        }
    }
    
    @Override
    @Transactional
    public void batchDeletePhotos(List<Long> photoIds, Long userId) {
        for (Long photoId : photoIds) {
            deletePhoto(photoId, userId);
        }
    }
    
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }
        
        if (file.getSize() > maxSize) {
            throw new RuntimeException("文件大小超过限制");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !allowedTypes.contains(contentType)) {
            throw new RuntimeException("不支持的文件类型");
        }
    }
    
    private void generateThumbnail(File sourceFile, File destFile) throws IOException {
        Thumbnails.of(sourceFile)
                .size(300, 300)
                .keepAspectRatio(true)
                .toFile(destFile);
    }
    
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "jpg";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
    
    private PhotoDTO convertToDTO(Photo photo) {
        PhotoDTO dto = new PhotoDTO();
        BeanUtils.copyProperties(photo, dto);
        return dto;
    }
}