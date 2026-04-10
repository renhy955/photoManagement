package com.photo.controller;

import com.photo.common.Result;
import com.photo.entity.Album;
import com.photo.entity.User;
import com.photo.service.AlbumService;
import com.photo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/albums")
public class AlbumController {

    @Autowired
    private AlbumService albumService;

    @Autowired
    private UserService userService;

    private Long getUserId(Authentication authentication) {
        User user = userService.getByUsername(authentication.getName());
        return user.getId();
    }

    @GetMapping
    public Result<List<Album>> getAlbumList(Authentication authentication) {
        return albumService.getAlbumList(getUserId(authentication));
    }

    @GetMapping("/{id}")
    public Result<Album> getAlbumDetail(@PathVariable Long id, Authentication authentication) {
        return albumService.getAlbumDetail(id, getUserId(authentication));
    }

    @PostMapping
    public Result<?> createAlbum(@RequestBody Album album, Authentication authentication) {
        return albumService.createAlbum(album, getUserId(authentication));
    }

    @PutMapping("/{id}")
    public Result<?> updateAlbum(@PathVariable Long id, @RequestBody Album album, Authentication authentication) {
        return albumService.updateAlbum(id, album, getUserId(authentication));
    }

    @DeleteMapping("/{id}")
    public Result<?> deleteAlbum(@PathVariable Long id, Authentication authentication) {
        return albumService.deleteAlbum(id, getUserId(authentication));
    }
}
