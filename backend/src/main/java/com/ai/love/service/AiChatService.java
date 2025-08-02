package com.ai.love.service;

import com.ai.love.dto.ai.ChatMessage;
import com.ai.love.entity.AiCharacter;
import com.ai.love.entity.Conversation;
import com.ai.love.entity.Message;
import com.ai.love.entity.User;
import com.ai.love.exception.BusinessException;
import com.ai.love.repository.AiCharacterRepository;
import com.ai.love.repository.ConversationRepository;
import com.ai.love.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * AI对话服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiChatService {

    private final ZhipuAiClient zhipuAiClient;
    private final AuthService authService;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final AiCharacterRepository aiCharacterRepository;

    /**
     * 发送消息并获取AI回复
     */
    @Transactional
    public List<Message> sendMessage(Long conversationId, String userMessage) {
        // 获取当前用户
        User currentUser = authService.getCurrentUserEntity();
        
        // 获取对话
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new BusinessException("对话不存在"));
        
        // 验证对话所有权
        if (!conversation.getUser().getId().equals(currentUser.getId())) {
            throw new BusinessException("无权访问此对话");
        }
        
        // 获取AI角色
        AiCharacter character = conversation.getCharacter();
        if (character == null) {
            throw new BusinessException("AI角色不存在");
        }

        try {
            long startTime = System.currentTimeMillis();
            
            // 保存用户消息
            Message userMsg = saveUserMessage(conversation, userMessage);
            
            // 构建对话历史
            List<ChatMessage> chatHistory = buildChatHistory(conversation);
            
            // 获取AI回复
            String aiResponse = zhipuAiClient.chatWithCharacter(
                    userMessage, 
                    character.generateSystemPrompt()
            );
            
            long processingTime = System.currentTimeMillis() - startTime;
            
            // 保存AI回复
            Message aiMsg = saveAiMessage(conversation, aiResponse, processingTime);
            
            // 更新对话统计
            conversation.updateMessageStats();
            conversationRepository.save(conversation);
            
            // 更新角色使用次数
            character.incrementUsage();
            aiCharacterRepository.save(character);
            
            log.info("AI对话完成: 用户={}, 角色={}, 处理时间={}ms", 
                    currentUser.getUsername(), character.getName(), processingTime);
            
            // 返回用户消息和AI回复消息
            List<Message> messages = new ArrayList<>();
            messages.add(userMsg);
            messages.add(aiMsg);
            return messages;
            
        } catch (Exception e) {
            log.error("AI对话失败: ", e);
            throw new BusinessException("AI对话失败: " + e.getMessage());
        }
    }

    /**
     * 创建新对话
     */
    @Transactional
    public Conversation createConversation(Long characterId, String title) {
        User currentUser = authService.getCurrentUserEntity();
        
        AiCharacter character = aiCharacterRepository.findById(characterId)
                .orElseThrow(() -> new BusinessException("AI角色不存在"));
        
        // 验证角色所有权
        if (!character.getUser().getId().equals(currentUser.getId())) {
            throw new BusinessException("无权使用此AI角色");
        }
        
        Conversation conversation = new Conversation();
        conversation.setUser(currentUser);
        conversation.setCharacter(character);
        conversation.setTitle(title != null ? title : "与" + character.getName() + "的对话");
        conversation.setStatus(Conversation.ConversationStatus.ACTIVE);
        
        return conversationRepository.save(conversation);
    }

    /**
     * 构建对话历史
     */
    private List<ChatMessage> buildChatHistory(Conversation conversation) {
        List<Message> messages = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversation.getId());
        List<ChatMessage> chatHistory = new ArrayList<>();
        
        // 限制历史消息数量，避免超出令牌限制
        int maxHistoryMessages = 20;
        int startIndex = Math.max(0, messages.size() - maxHistoryMessages);
        
        for (int i = startIndex; i < messages.size(); i++) {
            Message msg = messages.get(i);
            String role = msg.getSenderType() == Message.SenderType.USER ? "user" : "assistant";
            chatHistory.add(ChatMessage.builder()
                    .role(role)
                    .content(msg.getContent())
                    .build());
        }
        
        return chatHistory;
    }

    /**
     * 保存用户消息
     */
    private Message saveUserMessage(Conversation conversation, String content) {
        Message message = new Message();
        message.setConversation(conversation);
        message.setContent(content);
        message.setSenderType(Message.SenderType.USER);
        message.setMessageType(Message.MessageType.TEXT);
        
        return messageRepository.save(message);
    }

    /**
     * 保存AI消息
     */
    private Message saveAiMessage(Conversation conversation, String content, Long processingTime) {
        Message message = new Message();
        message.setConversation(conversation);
        message.setContent(content);
        message.setSenderType(Message.SenderType.AI);
        message.setMessageType(Message.MessageType.TEXT);
        message.setProcessingTimeMs(processingTime);
        
        return messageRepository.save(message);
    }

    /**
     * 获取对话消息列表
     */
    @Transactional(readOnly = true)
    public List<Message> getConversationMessages(Long conversationId) {
        User currentUser = authService.getCurrentUserEntity();
        
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new BusinessException("对话不存在"));
        
        if (!conversation.getUser().getId().equals(currentUser.getId())) {
            throw new BusinessException("无权访问此对话");
        }
        
        return messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
    }

    /**
     * 删除对话
     */
    @Transactional
    public void deleteConversation(Long conversationId) {
        User currentUser = authService.getCurrentUserEntity();
        
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new BusinessException("对话不存在"));
        
        if (!conversation.getUser().getId().equals(currentUser.getId())) {
            throw new BusinessException("无权删除此对话");
        }
        
        conversation.setStatus(Conversation.ConversationStatus.DELETED);
        conversationRepository.save(conversation);
    }

    /**
     * 检查AI服务状态
     */
    public boolean checkAiServiceStatus() {
        return zhipuAiClient.checkConnection();
    }
}
