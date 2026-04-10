package com.photo.album.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class PhotoRenameRequest {
    @NotBlank(message = "照片名称不能为空")
    private String name;
}
