package com.ai.love.service;

import com.ai.love.dto.auth.LoginRequest;
import com.ai.love.dto.auth.LoginResponse;
import com.ai.love.dto.auth.RegisterRequest;
import com.ai.love.dto.auth.UpdateUserProfileRequest;
import com.ai.love.dto.auth.UserPreferencesDto;
import com.ai.love.dto.auth.UserProfileResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ai.love.entity.User;
import com.ai.love.exception.BusinessException;
import com.ai.love.repository.UserRepository;
import com.ai.love.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 认证服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    /**
     * 用户注册
     */
    @Transactional
    public void register(RegisterRequest request) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("邮箱已被注册");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setStatus(User.UserStatus.ACTIVE);

        userRepository.save(user);
        log.info("用户注册成功: {}", user.getUsername());
    }

    /**
     * 用户登录
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        // 认证用户
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 获取用户信息
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 更新登录信息 - 使用原生SQL避免触发器问题
        userRepository.updateLoginInfo(user.getId(), LocalDateTime.now());

        // 生成JWT令牌
        String token = jwtUtil.generateToken(user.getUsername(), user.getId());

        // 构建响应
        UserProfileResponse userProfile = UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .status(user.getStatus().name())
                .loginCount(user.getLoginCount())
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .build();

        log.info("用户登录成功: {}", user.getUsername());

        return LoginResponse.builder()
                .token(token)
                .user(userProfile)
                .build();
    }

    /**
     * 获取当前用户信息
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUser() {
        User user = getCurrentUserEntity();

        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .status(user.getStatus().name())
                .loginCount(user.getLoginCount())
                .emailVerified(user.getEmailVerified())
                .phone(user.getPhone())
                .birthDate(user.getBirthDate())
                .gender(user.getGender())
                .genderDescription(user.getGender() != null ? user.getGender().getDescription() : null)
                .bio(user.getBio())
                .lastLoginIp(user.getLastLoginIp())
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    /**
     * 更新用户信息
     */
    @Transactional
    public UserProfileResponse updateProfile(UpdateUserProfileRequest request) {
        User user = getCurrentUserEntity();

        // 检查邮箱是否已被其他用户使用
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new BusinessException("邮箱已被其他用户使用");
            }
            user.setEmail(request.getEmail());
            user.setEmailVerified(false); // 邮箱变更后需要重新验证
        }

        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getBirthDate() != null) {
            user.setBirthDate(request.getBirthDate());
        }
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        if (request.getPreferences() != null) {
            user.setPreferences(request.getPreferences());
        }

        userRepository.save(user);
        log.info("用户信息更新成功: {}", user.getUsername());

        return getCurrentUser();
    }

    /**
     * 更新用户信息（兼容旧版本）
     */
    @Transactional
    public UserProfileResponse updateProfile(UserProfileResponse request) {
        User user = getCurrentUserEntity();

        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }

        userRepository.save(user);
        log.info("用户信息更新成功: {}", user.getUsername());

        return getCurrentUser();
    }

    /**
     * 修改密码
     */
    @Transactional
    public void changePassword(String oldPassword, String newPassword) {
        User user = getCurrentUserEntity();
        
        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new BusinessException("原密码错误");
        }
        
        // 更新密码
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        log.info("用户密码修改成功: {}", user.getUsername());
    }

    /**
     * 刷新令牌
     */
    public String refreshToken(String token) {
        try {
            return jwtUtil.refreshToken(token);
        } catch (Exception e) {
            throw new BusinessException("令牌刷新失败");
        }
    }

    /**
     * 获取当前用户实体
     */
    public User getCurrentUserEntity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("用户未登录");
        }

        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));
    }

    /**
     * 获取当前用户ID
     */
    public Long getCurrentUserId() {
        return getCurrentUserEntity().getId();
    }

    /**
     * 获取用户偏好设置
     */
    @Transactional(readOnly = true)
    public UserPreferencesDto getUserPreferences() {
        User user = getCurrentUserEntity();
        String preferencesJson = user.getPreferences();

        if (preferencesJson == null || preferencesJson.trim().isEmpty()) {
            return new UserPreferencesDto(); // 返回默认偏好设置
        }

        try {
            return objectMapper.readValue(preferencesJson, UserPreferencesDto.class);
        } catch (JsonProcessingException e) {
            log.warn("解析用户偏好设置失败: userId={}, error={}", user.getId(), e.getMessage());
            return new UserPreferencesDto(); // 返回默认偏好设置
        }
    }

    /**
     * 更新用户偏好设置
     */
    @Transactional
    public UserPreferencesDto updateUserPreferences(UserPreferencesDto preferences) {
        User user = getCurrentUserEntity();

        try {
            String preferencesJson = objectMapper.writeValueAsString(preferences);
            user.setPreferences(preferencesJson);
            userRepository.save(user);

            log.info("用户偏好设置更新成功: userId={}", user.getId());
            return preferences;
        } catch (JsonProcessingException e) {
            log.error("保存用户偏好设置失败: userId={}, error={}", user.getId(), e.getMessage());
            throw new BusinessException("偏好设置保存失败");
        }
    }

    /**
     * 重置用户偏好设置
     */
    @Transactional
    public UserPreferencesDto resetUserPreferences() {
        User user = getCurrentUserEntity();
        UserPreferencesDto defaultPreferences = new UserPreferencesDto();

        try {
            String preferencesJson = objectMapper.writeValueAsString(defaultPreferences);
            user.setPreferences(preferencesJson);
            userRepository.save(user);

            log.info("用户偏好设置重置成功: userId={}", user.getId());
            return defaultPreferences;
        } catch (JsonProcessingException e) {
            log.error("重置用户偏好设置失败: userId={}, error={}", user.getId(), e.getMessage());
            throw new BusinessException("偏好设置重置失败");
        }
    }
}
