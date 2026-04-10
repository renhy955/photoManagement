package com.photo.album.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("album")
public class Album {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String name;
    
    private String description;
    
    private String coverUrl;
    
    private Integer photoCount;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableLogic
    private Integer isDeleted;
}
