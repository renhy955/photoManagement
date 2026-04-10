package com.photo.album.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class AlbumRequest {
    @NotBlank(message = "相册名称不能为空")
    private String name;
    
    private String description;
    
    private String coverUrl;
}
