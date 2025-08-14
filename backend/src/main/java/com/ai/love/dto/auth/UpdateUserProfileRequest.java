package com.ai.love.dto.auth;

import com.ai.love.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * 更新用户信息请求DTO
 */
@Data
public class UpdateUserProfileRequest {

    @Size(max = 50, message = "昵称长度不能超过50个字符")
    private String nickname;

    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;

    @Size(max = 500, message = "头像URL长度不能超过500个字符")
    private String avatarUrl;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Past(message = "生日必须是过去的日期")
    private LocalDate birthDate;

    private User.Gender gender;

    @Size(max = 500, message = "个人简介不能超过500个字符")
    private String bio;

    @Size(max = 2000, message = "偏好设置数据过长")
    private String preferences;
}
