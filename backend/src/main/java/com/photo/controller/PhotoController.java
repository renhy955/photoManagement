package com.photo.controller;

import com.photo.common.Result;
import com.photo.entity.User;
import com.photo.service.PhotoService;
import com.photo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/photos")
public class PhotoController {

    @Autowired
    private PhotoService photoService;

    @Autowired
    private UserService userService;

    private Long getUserId(Authentication authentication) {
        User user = userService.getByUsername(authentication.getName());
        return user.getId();
    }

    @GetMapping
    public Result<Map<String, Object>> getPhotoList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) Long albumId,
            Authentication authentication) {
        return photoService.getPhotoList(page, size, getUserId(authentication), albumId);
    }

    @PostMapping("/upload")
    public Result<?> uploadPhotos(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(required = false) Long albumId,
            Authentication authentication) throws IOException {
        return photoService.uploadPhotos(files, getUserId(authentication), albumId);
    }

    @PutMapping("/{id}/rename")
    public Result<?> renamePhoto(@PathVariable Long id, @RequestParam String name, Authentication authentication) {
        return photoService.renamePhoto(id, name, getUserId(authentication));
    }

    @PostMapping("/move")
    public Result<?> movePhotos(@RequestParam List<Long> photoIds, @RequestParam Long targetAlbumId, Authentication authentication) {
        return photoService.movePhotos(photoIds, targetAlbumId, getUserId(authentication));
    }

    @PostMapping("/copy")
    public Result<?> copyPhotos(@RequestParam List<Long> photoIds, @RequestParam Long targetAlbumId, Authentication authentication) {
        return photoService.copyPhotos(photoIds, targetAlbumId, getUserId(authentication));
    }

    @DeleteMapping
    public Result<?> deletePhotos(@RequestParam List<Long> photoIds, Authentication authentication) {
        return photoService.deletePhotos(photoIds, getUserId(authentication));
    }
}
