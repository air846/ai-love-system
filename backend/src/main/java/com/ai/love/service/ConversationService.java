package com.ai.love.service;

import com.ai.love.dto.conversation.ConversationListResponse;
import com.ai.love.dto.conversation.ConversationDetailResponse;
import com.ai.love.dto.conversation.UpdateConversationRequest;
import com.ai.love.dto.conversation.ConversationSettingsRequest;
import com.ai.love.dto.conversation.ConversationSettingsResponse;
import com.ai.love.entity.Conversation;
import com.ai.love.entity.User;
import com.ai.love.exception.BusinessException;
import com.ai.love.exception.ResourceNotFoundException;
import com.ai.love.repository.ConversationRepository;
import com.ai.love.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 对话服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final AuthService authService;

    /**
     * 获取用户的对话列表
     */
    @Transactional(readOnly = true)
    public Page<ConversationListResponse> getUserConversations(Pageable pageable) {
        User currentUser = authService.getCurrentUserEntity();
        Page<Conversation> conversations = conversationRepository.findByUserIdAndStatus(
                currentUser.getId(), 
                Conversation.ConversationStatus.ACTIVE, 
                pageable
        );
        
        return conversations.map(ConversationListResponse::fromEntity);
    }

    /**
     * 获取对话详情
     */
    @Transactional(readOnly = true)
    public ConversationDetailResponse getConversationDetail(Long conversationId) {
        User currentUser = authService.getCurrentUserEntity();
        
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("对话不存在"));
        
        // 验证对话所有权
        if (!conversation.getUser().getId().equals(currentUser.getId())) {
            throw new BusinessException("无权访问此对话");
        }
        
        return ConversationDetailResponse.fromEntity(conversation);
    }

    /**
     * 更新对话信息
     */
    @Transactional
    public ConversationDetailResponse updateConversation(Long conversationId, UpdateConversationRequest request) {
        User currentUser = authService.getCurrentUserEntity();
        
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("对话不存在"));
        
        // 验证对话所有权
        if (!conversation.getUser().getId().equals(currentUser.getId())) {
            throw new BusinessException("无权修改此对话");
        }
        
        // 更新字段
        if (request.getTitle() != null) {
            conversation.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            conversation.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            conversation.setStatus(request.getStatus());
        }
        
        Conversation updatedConversation = conversationRepository.save(conversation);
        log.info("对话更新成功: 用户={}, 对话ID={}", currentUser.getUsername(), conversationId);
        
        return ConversationDetailResponse.fromEntity(updatedConversation);
    }

    /**
     * 搜索对话
     */
    @Transactional(readOnly = true)
    public Page<ConversationListResponse> searchConversations(String keyword, Pageable pageable) {
        User currentUser = authService.getCurrentUserEntity();
        Page<Conversation> conversations = conversationRepository.findByUserIdAndTitleContaining(
                currentUser.getId(), keyword, pageable);
        
        return conversations.map(ConversationListResponse::fromEntity);
    }

    /**
     * 获取最近的对话
     */
    @Transactional(readOnly = true)
    public List<ConversationListResponse> getRecentConversations(int limit) {
        User currentUser = authService.getCurrentUserEntity();
        List<Conversation> conversations = conversationRepository.findActiveConversationsByUserId(currentUser.getId());
        
        return conversations.stream()
                .limit(limit)
                .map(ConversationListResponse::fromEntity)
                .toList();
    }

    /**
     * 归档对话
     */
    @Transactional
    public void archiveConversation(Long conversationId) {
        User currentUser = authService.getCurrentUserEntity();
        
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("对话不存在"));
        
        // 验证对话所有权
        if (!conversation.getUser().getId().equals(currentUser.getId())) {
            throw new BusinessException("无权归档此对话");
        }
        
        conversation.setStatus(Conversation.ConversationStatus.ARCHIVED);
        conversationRepository.save(conversation);
        
        log.info("对话归档成功: 用户={}, 对话ID={}", currentUser.getUsername(), conversationId);
    }

    /**
     * 恢复对话
     */
    @Transactional
    public void restoreConversation(Long conversationId) {
        User currentUser = authService.getCurrentUserEntity();
        
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("对话不存在"));
        
        // 验证对话所有权
        if (!conversation.getUser().getId().equals(currentUser.getId())) {
            throw new BusinessException("无权恢复此对话");
        }
        
        conversation.setStatus(Conversation.ConversationStatus.ACTIVE);
        conversationRepository.save(conversation);
        
        log.info("对话恢复成功: 用户={}, 对话ID={}", currentUser.getUsername(), conversationId);
    }

    /**
     * 获取对话统计信息
     */
    @Transactional(readOnly = true)
    public ConversationStatsResponse getConversationStats() {
        User currentUser = authService.getCurrentUserEntity();
        
        long totalCount = conversationRepository.countByUserId(currentUser.getId());
        long activeCount = conversationRepository.countActiveByUserId(currentUser.getId());
        
        // 获取各状态的对话数量
        List<Object[]> statusCounts = conversationRepository.countByStatusAndUserId(currentUser.getId());
        
        return ConversationStatsResponse.builder()
                .totalConversations(totalCount)
                .activeConversations(activeCount)
                .statusCounts(statusCounts)
                .build();
    }

    /**
     * 批量删除对话
     */
    @Transactional
    public void batchDeleteConversations(List<Long> conversationIds) {
        User currentUser = authService.getCurrentUserEntity();
        
        for (Long conversationId : conversationIds) {
            Conversation conversation = conversationRepository.findById(conversationId)
                    .orElseThrow(() -> new ResourceNotFoundException("对话不存在: " + conversationId));
            
            // 验证对话所有权
            if (!conversation.getUser().getId().equals(currentUser.getId())) {
                throw new BusinessException("无权删除对话: " + conversationId);
            }
            
            conversation.setStatus(Conversation.ConversationStatus.DELETED);
            conversationRepository.save(conversation);
        }
        
        log.info("批量删除对话成功: 用户={}, 数量={}", currentUser.getUsername(), conversationIds.size());
    }

    /**
     * 获取对话设置
     */
    @Transactional(readOnly = true)
    public ConversationSettingsResponse getConversationSettings(Long conversationId) {
        User currentUser = authService.getCurrentUserEntity();

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("对话不存在"));

        // 验证对话所有权
        if (!conversation.getUser().getId().equals(currentUser.getId())) {
            throw new BusinessException("无权访问此对话设置");
        }

        return ConversationSettingsResponse.fromEntity(conversation);
    }

    /**
     * 更新对话设置
     */
    @Transactional
    public ConversationSettingsResponse updateConversationSettings(Long conversationId, ConversationSettingsRequest request) {
        User currentUser = authService.getCurrentUserEntity();

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("对话不存在"));

        // 验证对话所有权
        if (!conversation.getUser().getId().equals(currentUser.getId())) {
            throw new BusinessException("无权修改此对话设置");
        }

        // 更新基本信息
        if (request.getTitle() != null) {
            conversation.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            conversation.setDescription(request.getDescription());
        }

        // 更新AI参数
        if (request.getAiTemperature() != null) {
            conversation.setAiTemperature(request.getAiTemperature());
        }
        if (request.getAiMaxTokens() != null) {
            conversation.setAiMaxTokens(request.getAiMaxTokens());
        }
        if (request.getAiModel() != null) {
            conversation.setAiModel(request.getAiModel());
        }

        // 更新偏好设置
        if (request.getAutoSaveEnabled() != null) {
            conversation.setAutoSaveEnabled(request.getAutoSaveEnabled());
        }
        if (request.getNotificationEnabled() != null) {
            conversation.setNotificationEnabled(request.getNotificationEnabled());
        }
        if (request.getContextLength() != null) {
            conversation.setContextLength(request.getContextLength());
        }
        if (request.getResponseStyle() != null) {
            conversation.setResponseStyle(request.getResponseStyle());
        }
        if (request.getLanguagePreference() != null) {
            conversation.setLanguagePreference(request.getLanguagePreference());
        }

        Conversation updatedConversation = conversationRepository.save(conversation);
        log.info("对话设置更新成功: 用户={}, 对话ID={}", currentUser.getUsername(), conversationId);

        return ConversationSettingsResponse.fromEntity(updatedConversation);
    }

    /**
     * 对话统计响应DTO
     */
    @lombok.Data
    @lombok.Builder
    public static class ConversationStatsResponse {
        private Long totalConversations;
        private Long activeConversations;
        private List<Object[]> statusCounts;
    }
}
