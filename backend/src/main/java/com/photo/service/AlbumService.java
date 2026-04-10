package com.photo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.photo.common.Result;
import com.photo.entity.Album;
import com.photo.mapper.AlbumMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlbumService {

    @Autowired
    private AlbumMapper albumMapper;

    public Result<List<Album>> getAlbumList(Long userId) {
        List<Album> list = albumMapper.selectList(
                new LambdaQueryWrapper<Album>()
                        .eq(Album::getUserId, userId)
                        .orderByDesc(Album::getCreateTime)
        );
        return Result.success(list);
    }

    public Result<Album> getAlbumDetail(Long id, Long userId) {
        Album album = albumMapper.selectOne(
                new LambdaQueryWrapper<Album>()
                        .eq(Album::getId, id)
                        .eq(Album::getUserId, userId)
        );
        if (album == null) {
            return Result.error("相册不存在");
        }
        return Result.success(album);
    }

    public Result<?> createAlbum(Album album, Long userId) {
        album.setUserId(userId);
        albumMapper.insert(album);
        return Result.success();
    }

    public Result<?> updateAlbum(Long id, Album album, Long userId) {
        Album existing = albumMapper.selectOne(
                new LambdaQueryWrapper<Album>()
                        .eq(Album::getId, id)
                        .eq(Album::getUserId, userId)
        );
        if (existing == null) {
            return Result.error("相册不存在");
        }

        albumMapper.update(null,
                new LambdaUpdateWrapper<Album>()
                        .eq(Album::getId, id)
                        .eq(Album::getUserId, userId)
                        .set(Album::getName, album.getName())
                        .set(Album::getDescription, album.getDescription())
        );
        return Result.success();
    }

    public Result<?> deleteAlbum(Long id, Long userId) {
        albumMapper.delete(
                new LambdaQueryWrapper<Album>()
                        .eq(Album::getId, id)
                        .eq(Album::getUserId, userId)
        );
        return Result.success();
    }
}
