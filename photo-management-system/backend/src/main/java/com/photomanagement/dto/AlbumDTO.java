package com.photomanagement.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlbumDTO {
    private Long id;
    private String name;
    private String description;
    private String coverUrl;
    private Integer photoCount;
    private Integer sortOrder;
    private LocalDateTime createTime;
}
