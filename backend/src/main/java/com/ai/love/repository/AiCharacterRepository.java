package com.ai.love.repository;

import com.ai.love.entity.AiCharacter;
import com.ai.love.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * AI角色数据访问层
 */
@Repository
public interface AiCharacterRepository extends JpaRepository<AiCharacter, Long> {

    /**
     * 根据用户查找角色
     */
    List<AiCharacter> findByUser(User user);

    /**
     * 根据用户ID查找角色
     */
    List<AiCharacter> findByUserId(Long userId);

    /**
     * 根据用户和状态查找角色
     */
    List<AiCharacter> findByUserAndStatus(User user, AiCharacter.CharacterStatus status);

    /**
     * 根据用户ID和状态查找角色
     */
    Page<AiCharacter> findByUserIdAndStatus(Long userId, AiCharacter.CharacterStatus status, Pageable pageable);

    /**
     * 根据性格查找角色
     */
    Page<AiCharacter> findByPersonality(AiCharacter.Personality personality, Pageable pageable);

    /**
     * 根据性别查找角色
     */
    Page<AiCharacter> findByGender(AiCharacter.Gender gender, Pageable pageable);

    /**
     * 根据名称模糊查找角色
     */
    @Query("SELECT c FROM AiCharacter c WHERE c.name LIKE %:name% AND c.status = 'ACTIVE'")
    Page<AiCharacter> findByNameContaining(@Param("name") String name, Pageable pageable);

    /**
     * 查找用户的活跃角色
     */
    @Query("SELECT c FROM AiCharacter c WHERE c.user.id = :userId AND c.status = 'ACTIVE' ORDER BY c.usageCount DESC")
    List<AiCharacter> findActiveCharactersByUserId(@Param("userId") Long userId);

    /**
     * 查找最受欢迎的角色
     */
    @Query("SELECT c FROM AiCharacter c WHERE c.status = 'ACTIVE' ORDER BY c.usageCount DESC")
    Page<AiCharacter> findMostPopularCharacters(Pageable pageable);

    /**
     * 根据用户ID和角色名称查找角色
     */
    Optional<AiCharacter> findByUserIdAndName(Long userId, String name);

    /**
     * 检查用户是否已有同名角色
     */
    boolean existsByUserIdAndName(Long userId, String name);

    /**
     * 更新角色使用次数
     */
    @Modifying
    @Query("UPDATE AiCharacter c SET c.usageCount = c.usageCount + 1 WHERE c.id = :characterId")
    int incrementUsageCount(@Param("characterId") Long characterId);

    /**
     * 统计用户的角色数量
     */
    @Query("SELECT COUNT(c) FROM AiCharacter c WHERE c.user.id = :userId AND c.status = 'ACTIVE'")
    long countByUserId(@Param("userId") Long userId);

    /**
     * 统计各性格类型的角色数量
     */
    @Query("SELECT c.personality, COUNT(c) FROM AiCharacter c WHERE c.status = 'ACTIVE' GROUP BY c.personality")
    List<Object[]> countByPersonality();

    /**
     * 查找最近创建的角色
     */
    @Query("SELECT c FROM AiCharacter c WHERE c.status = 'ACTIVE' ORDER BY c.createdAt DESC")
    Page<AiCharacter> findRecentCharacters(Pageable pageable);

    /**
     * 根据关键词搜索角色
     */
    @Query("SELECT c FROM AiCharacter c WHERE c.status = 'ACTIVE' AND " +
           "(c.name LIKE %:keyword% OR c.description LIKE %:keyword% OR c.backgroundStory LIKE %:keyword%)")
    Page<AiCharacter> searchCharacters(@Param("keyword") String keyword, Pageable pageable);
}
