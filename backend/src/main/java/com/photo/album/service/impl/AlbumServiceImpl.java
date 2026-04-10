package com.photo.album.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.photo.album.dto.AlbumRequest;
import com.photo.album.entity.Album;
import com.photo.album.entity.Photo;
import com.photo.album.mapper.AlbumMapper;
import com.photo.album.mapper.PhotoMapper;
import com.photo.album.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AlbumServiceImpl implements AlbumService {
    
    @Autowired
    private AlbumMapper albumMapper;
    
    @Autowired
    private PhotoMapper photoMapper;
    
    @Override
    public Album createAlbum(Long userId, AlbumRequest request) {
        Album album = new Album();
        album.setUserId(userId);
        album.setName(request.getName());
        album.setDescription(request.getDescription());
        album.setCoverUrl(request.getCoverUrl());
        album.setPhotoCount(0);
        
        albumMapper.insert(album);
        return album;
    }
    
    @Override
    public Album updateAlbum(Long id, Long userId, AlbumRequest request) {
        Album album = albumMapper.selectOne(
            new LambdaQueryWrapper<Album>()
                .eq(Album::getId, id)
                .eq(Album::getUserId, userId)
        );
        
        if (album == null) {
            throw new IllegalArgumentException("相册不存在");
        }
        
        album.setName(request.getName());
        album.setDescription(request.getDescription());
        if (request.getCoverUrl() != null) {
            album.setCoverUrl(request.getCoverUrl());
        }
        
        albumMapper.updateById(album);
        return album;
    }
    
    @Override
    @Transactional
    public void deleteAlbum(Long id, Long userId) {
        Album album = albumMapper.selectOne(
            new LambdaQueryWrapper<Album>()
                .eq(Album::getId, id)
                .eq(Album::getUserId, userId)
        );
        
        if (album == null) {
            throw new IllegalArgumentException("相册不存在");
        }
        
        LambdaUpdateWrapper<Photo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Photo::getAlbumId, id)
                     .set(Photo::getAlbumId, null);
        photoMapper.update(null, updateWrapper);
        
        albumMapper.deleteById(id);
    }
    
    @Override
    public Album getAlbumById(Long id, Long userId) {
        return albumMapper.selectOne(
            new LambdaQueryWrapper<Album>()
                .eq(Album::getId, id)
                .eq(Album::getUserId, userId)
        );
    }
    
    @Override
    public Page<Album> getUserAlbums(Long userId, int page, int size) {
        Page<Album> pageParam = new Page<>(page, size);
        return albumMapper.selectPage(pageParam,
            new LambdaQueryWrapper<Album>()
                .eq(Album::getUserId, userId)
                .orderByDesc(Album::getCreatedAt)
        );
    }
    
    @Override
    public List<Album> getAllUserAlbums(Long userId) {
        return albumMapper.selectList(
            new LambdaQueryWrapper<Album>()
                .eq(Album::getUserId, userId)
                .orderByDesc(Album::getCreatedAt)
        );
    }
}
