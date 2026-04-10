package com.photo.album.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.photo.album.dto.AlbumRequest;
import com.photo.album.entity.Album;
import com.photo.album.service.AlbumService;
import com.photo.album.utils.Result;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/albums")
public class AlbumController {
    
    @Autowired
    private AlbumService albumService;
    
    @PostMapping
    public Result<Album> createAlbum(@Valid @RequestBody AlbumRequest request,
                                     @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        Album album = albumService.createAlbum(userId, request);
        return Result.success("创建成功", album);
    }
    
    @PutMapping("/{id}")
    public Result<Album> updateAlbum(@PathVariable Long id,
                                     @Valid @RequestBody AlbumRequest request,
                                     @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        Album album = albumService.updateAlbum(id, userId, request);
        return Result.success("更新成功", album);
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteAlbum(@PathVariable Long id,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        albumService.deleteAlbum(id, userId);
        return Result.success("删除成功", null);
    }
    
    @GetMapping("/{id}")
    public Result<Album> getAlbum(@PathVariable Long id,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        Album album = albumService.getAlbumById(id, userId);
        return Result.success(album);
    }
    
    @GetMapping
    public Result<Page<Album>> getUserAlbums(@RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int size,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        Page<Album> albums = albumService.getUserAlbums(userId, page, size);
        return Result.success(albums);
    }
    
    @GetMapping("/all")
    public Result<List<Album>> getAllUserAlbums(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        List<Album> albums = albumService.getAllUserAlbums(userId);
        return Result.success(albums);
    }
    
    private Long getUserId(UserDetails userDetails) {
        return Long.parseLong(userDetails.getUsername().hashCode() + "");
    }
}
