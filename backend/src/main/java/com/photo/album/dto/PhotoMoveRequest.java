package com.photo.album.dto;

import lombok.Data;
import java.util.List;

@Data
public class PhotoMoveRequest {
    private Long albumId;
    
    private List<Long> photoIds;
}
