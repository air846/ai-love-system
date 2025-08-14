package com.ai.love.repository;

import com.ai.love.entity.EmotionAnalysis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 情感分析数据访问层
 */
@Repository
public interface EmotionAnalysisRepository extends JpaRepository<EmotionAnalysis, Long> {

    /**
     * 根据消息ID查找情感分析
     */
    Optional<EmotionAnalysis> findByMessageId(Long messageId);

    /**
     * 根据对话ID查找情感分析
     */
    Page<EmotionAnalysis> findByConversationId(Long conversationId, Pageable pageable);

    /**
     * 根据对话ID和情感类型查找情感分析
     */
    Page<EmotionAnalysis> findByConversationIdAndEmotionType(Long conversationId, 
                                                            EmotionAnalysis.EmotionType emotionType, 
                                                            Pageable pageable);

    /**
     * 查找对话的情感分析（按时间排序）
     */
    List<EmotionAnalysis> findByConversationIdOrderByCreatedAtAsc(Long conversationId);

    /**
     * 查找高置信度的情感分析
     */
    @Query("SELECT e FROM EmotionAnalysis e WHERE e.conversation.id = :conversationId AND e.confidence > :threshold")
    List<EmotionAnalysis> findHighConfidenceEmotions(@Param("conversationId") Long conversationId, 
                                                     @Param("threshold") Double threshold);

    /**
     * 查找正面情感分析
     */
    @Query("SELECT e FROM EmotionAnalysis e WHERE e.conversation.user.id = :userId AND e.valence > 0.1")
    List<EmotionAnalysis> findPositiveEmotions(@Param("userId") Long userId);

    /**
     * 查找负面情感分析
     */
    @Query("SELECT e FROM EmotionAnalysis e WHERE e.conversation.user.id = :userId AND e.valence < -0.1")
    List<EmotionAnalysis> findNegativeEmotions(@Param("userId") Long userId);

    /**
     * 统计正面情感数量
     */
    @Query("SELECT COUNT(e) FROM EmotionAnalysis e WHERE e.conversation.user.id = :userId AND e.valence > 0.1")
    long countPositiveEmotions(@Param("userId") Long userId);

    /**
     * 统计负面情感数量
     */
    @Query("SELECT COUNT(e) FROM EmotionAnalysis e WHERE e.conversation.user.id = :userId AND e.valence < -0.1")
    long countNegativeEmotions(@Param("userId") Long userId);

    /**
     * 查找用户的所有关键词
     */
    @Query("SELECT e.keywords FROM EmotionAnalysis e WHERE e.conversation.user.id = :userId AND e.keywords IS NOT NULL AND e.keywords != ''")
    List<String> findAllKeywordsByUserId(@Param("userId") Long userId);

    /**
     * 查找高强度情感分析
     */
    @Query("SELECT e FROM EmotionAnalysis e WHERE e.conversation.id = :conversationId AND e.intensity > :threshold")
    List<EmotionAnalysis> findHighIntensityEmotions(@Param("conversationId") Long conversationId, 
                                                    @Param("threshold") Double threshold);

    /**
     * 统计对话中各情感类型的数量
     */
    @Query("SELECT e.emotionType, COUNT(e) FROM EmotionAnalysis e WHERE e.conversation.id = :conversationId GROUP BY e.emotionType")
    List<Object[]> countByEmotionTypeAndConversationId(@Param("conversationId") Long conversationId);

    /**
     * 计算对话的平均情感效价
     */
    @Query("SELECT AVG(e.valence) FROM EmotionAnalysis e WHERE e.conversation.id = :conversationId")
    Double getAverageValenceByConversationId(@Param("conversationId") Long conversationId);

    /**
     * 计算对话的平均情感强度
     */
    @Query("SELECT AVG(e.intensity) FROM EmotionAnalysis e WHERE e.conversation.id = :conversationId")
    Double getAverageIntensityByConversationId(@Param("conversationId") Long conversationId);

    /**
     * 查找用户的情感分析
     */
    @Query("SELECT e FROM EmotionAnalysis e WHERE e.conversation.user.id = :userId ORDER BY e.createdAt DESC")
    Page<EmotionAnalysis> findByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * 查找指定时间范围内的情感分析
     */
    @Query("SELECT e FROM EmotionAnalysis e WHERE e.conversation.id = :conversationId AND e.createdAt BETWEEN :startDate AND :endDate")
    Page<EmotionAnalysis> findByConversationIdAndCreatedAtBetween(@Param("conversationId") Long conversationId,
                                                                 @Param("startDate") LocalDateTime startDate,
                                                                 @Param("endDate") LocalDateTime endDate,
                                                                 Pageable pageable);

    /**
     * 查找用户的情感趋势
     */
    @Query("SELECT DATE(e.createdAt), AVG(e.valence), AVG(e.intensity) FROM EmotionAnalysis e " +
           "WHERE e.conversation.user.id = :userId AND e.createdAt >= :startDate " +
           "GROUP BY DATE(e.createdAt) ORDER BY DATE(e.createdAt)")
    List<Object[]> getEmotionTrendByUserId(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate);

    /**
     * 查找最常见的情感类型
     */
    @Query("SELECT e.emotionType, COUNT(e) FROM EmotionAnalysis e WHERE e.conversation.user.id = :userId " +
           "GROUP BY e.emotionType ORDER BY COUNT(e) DESC")
    List<Object[]> getMostCommonEmotionsByUserId(@Param("userId") Long userId);

    /**
     * 查找情感变化最大的对话
     */
    @Query("SELECT e.conversation.id, MAX(e.valence) - MIN(e.valence) as variance FROM EmotionAnalysis e " +
           "WHERE e.conversation.user.id = :userId GROUP BY e.conversation.id ORDER BY variance DESC")
    List<Object[]> getConversationsWithHighestEmotionVariance(@Param("userId") Long userId);

    /**
     * 统计用户的情感分析总数
     */
    @Query("SELECT COUNT(e) FROM EmotionAnalysis e WHERE e.conversation.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    /**
     * 查找最近的情感分析
     */
    @Query("SELECT e FROM EmotionAnalysis e WHERE e.conversation.user.id = :userId ORDER BY e.createdAt DESC")
    Page<EmotionAnalysis> findRecentEmotionsByUserId(@Param("userId") Long userId, Pageable pageable);
}
