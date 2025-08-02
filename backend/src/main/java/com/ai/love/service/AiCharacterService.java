package com.ai.love.service;

import com.ai.love.dto.character.CreateCharacterRequest;
import com.ai.love.dto.character.UpdateCharacterRequest;
import com.ai.love.dto.character.CharacterResponse;
import com.ai.love.entity.AiCharacter;
import com.ai.love.entity.User;
import com.ai.love.exception.BusinessException;
import com.ai.love.exception.ResourceNotFoundException;
import com.ai.love.repository.AiCharacterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AI角色服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiCharacterService {

    private final AiCharacterRepository aiCharacterRepository;
    private final AuthService authService;
    private final ZhipuAiClient zhipuAiClient;

    /**
     * 创建AI角色
     */
    @Transactional
    public CharacterResponse createCharacter(CreateCharacterRequest request) {
        User currentUser = authService.getCurrentUserEntity();
        
        // 检查角色名称是否重复
        if (aiCharacterRepository.existsByUserIdAndName(currentUser.getId(), request.getName())) {
            throw new BusinessException("角色名称已存在");
        }
        
        // 检查用户角色数量限制
        long characterCount = aiCharacterRepository.countByUserId(currentUser.getId());
        if (characterCount >= 10) { // 限制每个用户最多10个角色
            throw new BusinessException("角色数量已达上限（10个）");
        }
        
        AiCharacter character = new AiCharacter();
        character.setUser(currentUser);
        character.setName(request.getName());
        character.setDescription(request.getDescription());
        character.setAvatarUrl(request.getAvatarUrl());
        character.setPersonality(request.getPersonality());
        character.setGender(request.getGender());
        character.setAge(request.getAge());
        character.setBackgroundStory(request.getBackgroundStory());
        character.setSystemPrompt(request.getSystemPrompt());
        character.setTemperature(request.getTemperature() != null ? request.getTemperature() : 0.7);
        character.setMaxTokens(request.getMaxTokens() != null ? request.getMaxTokens() : 2048);
        character.setStatus(AiCharacter.CharacterStatus.ACTIVE);
        
        AiCharacter savedCharacter = aiCharacterRepository.save(character);
        log.info("AI角色创建成功: 用户={}, 角色={}", currentUser.getUsername(), savedCharacter.getName());
        
        return CharacterResponse.fromEntity(savedCharacter);
    }

    /**
     * 更新AI角色
     */
    @Transactional
    public CharacterResponse updateCharacter(Long characterId, UpdateCharacterRequest request) {
        User currentUser = authService.getCurrentUserEntity();
        
        AiCharacter character = aiCharacterRepository.findById(characterId)
                .orElseThrow(() -> new ResourceNotFoundException("AI角色不存在"));
        
        // 验证角色所有权
        if (!character.getUser().getId().equals(currentUser.getId())) {
            throw new BusinessException("无权修改此AI角色");
        }
        
        // 检查名称重复（排除当前角色）
        if (request.getName() != null && !request.getName().equals(character.getName())) {
            if (aiCharacterRepository.existsByUserIdAndName(currentUser.getId(), request.getName())) {
                throw new BusinessException("角色名称已存在");
            }
            character.setName(request.getName());
        }
        
        // 更新其他字段
        if (request.getDescription() != null) {
            character.setDescription(request.getDescription());
        }
        if (request.getAvatarUrl() != null) {
            character.setAvatarUrl(request.getAvatarUrl());
        }
        if (request.getPersonality() != null) {
            character.setPersonality(request.getPersonality());
        }
        if (request.getGender() != null) {
            character.setGender(request.getGender());
        }
        if (request.getAge() != null) {
            character.setAge(request.getAge());
        }
        if (request.getBackgroundStory() != null) {
            character.setBackgroundStory(request.getBackgroundStory());
        }
        if (request.getSystemPrompt() != null) {
            character.setSystemPrompt(request.getSystemPrompt());
        }
        if (request.getTemperature() != null) {
            character.setTemperature(request.getTemperature());
        }
        if (request.getMaxTokens() != null) {
            character.setMaxTokens(request.getMaxTokens());
        }
        
        AiCharacter updatedCharacter = aiCharacterRepository.save(character);
        log.info("AI角色更新成功: 用户={}, 角色={}", currentUser.getUsername(), updatedCharacter.getName());
        
        return CharacterResponse.fromEntity(updatedCharacter);
    }

    /**
     * 获取用户的AI角色列表
     */
    @Transactional(readOnly = true)
    public List<CharacterResponse> getUserCharacters() {
        User currentUser = authService.getCurrentUserEntity();
        List<AiCharacter> characters = aiCharacterRepository.findActiveCharactersByUserId(currentUser.getId());
        
        return characters.stream()
                .map(CharacterResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 获取AI角色详情
     */
    @Transactional(readOnly = true)
    public CharacterResponse getCharacter(Long characterId) {
        User currentUser = authService.getCurrentUserEntity();
        
        AiCharacter character = aiCharacterRepository.findById(characterId)
                .orElseThrow(() -> new ResourceNotFoundException("AI角色不存在"));
        
        // 验证角色所有权
        if (!character.getUser().getId().equals(currentUser.getId())) {
            throw new BusinessException("无权访问此AI角色");
        }
        
        return CharacterResponse.fromEntity(character);
    }

    /**
     * 删除AI角色
     */
    @Transactional
    public void deleteCharacter(Long characterId) {
        User currentUser = authService.getCurrentUserEntity();
        
        AiCharacter character = aiCharacterRepository.findById(characterId)
                .orElseThrow(() -> new ResourceNotFoundException("AI角色不存在"));
        
        // 验证角色所有权
        if (!character.getUser().getId().equals(currentUser.getId())) {
            throw new BusinessException("无权删除此AI角色");
        }
        
        // 软删除
        character.setStatus(AiCharacter.CharacterStatus.DELETED);
        aiCharacterRepository.save(character);
        
        log.info("AI角色删除成功: 用户={}, 角色={}", currentUser.getUsername(), character.getName());
    }

    /**
     * 测试AI角色对话
     */
    @Transactional(readOnly = true)
    public String testCharacter(Long characterId, String testMessage) {
        User currentUser = authService.getCurrentUserEntity();
        
        AiCharacter character = aiCharacterRepository.findById(characterId)
                .orElseThrow(() -> new ResourceNotFoundException("AI角色不存在"));
        
        // 验证角色所有权
        if (!character.getUser().getId().equals(currentUser.getId())) {
            throw new BusinessException("无权测试此AI角色");
        }
        
        try {
            String response = zhipuAiClient.chatWithCharacter(testMessage, character.generateSystemPrompt());
            log.info("AI角色测试成功: 用户={}, 角色={}", currentUser.getUsername(), character.getName());
            return response;
        } catch (Exception e) {
            log.error("AI角色测试失败: ", e);
            throw new BusinessException("角色测试失败: " + e.getMessage());
        }
    }

    /**
     * 搜索AI角色
     */
    @Transactional(readOnly = true)
    public Page<CharacterResponse> searchCharacters(String keyword, Pageable pageable) {
        Page<AiCharacter> characters = aiCharacterRepository.searchCharacters(keyword, pageable);
        return characters.map(CharacterResponse::fromEntity);
    }

    /**
     * 获取热门AI角色
     */
    @Transactional(readOnly = true)
    public Page<CharacterResponse> getPopularCharacters(Pageable pageable) {
        Page<AiCharacter> characters = aiCharacterRepository.findMostPopularCharacters(pageable);
        return characters.map(CharacterResponse::fromEntity);
    }

    /**
     * 复制AI角色
     */
    @Transactional
    public CharacterResponse cloneCharacter(Long characterId, String newName) {
        User currentUser = authService.getCurrentUserEntity();
        
        AiCharacter originalCharacter = aiCharacterRepository.findById(characterId)
                .orElseThrow(() -> new ResourceNotFoundException("AI角色不存在"));
        
        // 验证角色所有权
        if (!originalCharacter.getUser().getId().equals(currentUser.getId())) {
            throw new BusinessException("无权复制此AI角色");
        }
        
        // 检查新名称是否重复
        if (aiCharacterRepository.existsByUserIdAndName(currentUser.getId(), newName)) {
            throw new BusinessException("角色名称已存在");
        }
        
        // 创建副本
        AiCharacter clonedCharacter = new AiCharacter();
        clonedCharacter.setUser(currentUser);
        clonedCharacter.setName(newName);
        clonedCharacter.setDescription(originalCharacter.getDescription());
        clonedCharacter.setAvatarUrl(originalCharacter.getAvatarUrl());
        clonedCharacter.setPersonality(originalCharacter.getPersonality());
        clonedCharacter.setGender(originalCharacter.getGender());
        clonedCharacter.setAge(originalCharacter.getAge());
        clonedCharacter.setBackgroundStory(originalCharacter.getBackgroundStory());
        clonedCharacter.setSystemPrompt(originalCharacter.getSystemPrompt());
        clonedCharacter.setTemperature(originalCharacter.getTemperature());
        clonedCharacter.setMaxTokens(originalCharacter.getMaxTokens());
        clonedCharacter.setStatus(AiCharacter.CharacterStatus.ACTIVE);
        
        AiCharacter savedCharacter = aiCharacterRepository.save(clonedCharacter);
        log.info("AI角色复制成功: 用户={}, 原角色={}, 新角色={}", 
                currentUser.getUsername(), originalCharacter.getName(), savedCharacter.getName());
        
        return CharacterResponse.fromEntity(savedCharacter);
    }
}
