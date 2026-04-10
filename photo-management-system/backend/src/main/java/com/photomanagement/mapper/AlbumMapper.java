package com.photomanagement.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.photomanagement.entity.Album;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AlbumMapper extends BaseMapper<Album> {
    
    @Update("UPDATE album SET photo_count = photo_count + #{count} WHERE id = #{albumId}")
    void updatePhotoCount(@Param("albumId") Long albumId, @Param("count") int count);
}
