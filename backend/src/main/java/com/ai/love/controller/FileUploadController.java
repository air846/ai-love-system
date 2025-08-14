package com.ai.love.controller;

import com.ai.love.common.ApiResponse;
import com.ai.love.exception.BusinessException;
import com.ai.love.service.AuthService;
import com.ai.love.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传控制器
 */
@Slf4j
@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
@Tag(name = "文件上传", description = "文件上传相关接口")
public class FileUploadController {

    private final FileUploadService fileUploadService;
    private final AuthService authService;

    /**
     * 上传头像
     */
    @PostMapping("/avatar")
    @Operation(summary = "上传头像", description = "上传用户头像图片")
    public ApiResponse<String> uploadAvatar(
            @Parameter(description = "头像文件", required = true)
            @RequestParam("file") MultipartFile file) {
        try {
            Long userId = authService.getCurrentUserId();
            String avatarUrl = fileUploadService.uploadAvatar(file, userId);
            return ApiResponse.success("头像上传成功", avatarUrl);
        } catch (BusinessException e) {
            log.warn("头像上传失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            log.error("头像上传时发生未知错误", e);
            return ApiResponse.error("上传失败，请稍后重试");
        }
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/file")
    @Operation(summary = "删除文件", description = "删除指定的文件")
    public ApiResponse<String> deleteFile(
            @Parameter(description = "文件路径", required = true)
            @RequestParam("filePath") String filePath) {
        
        boolean success = fileUploadService.deleteFile(filePath);
        
        if (success) {
            return ApiResponse.success("文件删除成功");
        } else {
            return ApiResponse.error("文件删除失败");
        }
    }
}
