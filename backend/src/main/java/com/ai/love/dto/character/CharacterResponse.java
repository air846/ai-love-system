package com.ai.love.dto.character;

import com.ai.love.entity.AiCharacter;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI角色响应DTO
 */
@Data
@Builder
public class CharacterResponse {

    private Long id;
    private String name;
    private String description;
    private String avatarUrl;
    private String personality;
    private String personalityDescription;
    private String gender;
    private String genderDescription;
    private Integer age;
    private String backgroundStory;
    private String systemPrompt;
    private Double temperature;
    private Integer maxTokens;
    private String status;
    private Integer usageCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /**
     * 从实体转换
     */
    public static CharacterResponse fromEntity(AiCharacter character) {
        return CharacterResponse.builder()
                .id(character.getId())
                .name(character.getName())
                .description(character.getDescription())
                .avatarUrl(character.getAvatarUrl())
                .personality(character.getPersonality().name())
                .personalityDescription(character.getPersonality().getDescription())
                .gender(character.getGender().name())
                .genderDescription(character.getGender().getDescription())
                .age(character.getAge())
                .backgroundStory(character.getBackgroundStory())
                .systemPrompt(character.getSystemPrompt())
                .temperature(character.getTemperature())
                .maxTokens(character.getMaxTokens())
                .status(character.getStatus().name())
                .usageCount(character.getUsageCount())
                .createdAt(character.getCreatedAt())
                .updatedAt(character.getUpdatedAt())
                .build();
    }
}
