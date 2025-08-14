package com.ai.love.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_username", columnList = "username", unique = true),
    @Index(name = "idx_email", columnList = "email", unique = true)
})
public class User extends BaseEntity {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank(message = "密码不能为空")
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "nickname", length = 50)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "last_login_ip", length = 45)
    private String lastLoginIp;

    @Column(name = "login_count", nullable = false)
    private Integer loginCount = 0;

    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "preferences", columnDefinition = "JSONB")
    private String preferences;

    // 关联关系
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AiCharacter> characters;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Conversation> conversations;

    /**
     * 用户状态枚举
     */
    public enum UserStatus {
        ACTIVE("激活"),
        INACTIVE("未激活"),
        SUSPENDED("暂停"),
        DELETED("已删除");

        private final String description;

        UserStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 用户性别枚举
     */
    public enum Gender {
        MALE("男"),
        FEMALE("女"),
        OTHER("其他");

        private final String description;

        Gender(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 更新登录信息
     */
    public void updateLoginInfo() {
        this.lastLoginAt = LocalDateTime.now();
        this.loginCount = this.loginCount + 1;
    }

    /**
     * 更新登录信息（包含IP地址）
     */
    public void updateLoginInfo(String ipAddress) {
        this.lastLoginAt = LocalDateTime.now();
        this.lastLoginIp = ipAddress;
        this.loginCount = this.loginCount + 1;
    }
}
