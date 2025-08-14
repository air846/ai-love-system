package com.ai.love.dto.auth;

import com.ai.love.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户信息响应DTO
 */
@Data
@Builder
public class UserProfileResponse {

    private Long id;
    private String username;
    private String email;
    private String nickname;
    private String avatarUrl;
    private String status;
    private Integer loginCount;
    private Boolean emailVerified;
    private String phone;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    private User.Gender gender;
    private String genderDescription;
    private String bio;
    private String lastLoginIp;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
