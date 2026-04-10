package com.photomanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.photomanagement.dto.AlbumDTO;
import com.photomanagement.entity.Album;
import com.photomanagement.mapper.AlbumMapper;
import com.photomanagement.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl extends ServiceImpl<AlbumMapper, Album> implements AlbumService {
    
    @Override
    public List<AlbumDTO> getUserAlbums(Long userId) {
        LambdaQueryWrapper<Album> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Album::getUserId, userId)
               .orderByAsc(Album::getSortOrder)
               .orderByDesc(Album::getCreateTime);
        List<Album> albums = list(wrapper);
        return albums.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    @Override
    public AlbumDTO createAlbum(Long userId, String name, String description) {
        Album album = new Album();
        album.setUserId(userId);
        album.setName(name);
        album.setDescription(description);
        album.setPhotoCount(0);
        album.setSortOrder(0);
        
        save(album);
        return convertToDTO(album);
    }
    
    @Override
    public AlbumDTO updateAlbum(Long albumId, Long userId, String name, String description) {
        Album album = getById(albumId);
        if (album == null || !album.getUserId().equals(userId)) {
            throw new RuntimeException("相册不存在或无权限");
        }
        
        album.setName(name);
        album.setDescription(description);
        updateById(album);
        return convertToDTO(album);
    }
    
    @Override
    public void deleteAlbum(Long albumId, Long userId) {
        Album album = getById(albumId);
        if (album == null || !album.getUserId().equals(userId)) {
            throw new RuntimeException("相册不存在或无权限");
        }
        
        removeById(albumId);
    }
    
    @Override
    public void updatePhotoCount(Long albumId, int count) {
        baseMapper.updatePhotoCount(albumId, count);
    }
    
    private AlbumDTO convertToDTO(Album album) {
        AlbumDTO dto = new AlbumDTO();
        BeanUtils.copyProperties(album, dto);
        return dto;
    }
}
