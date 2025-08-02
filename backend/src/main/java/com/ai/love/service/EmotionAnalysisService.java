package com.ai.love.service;

import com.ai.love.dto.emotion.EmotionAnalysisResponse;
import com.ai.love.dto.emotion.EmotionTrendResponse;
import com.ai.love.dto.emotion.EmotionStatsResponse;
import com.ai.love.entity.EmotionAnalysis;
import com.ai.love.entity.Message;
import com.ai.love.entity.Conversation;
import com.ai.love.entity.User;
import com.ai.love.exception.BusinessException;
import com.ai.love.repository.EmotionAnalysisRepository;
import com.ai.love.repository.MessageRepository;
import com.ai.love.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 情感分析服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmotionAnalysisService {

    private final EmotionAnalysisRepository emotionAnalysisRepository;
    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final AuthService authService;

    /**
     * 分析消息情感
     */
    @Transactional
    public EmotionAnalysisResponse analyzeMessage(Long messageId) {
        User currentUser = authService.getCurrentUserEntity();
        
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new BusinessException("消息不存在"));
        
        // 验证消息所有权
        if (!message.getConversation().getUser().getId().equals(currentUser.getId())) {
            throw new BusinessException("无权分析此消息");
        }
        
        // 检查是否已有分析结果
        Optional<EmotionAnalysis> existingAnalysis = emotionAnalysisRepository.findByMessageId(messageId);
        if (existingAnalysis.isPresent()) {
            return EmotionAnalysisResponse.fromEntity(existingAnalysis.get());
        }
        
        // 执行情感分析
        EmotionAnalysis analysis = performEmotionAnalysis(message);
        EmotionAnalysis savedAnalysis = emotionAnalysisRepository.save(analysis);
        
        // 更新消息的情感分数
        message.setEmotionScore(analysis.getValence());
        messageRepository.save(message);
        
        log.info("情感分析完成: 消息ID={}, 情感类型={}, 置信度={}", 
                messageId, analysis.getEmotionType(), analysis.getConfidence());
        
        return EmotionAnalysisResponse.fromEntity(savedAnalysis);
    }

    /**
     * 获取对话的情感分析
     */
    @Transactional(readOnly = true)
    public List<EmotionAnalysisResponse> getConversationEmotions(Long conversationId) {
        User currentUser = authService.getCurrentUserEntity();
        
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new BusinessException("对话不存在"));
        
        // 验证对话所有权
        if (!conversation.getUser().getId().equals(currentUser.getId())) {
            throw new BusinessException("无权访问此对话的情感分析");
        }
        
        List<EmotionAnalysis> analyses = emotionAnalysisRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
        
        return analyses.stream()
                .map(EmotionAnalysisResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 获取用户的情感趋势
     */
    @Transactional(readOnly = true)
    public List<EmotionTrendResponse> getEmotionTrend(int days) {
        User currentUser = authService.getCurrentUserEntity();
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        
        List<Object[]> trendData = emotionAnalysisRepository.getEmotionTrendByUserId(currentUser.getId(), startDate);
        
        return trendData.stream()
                .map(data -> EmotionTrendResponse.builder()
                        .date((java.sql.Date) data[0])
                        .averageValence((Double) data[1])
                        .averageIntensity((Double) data[2])
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 获取情感统计信息
     */
    @Transactional(readOnly = true)
    public EmotionStatsResponse getEmotionStats() {
        User currentUser = authService.getCurrentUserEntity();
        
        // 获取最常见的情感类型
        List<Object[]> emotionCounts = emotionAnalysisRepository.getMostCommonEmotionsByUserId(currentUser.getId());
        Map<String, Long> emotionDistribution = emotionCounts.stream()
                .collect(Collectors.toMap(
                        data -> ((EmotionAnalysis.EmotionType) data[0]).getDescription(),
                        data -> (Long) data[1]
                ));
        
        // 获取正面/负面情感比例
        List<EmotionAnalysis> positiveEmotions = emotionAnalysisRepository.findPositiveEmotions(currentUser.getId());
        List<EmotionAnalysis> negativeEmotions = emotionAnalysisRepository.findNegativeEmotions(currentUser.getId());
        
        long totalAnalyses = emotionAnalysisRepository.countByUserId(currentUser.getId());
        double positiveRatio = totalAnalyses > 0 ? (double) positiveEmotions.size() / totalAnalyses : 0;
        double negativeRatio = totalAnalyses > 0 ? (double) negativeEmotions.size() / totalAnalyses : 0;
        
        return EmotionStatsResponse.builder()
                .totalAnalyses(totalAnalyses)
                .emotionDistribution(emotionDistribution)
                .positiveRatio(positiveRatio)
                .negativeRatio(negativeRatio)
                .neutralRatio(1.0 - positiveRatio - negativeRatio)
                .build();
    }

    /**
     * 执行情感分析（简化版本）
     */
    private EmotionAnalysis performEmotionAnalysis(Message message) {
        String content = message.getContent().toLowerCase();
        
        // 简化的情感分析逻辑（实际项目中应该使用更复杂的NLP模型）
        EmotionAnalysis.EmotionType emotionType = detectEmotionType(content);
        double confidence = calculateConfidence(content, emotionType);
        double intensity = calculateIntensity(content);
        
        EmotionAnalysis analysis = new EmotionAnalysis();
        analysis.setMessage(message);
        analysis.setConversation(message.getConversation());
        analysis.setEmotionType(emotionType);
        analysis.setConfidence(confidence);
        analysis.setIntensity(intensity);
        analysis.setValence(emotionType.getDefaultValence());
        analysis.setArousal(emotionType.getDefaultArousal());
        analysis.setKeywords(extractKeywords(content));
        
        return analysis;
    }

    /**
     * 检测情感类型
     */
    private EmotionAnalysis.EmotionType detectEmotionType(String content) {
        // 简化的关键词匹配
        Map<EmotionAnalysis.EmotionType, String[]> emotionKeywords = Map.of(
                EmotionAnalysis.EmotionType.JOY, new String[]{"开心", "高兴", "快乐", "喜悦", "哈哈", "😊", "😄"},
                EmotionAnalysis.EmotionType.SADNESS, new String[]{"难过", "伤心", "悲伤", "哭", "😢", "😭"},
                EmotionAnalysis.EmotionType.ANGER, new String[]{"生气", "愤怒", "气愤", "讨厌", "😡", "😠"},
                EmotionAnalysis.EmotionType.FEAR, new String[]{"害怕", "恐惧", "担心", "紧张", "😨", "😰"},
                EmotionAnalysis.EmotionType.SURPRISE, new String[]{"惊讶", "意外", "震惊", "😲", "😮"},
                EmotionAnalysis.EmotionType.LOVE, new String[]{"爱", "喜欢", "爱你", "亲爱的", "❤️", "💕"},
                EmotionAnalysis.EmotionType.EXCITEMENT, new String[]{"兴奋", "激动", "太棒了", "amazing", "😍"},
                EmotionAnalysis.EmotionType.CALM, new String[]{"平静", "安静", "放松", "冷静", "😌"}
        );
        
        for (Map.Entry<EmotionAnalysis.EmotionType, String[]> entry : emotionKeywords.entrySet()) {
            for (String keyword : entry.getValue()) {
                if (content.contains(keyword)) {
                    return entry.getKey();
                }
            }
        }
        
        return EmotionAnalysis.EmotionType.NEUTRAL;
    }

    /**
     * 计算置信度
     */
    private double calculateConfidence(String content, EmotionAnalysis.EmotionType emotionType) {
        if (emotionType == EmotionAnalysis.EmotionType.NEUTRAL) {
            return 0.5;
        }
        
        // 基于内容长度和关键词密度计算置信度
        double baseConfidence = 0.7;
        double lengthFactor = Math.min(content.length() / 100.0, 1.0);
        
        return Math.min(baseConfidence + lengthFactor * 0.2, 0.95);
    }

    /**
     * 计算情感强度
     */
    private double calculateIntensity(String content) {
        // 基于感叹号、大写字母等计算强度
        long exclamationCount = content.chars().filter(ch -> ch == '!').count();
        long upperCaseCount = content.chars().filter(Character::isUpperCase).count();
        
        double intensity = 0.5;
        intensity += Math.min(exclamationCount * 0.1, 0.3);
        intensity += Math.min(upperCaseCount * 0.01, 0.2);
        
        return Math.min(intensity, 1.0);
    }

    /**
     * 提取关键词
     */
    private String extractKeywords(String content) {
        // 简化的关键词提取
        String[] words = content.split("\\s+");
        List<String> keywords = Arrays.stream(words)
                .filter(word -> word.length() > 2)
                .limit(5)
                .collect(Collectors.toList());
        
        return String.join(",", keywords);
    }
}
