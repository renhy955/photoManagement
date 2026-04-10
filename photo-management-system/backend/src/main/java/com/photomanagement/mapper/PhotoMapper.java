package com.photomanagement.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.photomanagement.entity.Photo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface PhotoMapper extends BaseMapper<Photo> {
    
    @Update("UPDATE photo SET album_id = #{newAlbumId} WHERE id = #{photoId} AND user_id = #{userId}")
    void movePhoto(@Param("photoId") Long photoId, @Param("newAlbumId") Long newAlbumId, @Param("userId") Long userId);
    
    @Update("UPDATE photo SET name = #{name} WHERE id = #{photoId} AND user_id = #{userId}")
    void renamePhoto(@Param("photoId") Long photoId, @Param("name") String name, @Param("userId") Long userId);
}
