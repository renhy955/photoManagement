package com.photomanagement.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.photomanagement.dto.ApiResponse;
import com.photomanagement.dto.PhotoDTO;
import com.photomanagement.security.UserDetailsImpl;
import com.photomanagement.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/photos")
@RequiredArgsConstructor
public class PhotoController {
    
    private final PhotoService photoService;
    
    @GetMapping
    public ApiResponse<Page<PhotoDTO>> getUserPhotos(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = getCurrentUserId();
        Page<PhotoDTO> pageParam = new Page<>(page, size);
        Page<PhotoDTO> result = (Page<PhotoDTO>) photoService.getUserPhotos(userId, pageParam);
        return ApiResponse.success(result);
    }
    
    @GetMapping("/album/{albumId}")
    public ApiResponse<Page<PhotoDTO>> getAlbumPhotos(
            @PathVariable Long albumId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = getCurrentUserId();
        Page<PhotoDTO> pageParam = new Page<>(page, size);
        Page<PhotoDTO> result = (Page<PhotoDTO>) photoService.getAlbumPhotos(albumId, userId, pageParam);
        return ApiResponse.success(result);
    }
    
    @GetMapping("/{id}")
    public ApiResponse<PhotoDTO> getPhotoById(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        PhotoDTO photo = photoService.getPhotoById(id, userId);
        return ApiResponse.success(photo);
    }
    
    @PostMapping("/upload")
    public ApiResponse<List<PhotoDTO>> uploadPhotos(
            @RequestParam(value = "albumId", required = false) Long albumId,
            @RequestParam("files") MultipartFile[] files) throws IOException {
        Long userId = getCurrentUserId();
        List<PhotoDTO> photos = photoService.uploadPhotos(userId, albumId, files);
        return ApiResponse.success(photos);
    }
    
    @PutMapping("/{id}/rename")
    public ApiResponse<PhotoDTO> renamePhoto(@PathVariable Long id, @RequestBody Map<String, String> params) {
        Long userId = getCurrentUserId();
        String newName = params.get("name");
        PhotoDTO photo = photoService.renamePhoto(id, userId, newName);
        return ApiResponse.success(photo);
    }
    
    @PutMapping("/{id}/move")
    public ApiResponse<Void> movePhoto(@PathVariable Long id, @RequestBody Map<String, Long> params) {
        Long userId = getCurrentUserId();
        Long newAlbumId = params.get("albumId");
        photoService.movePhoto(id, userId, newAlbumId);
        return ApiResponse.success();
    }
    
    @PostMapping("/{id}/copy")
    public ApiResponse<Void> copyPhoto(@PathVariable Long id, @RequestBody Map<String, Long> params) {
        Long userId = getCurrentUserId();
        Long targetAlbumId = params.get("albumId");
        photoService.copyPhoto(id, userId, targetAlbumId);
        return ApiResponse.success();
    }
    
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePhoto(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        photoService.deletePhoto(id, userId);
        return ApiResponse.success();
    }
    
    @PostMapping("/batch-delete")
    public ApiResponse<Void> batchDeletePhotos(@RequestBody Map<String, List<Long>> params) {
        Long userId = getCurrentUserId();
        List<Long> photoIds = params.get("ids");
        photoService.batchDeletePhotos(photoIds, userId);
        return ApiResponse.success();
    }
    
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }
}
