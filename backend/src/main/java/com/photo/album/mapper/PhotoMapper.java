package com.photo.album.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.photo.album.entity.Photo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PhotoMapper extends BaseMapper<Photo> {
}
