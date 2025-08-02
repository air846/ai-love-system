package com.ai.love.dto.auth;

import lombok.Builder;
import lombok.Data;

/**
 * 登录响应DTO
 */
@Data
@Builder
public class LoginResponse {
    
    private String token;
    @Builder.Default
    private String tokenType = "Bearer";
    private UserProfileResponse user;
}
