package com.ai.love.repository;

import com.ai.love.entity.Conversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 对话数据访问层
 */
@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    /**
     * 根据用户ID查找对话
     */
    Page<Conversation> findByUserId(Long userId, Pageable pageable);

    /**
     * 根据用户ID和状态查找对话
     */
    Page<Conversation> findByUserIdAndStatus(Long userId, Conversation.ConversationStatus status, Pageable pageable);

    /**
     * 根据角色ID查找对话
     */
    Page<Conversation> findByCharacterId(Long characterId, Pageable pageable);

    /**
     * 根据用户ID和角色ID查找对话
     */
    Page<Conversation> findByUserIdAndCharacterId(Long userId, Long characterId, Pageable pageable);

    /**
     * 查找用户的活跃对话
     */
    @Query("SELECT c FROM Conversation c WHERE c.user.id = :userId AND c.status = 'ACTIVE' ORDER BY c.lastMessageAt DESC")
    List<Conversation> findActiveConversationsByUserId(@Param("userId") Long userId);

    /**
     * 查找最近的对话
     */
    @Query("SELECT c FROM Conversation c WHERE c.user.id = :userId ORDER BY c.lastMessageAt DESC")
    Page<Conversation> findRecentConversationsByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * 根据标题模糊查找对话
     */
    @Query("SELECT c FROM Conversation c WHERE c.user.id = :userId AND c.title LIKE %:title%")
    Page<Conversation> findByUserIdAndTitleContaining(@Param("userId") Long userId, 
                                                     @Param("title") String title, 
                                                     Pageable pageable);

    /**
     * 查找指定时间范围内的对话
     */
    @Query("SELECT c FROM Conversation c WHERE c.user.id = :userId AND c.createdAt BETWEEN :startDate AND :endDate")
    Page<Conversation> findByUserIdAndCreatedAtBetween(@Param("userId") Long userId,
                                                      @Param("startDate") LocalDateTime startDate,
                                                      @Param("endDate") LocalDateTime endDate,
                                                      Pageable pageable);

    /**
     * 更新对话消息统计
     */
    @Modifying
    @Query("UPDATE Conversation c SET c.messageCount = c.messageCount + 1, c.lastMessageAt = :messageTime WHERE c.id = :conversationId")
    int updateMessageStats(@Param("conversationId") Long conversationId, @Param("messageTime") LocalDateTime messageTime);

    /**
     * 统计用户的对话数量
     */
    @Query("SELECT COUNT(c) FROM Conversation c WHERE c.user.id = :userId AND c.status != 'DELETED'")
    long countByUserId(@Param("userId") Long userId);

    /**
     * 统计用户的活跃对话数量
     */
    @Query("SELECT COUNT(c) FROM Conversation c WHERE c.user.id = :userId AND c.status = 'ACTIVE'")
    long countActiveByUserId(@Param("userId") Long userId);

    /**
     * 查找长时间未活跃的对话
     */
    @Query("SELECT c FROM Conversation c WHERE c.lastMessageAt < :cutoffTime AND c.status = 'ACTIVE'")
    List<Conversation> findInactiveConversations(@Param("cutoffTime") LocalDateTime cutoffTime);

    /**
     * 统计各状态的对话数量
     */
    @Query("SELECT c.status, COUNT(c) FROM Conversation c WHERE c.user.id = :userId GROUP BY c.status")
    List<Object[]> countByStatusAndUserId(@Param("userId") Long userId);

    /**
     * 查找用户与特定角色的最新对话
     */
    @Query("SELECT c FROM Conversation c WHERE c.user.id = :userId AND c.character.id = :characterId ORDER BY c.lastMessageAt DESC")
    Optional<Conversation> findLatestConversationByUserAndCharacter(@Param("userId") Long userId, 
                                                                   @Param("characterId") Long characterId);

    /**
     * 查找消息数量最多的对话
     */
    @Query("SELECT c FROM Conversation c WHERE c.user.id = :userId ORDER BY c.messageCount DESC")
    Page<Conversation> findMostActiveConversationsByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * 删除用户的所有对话（软删除）
     */
    @Modifying
    @Query("UPDATE Conversation c SET c.status = 'DELETED' WHERE c.user.id = :userId")
    int softDeleteByUserId(@Param("userId") Long userId);
}
