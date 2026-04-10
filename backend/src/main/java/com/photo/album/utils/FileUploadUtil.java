package com.photo.album.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
public class FileUploadUtil {
    
    @Value("${file.upload.path}")
    private String uploadPath;
    
    @Value("${file.upload.allowed-types}")
    private String allowedTypes;
    
    @Value("${file.upload.max-size}")
    private Long maxSize;
    
    public String uploadFile(MultipartFile file, Long userId) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        
        if (!isAllowedType(fileExtension)) {
            throw new IllegalArgumentException("不支持的文件类型: " + fileExtension);
        }
        
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("文件大小超过限制");
        }
        
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        String fileName = UUID.randomUUID().toString() + "." + fileExtension;
        String relativePath = userId + "/" + datePath + "/" + fileName;
        
        Path fullPath = Paths.get(uploadPath, relativePath);
        Files.createDirectories(fullPath.getParent());
        
        file.transferTo(fullPath.toFile());
        
        return "/uploads/" + relativePath;
    }
    
    public void deleteFile(String fileUrl) {
        if (fileUrl != null && fileUrl.startsWith("/uploads/")) {
            String relativePath = fileUrl.substring("/uploads/".length());
            Path filePath = Paths.get(uploadPath, relativePath);
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public int[] getImageDimensions(MultipartFile file) throws IOException {
        BufferedImage image = ImageIO.read(file.getInputStream());
        if (image != null) {
            return new int[]{image.getWidth(), image.getHeight()};
        }
        return new int[]{0, 0};
    }
    
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
    
    private boolean isAllowedType(String extension) {
        List<String> allowedList = Arrays.asList(allowedTypes.split(","));
        return allowedList.contains(extension.toLowerCase());
    }
}
