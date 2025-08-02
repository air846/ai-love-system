package com.ai.love.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * AI角色实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ai_characters", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_name", columnList = "name")
})
public class AiCharacter extends BaseEntity {

    @NotBlank(message = "角色名称不能为空")
    @Size(max = 50, message = "角色名称不能超过50个字符")
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Size(max = 1000, message = "角色描述不能超过1000个字符")
    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "personality", nullable = false)
    private Personality personality = Personality.FRIENDLY;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender = Gender.FEMALE;

    @Column(name = "age")
    private Integer age;

    @Size(max = 2000, message = "背景故事不能超过2000个字符")
    @Column(name = "background_story", length = 2000)
    private String backgroundStory;

    @Column(name = "system_prompt", columnDefinition = "TEXT")
    private String systemPrompt;

    @Column(name = "temperature", nullable = false)
    private Double temperature = 0.7;

    @Column(name = "max_tokens", nullable = false)
    private Integer maxTokens = 2048;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CharacterStatus status = CharacterStatus.ACTIVE;

    @Column(name = "usage_count", nullable = false)
    private Integer usageCount = 0;

    // 关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "character", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Conversation> conversations;

    /**
     * 性格枚举
     */
    public enum Personality {
        FRIENDLY("友善"),
        SHY("害羞"),
        OUTGOING("外向"),
        MYSTERIOUS("神秘"),
        PLAYFUL("顽皮"),
        SERIOUS("严肃"),
        ROMANTIC("浪漫"),
        INTELLECTUAL("知性");

        private final String description;

        Personality(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 性别枚举
     */
    public enum Gender {
        MALE("男性"),
        FEMALE("女性"),
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
     * 角色状态枚举
     */
    public enum CharacterStatus {
        ACTIVE("激活"),
        INACTIVE("未激活"),
        DELETED("已删除");

        private final String description;

        CharacterStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 增加使用次数
     */
    public void incrementUsage() {
        this.usageCount = this.usageCount + 1;
    }

    /**
     * 生成系统提示词
     */
    public String generateSystemPrompt() {
        if (systemPrompt != null && !systemPrompt.trim().isEmpty()) {
            return systemPrompt;
        }

        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一个名叫").append(name).append("的AI角色。");
        
        if (description != null && !description.trim().isEmpty()) {
            prompt.append("你的描述是：").append(description).append("。");
        }
        
        prompt.append("你的性格是").append(personality.getDescription()).append("。");
        prompt.append("你的性别是").append(gender.getDescription()).append("。");
        
        if (age != null) {
            prompt.append("你的年龄是").append(age).append("岁。");
        }
        
        if (backgroundStory != null && !backgroundStory.trim().isEmpty()) {
            prompt.append("你的背景故事：").append(backgroundStory).append("。");
        }
        
        prompt.append("请以这个角色的身份与用户进行对话，保持角色的一致性和真实感。");
        
        return prompt.toString();
    }
}
