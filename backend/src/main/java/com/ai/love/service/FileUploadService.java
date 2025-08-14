package com.ai.love.service;

import com.ai.love.config.FileUploadConfig;
import com.ai.love.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;

/**
 * 文件上传服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final FileUploadConfig fileUploadConfig;

    /**
     * 上传头像
     */
    public String uploadAvatar(MultipartFile file, Long userId) {
        validateImageFile(file);
        
        try {
            // 计算日期子目录（与保存目录一致，确保访问路径正确）
            String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

            // 创建上传目录
            String uploadDir = createUploadDirectory(fileUploadConfig.getAvatarDir());
            
            // 生成文件名
            String fileName = generateFileName(file.getOriginalFilename(), userId);
            Path filePath = Paths.get(uploadDir, fileName);
            
            // 直接保存文件，不进行图片处理
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // 返回访问URL，必须包含日期子目录
            String relativePath = fileUploadConfig.getAvatarDir() + "/" + dateDir + "/" + fileName;
            String accessUrl = fileUploadConfig.getAccessUrlPrefix() + "/" + relativePath;
            
            log.info("头像上传成功: userId={}, fileName={}, accessUrl={}", userId, fileName, accessUrl);
            return accessUrl;
            
        } catch (IOException e) {
            log.error("头像上传失败: userId={}, error={}", userId, e.getMessage(), e);
            throw new BusinessException("头像上传失败: " + e.getMessage());
        }
    }

    /**
     * 验证图片文件
     */
    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        if (file.getSize() > fileUploadConfig.getMaxFileSize()) {
            throw new BusinessException("文件大小不能超过 " + 
                (fileUploadConfig.getMaxFileSize() / 1024 / 1024) + "MB");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new BusinessException("文件名不能为空");
        }

        String fileExtension = getFileExtension(fileName).toLowerCase();
        if (!Arrays.asList(fileUploadConfig.getAllowedImageTypes()).contains(fileExtension)) {
            throw new BusinessException("不支持的文件格式，仅支持: " + 
                String.join(", ", fileUploadConfig.getAllowedImageTypes()));
        }
    }

    /**
     * 创建上传目录
     */
    private String createUploadDirectory(String subDir) throws IOException {
        String baseDir = fileUploadConfig.getUploadDir();
        String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String fullDir = baseDir + "/" + subDir + "/" + dateDir;
        
        Path dirPath = Paths.get(fullDir);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }
        
        return fullDir;
    }

    /**
     * 生成文件名
     */
    private String generateFileName(String originalFileName, Long userId) {
        String fileExtension = getFileExtension(originalFileName);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return String.format("avatar_%d_%s_%s.%s", userId, timestamp, uuid, fileExtension);
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    /**
     * 处理头像图片（压缩和调整尺寸）
     */
    private BufferedImage processAvatarImage(MultipartFile file) throws IOException {
        BufferedImage originalImage = ImageIO.read(file.getInputStream());

        if (originalImage == null) {
            throw new BusinessException("无法读取图片文件，可能是不支持的格式或文件已损坏");
        }

        // 计算新尺寸
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        int maxWidth = fileUploadConfig.getAvatarMaxWidth();
        int maxHeight = fileUploadConfig.getAvatarMaxHeight();

        int newWidth = originalWidth;
        int newHeight = originalHeight;

        if (originalWidth > maxWidth || originalHeight > maxHeight) {
            double widthRatio = (double) maxWidth / originalWidth;
            double heightRatio = (double) maxHeight / originalHeight;
            double ratio = Math.min(widthRatio, heightRatio);

            newWidth = (int) (originalWidth * ratio);
            newHeight = (int) (originalHeight * ratio);
        }

        // 创建新图片，保留原始图片的类型以支持透明度
        int imageType = originalImage.getType();
        if (imageType == 0 || imageType == BufferedImage.TYPE_CUSTOM) {
            imageType = originalImage.getTransparency() == Transparency.OPAQUE ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        }
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, imageType);

        Graphics2D g2d = resizedImage.createGraphics();

        // 设置渲染质量
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 绘制图片
        g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g2d.dispose();

        return resizedImage;
    }

    /**
     * 删除文件
     */
    public boolean deleteFile(String filePath) {
        try {
            if (filePath != null && filePath.startsWith(fileUploadConfig.getAccessUrlPrefix())) {
                String relativePath = filePath.substring(fileUploadConfig.getAccessUrlPrefix().length());
                Path fullPath = Paths.get(fileUploadConfig.getUploadDir() + relativePath);
                
                if (Files.exists(fullPath)) {
                    Files.delete(fullPath);
                    log.info("文件删除成功: {}", filePath);
                    return true;
                }
            }
        } catch (IOException e) {
            log.error("文件删除失败: filePath={}, error={}", filePath, e.getMessage(), e);
        }
        return false;
    }
}
