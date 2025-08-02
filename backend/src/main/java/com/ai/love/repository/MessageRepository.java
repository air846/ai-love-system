package com.ai.love.repository;

import com.ai.love.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息数据访问层
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * 根据对话ID查找消息
     */
    Page<Message> findByConversationId(Long conversationId, Pageable pageable);

    /**
     * 根据对话ID查找消息（按时间排序）
     */
    List<Message> findByConversationIdOrderByCreatedAtAsc(Long conversationId);

    /**
     * 根据对话ID和发送者类型查找消息
     */
    Page<Message> findByConversationIdAndSenderType(Long conversationId, 
                                                   Message.SenderType senderType, 
                                                   Pageable pageable);

    /**
     * 查找对话的最新消息
     */
    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId ORDER BY m.createdAt DESC")
    Page<Message> findLatestMessagesByConversationId(@Param("conversationId") Long conversationId, Pageable pageable);

    /**
     * 查找对话的最后一条消息
     */
    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId ORDER BY m.createdAt DESC LIMIT 1")
    Message findLastMessageByConversationId(@Param("conversationId") Long conversationId);

    /**
     * 根据内容关键词搜索消息
     */
    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId AND m.content LIKE %:keyword%")
    Page<Message> searchMessagesByContent(@Param("conversationId") Long conversationId, 
                                         @Param("keyword") String keyword, 
                                         Pageable pageable);

    /**
     * 查找指定时间范围内的消息
     */
    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId AND m.createdAt BETWEEN :startDate AND :endDate")
    Page<Message> findByConversationIdAndCreatedAtBetween(@Param("conversationId") Long conversationId,
                                                         @Param("startDate") LocalDateTime startDate,
                                                         @Param("endDate") LocalDateTime endDate,
                                                         Pageable pageable);

    /**
     * 统计对话的消息数量
     */
    @Query("SELECT COUNT(m) FROM Message m WHERE m.conversation.id = :conversationId")
    long countByConversationId(@Param("conversationId") Long conversationId);

    /**
     * 统计对话中各类型发送者的消息数量
     */
    @Query("SELECT m.senderType, COUNT(m) FROM Message m WHERE m.conversation.id = :conversationId GROUP BY m.senderType")
    List<Object[]> countBySenderTypeAndConversationId(@Param("conversationId") Long conversationId);

    /**
     * 查找用户的所有消息
     */
    @Query("SELECT m FROM Message m WHERE m.conversation.user.id = :userId ORDER BY m.createdAt DESC")
    Page<Message> findByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * 查找用户发送的消息
     */
    @Query("SELECT m FROM Message m WHERE m.conversation.user.id = :userId AND m.senderType = 'USER' ORDER BY m.createdAt DESC")
    Page<Message> findUserMessagesByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * 查找AI回复的消息
     */
    @Query("SELECT m FROM Message m WHERE m.conversation.user.id = :userId AND m.senderType = 'AI' ORDER BY m.createdAt DESC")
    Page<Message> findAiMessagesByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * 统计用户的消息总数
     */
    @Query("SELECT COUNT(m) FROM Message m WHERE m.conversation.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    /**
     * 统计用户今日发送的消息数
     */
    @Query("SELECT COUNT(m) FROM Message m WHERE m.conversation.user.id = :userId AND DATE(m.createdAt) = CURRENT_DATE AND m.senderType = 'USER'")
    long countTodayUserMessagesByUserId(@Param("userId") Long userId);

    /**
     * 查找有情感分析的消息
     */
    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId AND m.emotionScore IS NOT NULL")
    Page<Message> findMessagesWithEmotion(@Param("conversationId") Long conversationId, Pageable pageable);

    /**
     * 查找高情感强度的消息
     */
    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId AND m.emotionScore > :threshold")
    List<Message> findHighEmotionMessages(@Param("conversationId") Long conversationId, 
                                         @Param("threshold") Double threshold);

    /**
     * 计算对话的平均响应时间
     */
    @Query("SELECT AVG(m.processingTimeMs) FROM Message m WHERE m.conversation.id = :conversationId AND m.senderType = 'AI' AND m.processingTimeMs IS NOT NULL")
    Double getAverageResponseTime(@Param("conversationId") Long conversationId);

    /**
     * 查找最长的消息
     */
    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId ORDER BY LENGTH(m.content) DESC")
    Page<Message> findLongestMessages(@Param("conversationId") Long conversationId, Pageable pageable);
}
