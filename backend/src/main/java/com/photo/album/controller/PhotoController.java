package com.photo.album.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.photo.album.dto.PhotoCopyRequest;
import com.photo.album.dto.PhotoMoveRequest;
import com.photo.album.dto.PhotoRenameRequest;
import com.photo.album.entity.Photo;
import com.photo.album.service.PhotoService;
import com.photo.album.utils.Result;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/photos")
public class PhotoController {
    
    @Autowired
    private PhotoService photoService;
    
    @PostMapping("/upload")
    public Result<Photo> uploadPhoto(@RequestParam("file") MultipartFile file,
                                     @RequestParam(required = false) Long albumId,
                                     @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        Photo photo = photoService.uploadPhoto(userId, albumId, file);
        return Result.success("上传成功", photo);
    }
    
    @PostMapping("/upload/batch")
    public Result<List<Photo>> uploadPhotos(@RequestParam("files") List<MultipartFile> files,
                                            @RequestParam(required = false) Long albumId,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        List<Photo> photos = photoService.uploadPhotos(userId, albumId, files);
        return Result.success("上传成功", photos);
    }
    
    @PutMapping("/{id}/rename")
    public Result<Photo> renamePhoto(@PathVariable Long id,
                                     @Valid @RequestBody PhotoRenameRequest request,
                                     @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        Photo photo = photoService.renamePhoto(id, userId, request);
        return Result.success("重命名成功", photo);
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deletePhoto(@PathVariable Long id,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        photoService.deletePhoto(id, userId);
        return Result.success("删除成功", null);
    }
    
    @DeleteMapping("/batch")
    public Result<Void> deletePhotos(@RequestBody List<Long> ids,
                                     @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        photoService.deletePhotos(ids, userId);
        return Result.success("删除成功", null);
    }
    
    @PostMapping("/move")
    public Result<Void> movePhotos(@Valid @RequestBody PhotoMoveRequest request,
                                   @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        photoService.movePhotos(request, userId);
        return Result.success("移动成功", null);
    }
    
    @PostMapping("/copy")
    public Result<Void> copyPhotos(@Valid @RequestBody PhotoCopyRequest request,
                                   @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        photoService.copyPhotos(request, userId);
        return Result.success("复制成功", null);
    }
    
    @GetMapping("/{id}")
    public Result<Photo> getPhoto(@PathVariable Long id,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        Photo photo = photoService.getPhotoById(id, userId);
        return Result.success(photo);
    }
    
    @GetMapping
    public Result<Page<Photo>> getUserPhotos(@RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "20") int size,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        Page<Photo> photos = photoService.getUserPhotos(userId, page, size);
        return Result.success(photos);
    }
    
    @GetMapping("/album/{albumId}")
    public Result<Page<Photo>> getAlbumPhotos(@PathVariable Long albumId,
                                              @RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "20") int size,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        Page<Photo> photos = photoService.getAlbumPhotos(albumId, userId, page, size);
        return Result.success(photos);
    }
    
    private Long getUserId(UserDetails userDetails) {
        return (long) userDetails.getUsername().hashCode();
    }
}
