package com.photo.album.dto;

import lombok.Data;
import java.util.List;

@Data
public class PhotoCopyRequest {
    private Long albumId;
    
    private List<Long> photoIds;
}
