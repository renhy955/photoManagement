package com.photomanagement.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PhotoDTO {
    private Long id;
    private Long albumId;
    private String name;
    private String url;
    private String thumbnailUrl;
    private Long fileSize;
    private Integer width;
    private Integer height;
    private String mimeType;
    private String description;
    private LocalDateTime createTime;
}
