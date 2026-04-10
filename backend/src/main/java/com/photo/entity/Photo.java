package com.photo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("photo")
public class Photo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long albumId;
    private String name;
    private String originalName;
    private String filePath;
    private Long fileSize;
    private String fileType;
    private Integer width;
    private Integer height;
    private LocalDateTime uploadTime;
    private LocalDateTime updateTime;
}
