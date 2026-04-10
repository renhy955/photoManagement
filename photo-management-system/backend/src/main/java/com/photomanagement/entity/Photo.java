package com.photomanagement.entity;

import com.baomidou.mybatisplus.annotation.*;
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
    
    private String url;
    
    private String thumbnailUrl;
    
    private Long fileSize;
    
    private Integer width;
    
    private Integer height;
    
    private String mimeType;
    
    private String description;
    
    @TableField("is_deleted")
    @TableLogic
    private Integer isDeleted;
    
    private LocalDateTime deleteTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
