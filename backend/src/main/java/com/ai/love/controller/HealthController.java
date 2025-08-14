package com.ai.love.controller;

import com.ai.love.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 */
@Slf4j
@RestController
@RequestMapping("/health")
@Tag(name = "健康检查", description = "系统健康状态检查接口")
public class HealthController {

    /**
     * 系统健康检查
     */
    @GetMapping
    @Operation(summary = "健康检查", description = "检查系统是否正常运行")
    public ApiResponse<Map<String, Object>> health() {
        Map<String, Object> healthInfo = new HashMap<>();
        healthInfo.put("status", "UP");
        healthInfo.put("timestamp", LocalDateTime.now());
        healthInfo.put("service", "AI Love System");
        healthInfo.put("version", "1.0.0");
        
        log.info("健康检查请求 - 系统状态正常");
        return ApiResponse.success("系统运行正常", healthInfo);
    }

    /**
     * 简单的ping测试
     */
    @GetMapping("/ping")
    @Operation(summary = "Ping测试", description = "简单的连通性测试")
    public ApiResponse<String> ping() {
        log.info("Ping请求");
        return ApiResponse.success("pong");
    }
}
