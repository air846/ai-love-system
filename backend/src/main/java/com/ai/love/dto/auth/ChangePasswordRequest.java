package com.ai.love.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改密码请求DTO
 */
@Data
public class ChangePasswordRequest {

    @NotBlank(message = "原密码不能为空")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 100, message = "新密码长度必须在6-100个字符之间")
    private String newPassword;

    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    /**
     * 验证新密码是否一致
     */
    public boolean isNewPasswordMatched() {
        return newPassword != null && newPassword.equals(confirmPassword);
    }
}
