package com.photo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.photo.common.Result;
import com.photo.entity.Photo;
import com.photo.mapper.PhotoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class PhotoService {

    @Autowired
    private PhotoMapper photoMapper;

    @Value("${file.upload-path}")
    private String uploadPath;

    @Value("${file.access-path}")
    private String accessPath;

    private static final List<String> ALLOWED_TYPES = Arrays.asList("image/jpeg", "image/png", "image/gif", "image/jpg", "image/webp");
    private static final long MAX_SIZE = 10 * 1024 * 1024;

    public Result<Map<String, Object>> getPhotoList(Integer page, Integer size, Long userId, Long albumId) {
        Page<Photo> photoPage = new Page<>(page, size);
        LambdaQueryWrapper<Photo> wrapper = new LambdaQueryWrapper<Photo>()
                .eq(Photo::getUserId, userId)
                .orderByDesc(Photo::getUploadTime);

        if (albumId != null) {
            wrapper.eq(Photo::getAlbumId, albumId);
        }

        photoMapper.selectPage(photoPage, wrapper);

        Map<String, Object> data = new HashMap<>();
        data.put("list", photoPage.getRecords());
        data.put("total", photoPage.getTotal());
        return Result.success(data);
    }

    public Result<?> uploadPhotos(MultipartFile[] files, Long userId, Long albumId) throws IOException {
        List<Photo> uploadedPhotos = new ArrayList<>();

        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        for (MultipartFile file : files) {
            if (!ALLOWED_TYPES.contains(file.getContentType())) {
                return Result.error("不支持的图片格式: " + file.getOriginalFilename());
            }
            if (file.getSize() > MAX_SIZE) {
                return Result.error("图片大小超过限制: " + file.getOriginalFilename());
            }

            String originalName = file.getOriginalFilename();
            String fileExtension = originalName.substring(originalName.lastIndexOf("."));
            String newFileName = UUID.randomUUID().toString() + fileExtension;

            Path filePath = Paths.get(uploadPath, newFileName);
            Files.copy(file.getInputStream(), filePath);

            Integer width = null;
            Integer height = null;
            try {
                BufferedImage image = ImageIO.read(filePath.toFile());
                if (image != null) {
                    width = image.getWidth();
                    height = image.getHeight();
                }
            } catch (Exception e) {
            }

            Photo photo = new Photo();
            photo.setUserId(userId);
            photo.setAlbumId(albumId);
            photo.setName(originalName);
            photo.setOriginalName(originalName);
            photo.setFilePath(accessPath + newFileName);
            photo.setFileSize(file.getSize());
            photo.setFileType(file.getContentType());
            photo.setWidth(width);
            photo.setHeight(height);

            photoMapper.insert(photo);
            uploadedPhotos.add(photo);
        }

        return Result.success(uploadedPhotos);
    }

    public Result<?> renamePhoto(Long id, String name, Long userId) {
        Photo photo = photoMapper.selectOne(
                new LambdaQueryWrapper<Photo>()
                        .eq(Photo::getId, id)
                        .eq(Photo::getUserId, userId)
        );
        if (photo == null) {
            return Result.error("照片不存在");
        }

        photoMapper.update(null,
                new LambdaUpdateWrapper<Photo>()
                        .eq(Photo::getId, id)
                        .set(Photo::getName, name)
        );
        return Result.success();
    }

    public Result<?> movePhotos(List<Long> photoIds, Long targetAlbumId, Long userId) {
        for (Long photoId : photoIds) {
            photoMapper.update(null,
                    new LambdaUpdateWrapper<Photo>()
                            .eq(Photo::getId, photoId)
                            .eq(Photo::getUserId, userId)
                            .set(Photo::getAlbumId, targetAlbumId)
            );
        }
        return Result.success();
    }

    public Result<?> copyPhotos(List<Long> photoIds, Long targetAlbumId, Long userId) {
        for (Long photoId : photoIds) {
            Photo photo = photoMapper.selectOne(
                    new LambdaQueryWrapper<Photo>()
                            .eq(Photo::getId, photoId)
                            .eq(Photo::getUserId, userId)
            );
            if (photo != null) {
                Photo newPhoto = new Photo();
                newPhoto.setUserId(userId);
                newPhoto.setAlbumId(targetAlbumId);
                newPhoto.setName(photo.getName());
                newPhoto.setOriginalName(photo.getOriginalName());
                newPhoto.setFilePath(photo.getFilePath());
                newPhoto.setFileSize(photo.getFileSize());
                newPhoto.setFileType(photo.getFileType());
                newPhoto.setWidth(photo.getWidth());
                newPhoto.setHeight(photo.getHeight());
                photoMapper.insert(newPhoto);
            }
        }
        return Result.success();
    }

    public Result<?> deletePhotos(List<Long> photoIds, Long userId) {
        for (Long photoId : photoIds) {
            photoMapper.delete(
                    new LambdaQueryWrapper<Photo>()
                            .eq(Photo::getId, photoId)
                            .eq(Photo::getUserId, userId)
            );
        }
        return Result.success();
    }
}
