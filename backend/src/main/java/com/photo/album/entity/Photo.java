package com.photo.album.entity;

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
    
    private String originalName;
    
    private String url;
    
    private String thumbnailUrl;
    
    private Long size;
    
    private String format;
    
    private Integer width;
    
    private Integer height;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableLogic
    private Integer isDeleted;
}
