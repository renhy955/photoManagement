package com.photomanagement.controller;

import com.photomanagement.dto.AlbumDTO;
import com.photomanagement.dto.ApiResponse;
import com.photomanagement.security.UserDetailsImpl;
import com.photomanagement.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/albums")
@RequiredArgsConstructor
public class AlbumController {
    
    private final AlbumService albumService;
    
    @GetMapping
    public ApiResponse<List<AlbumDTO>> getUserAlbums() {
        Long userId = getCurrentUserId();
        List<AlbumDTO> albums = albumService.getUserAlbums(userId);
        return ApiResponse.success(albums);
    }
    
    @PostMapping
    public ApiResponse<AlbumDTO> createAlbum(@RequestBody Map<String, String> params) {
        Long userId = getCurrentUserId();
        String name = params.get("name");
        String description = params.get("description");
        AlbumDTO album = albumService.createAlbum(userId, name, description);
        return ApiResponse.success(album);
    }
    
    @PutMapping("/{id}")
    public ApiResponse<AlbumDTO> updateAlbum(@PathVariable Long id, @RequestBody Map<String, String> params) {
        Long userId = getCurrentUserId();
        String name = params.get("name");
        String description = params.get("description");
        AlbumDTO album = albumService.updateAlbum(id, userId, name, description);
        return ApiResponse.success(album);
    }
    
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAlbum(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        albumService.deleteAlbum(id, userId);
        return ApiResponse.success();
    }
    
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }
}
