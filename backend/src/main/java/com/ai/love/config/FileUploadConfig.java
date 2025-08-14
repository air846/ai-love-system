package com.ai.love.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 文件上传配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "app.file-upload")
public class FileUploadConfig {

    /**
     * 上传目录
     */
    private String uploadDir = "uploads";

    /**
     * 头像上传目录
     */
    private String avatarDir = "avatars";

    /**
     * 最大文件大小（字节）
     */
    private long maxFileSize = 5 * 1024 * 1024; // 5MB

    /**
     * 允许的图片格式
     */
    private String[] allowedImageTypes = {"jpg", "jpeg", "png", "gif", "webp"};

    /**
     * 访问URL前缀
     */
    private String accessUrlPrefix = "/uploads";

    /**
     * 是否启用文件压缩
     */
    private boolean enableCompression = true;

    /**
     * 压缩质量 (0.1-1.0)
     */
    private float compressionQuality = 0.8f;

    /**
     * 头像最大尺寸
     */
    private int avatarMaxWidth = 400;
    private int avatarMaxHeight = 400;
}
