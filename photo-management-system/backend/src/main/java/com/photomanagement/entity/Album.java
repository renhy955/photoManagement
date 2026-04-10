package com.photomanagement.entity;

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
    
    private Integer sortOrder;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
