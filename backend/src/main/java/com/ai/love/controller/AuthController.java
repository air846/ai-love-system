package com.ai.love.controller;

import com.ai.love.common.ApiResponse;
import com.ai.love.dto.auth.*;
import com.ai.love.exception.BusinessException;
import com.ai.love.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户注册、登录、信息管理等接口")
public class AuthController {

    private final AuthService authService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "创建新用户账户")
    public ApiResponse<String> register(@Valid @RequestBody RegisterRequest request) {
        // 验证密码一致性
        if (!request.isPasswordMatched()) {
            throw new BusinessException("两次输入的密码不一致");
        }

        authService.register(request);
        return ApiResponse.success("注册成功");
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录获取访问令牌")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ApiResponse.success("登录成功", response);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/profile")
    @Operation(summary = "获取用户信息", description = "获取当前登录用户的详细信息")
    public ApiResponse<UserProfileResponse> getProfile() {
        UserProfileResponse profile = authService.getCurrentUser();
        return ApiResponse.success(profile);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/profile")
    @Operation(summary = "更新用户信息", description = "更新当前用户的个人信息")
    public ApiResponse<UserProfileResponse> updateProfile(@RequestBody UserProfileResponse request) {
        UserProfileResponse profile = authService.updateProfile(request);
        return ApiResponse.success("信息更新成功", profile);
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    @Operation(summary = "修改密码", description = "修改当前用户的登录密码")
    public ApiResponse<String> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        // 验证新密码一致性
        if (!request.isNewPasswordMatched()) {
            throw new BusinessException("两次输入的新密码不一致");
        }

        authService.changePassword(request.getOldPassword(), request.getNewPassword());
        return ApiResponse.success("密码修改成功");
    }

    /**
     * 刷新令牌
     */
    @PostMapping("/refresh")
    @Operation(summary = "刷新令牌", description = "刷新访问令牌")
    public ApiResponse<String> refreshToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BusinessException("无效的令牌格式");
        }

        String token = authHeader.substring(7);
        String newToken = authService.refreshToken(token);
        return ApiResponse.success("令牌刷新成功", newToken);
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户登出（客户端需清除令牌）")
    public ApiResponse<String> logout() {
        // 由于使用JWT，服务端无状态，登出主要由客户端处理
        // 这里可以记录登出日志或进行其他业务处理
        log.info("用户登出: {}", authService.getCurrentUserEntity().getUsername());
        return ApiResponse.success("登出成功");
    }
}
