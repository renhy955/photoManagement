package com.photomanagement;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.photomanagement.mapper")
public class PhotoManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(PhotoManagementApplication.class, args);
    }
}
