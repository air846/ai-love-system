package com.ai.love.controller;

import com.ai.love.common.ApiResponse;
import com.ai.love.dto.conversation.ConversationDetailResponse;
import com.ai.love.dto.conversation.ConversationListResponse;
import com.ai.love.dto.conversation.UpdateConversationRequest;
import com.ai.love.dto.conversation.ConversationSettingsRequest;
import com.ai.love.dto.conversation.ConversationSettingsResponse;
import com.ai.love.service.ConversationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 对话管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/conversations")
@RequiredArgsConstructor
@Tag(name = "对话管理", description = "对话列表、详情、搜索等管理接口")
public class ConversationController {

    private final ConversationService conversationService;

    /**
     * 获取用户的对话列表
     */
    @GetMapping
    @Operation(summary = "获取对话列表", description = "分页获取当前用户的对话列表")
    public ApiResponse<Page<ConversationListResponse>> getUserConversations(
            @PageableDefault(size = 20, sort = "lastMessageAt") Pageable pageable) {
        
        Page<ConversationListResponse> conversations = conversationService.getUserConversations(pageable);
        return ApiResponse.success(conversations);
    }

    /**
     * 获取对话详情
     */
    @GetMapping("/{conversationId}")
    @Operation(summary = "获取对话详情", description = "获取指定对话的详细信息")
    public ApiResponse<ConversationDetailResponse> getConversationDetail(@PathVariable Long conversationId) {
        ConversationDetailResponse conversation = conversationService.getConversationDetail(conversationId);
        return ApiResponse.success(conversation);
    }

    /**
     * 更新对话信息
     */
    @PutMapping("/{conversationId}")
    @Operation(summary = "更新对话信息", description = "更新对话的标题、描述等信息")
    public ApiResponse<ConversationDetailResponse> updateConversation(
            @PathVariable Long conversationId,
            @Valid @RequestBody UpdateConversationRequest request) {
        
        ConversationDetailResponse conversation = conversationService.updateConversation(conversationId, request);
        return ApiResponse.success("对话更新成功", conversation);
    }

    /**
     * 搜索对话
     */
    @GetMapping("/search")
    @Operation(summary = "搜索对话", description = "根据关键词搜索对话")
    public ApiResponse<Page<ConversationListResponse>> searchConversations(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        
        Page<ConversationListResponse> conversations = conversationService.searchConversations(keyword, pageable);
        return ApiResponse.success(conversations);
    }

    /**
     * 获取最近的对话
     */
    @GetMapping("/recent")
    @Operation(summary = "获取最近对话", description = "获取最近活跃的对话列表")
    public ApiResponse<List<ConversationListResponse>> getRecentConversations(
            @RequestParam(defaultValue = "10") int limit) {
        
        List<ConversationListResponse> conversations = conversationService.getRecentConversations(limit);
        return ApiResponse.success(conversations);
    }

    /**
     * 归档对话
     */
    @PostMapping("/{conversationId}/archive")
    @Operation(summary = "归档对话", description = "将对话设置为归档状态")
    public ApiResponse<String> archiveConversation(@PathVariable Long conversationId) {
        conversationService.archiveConversation(conversationId);
        return ApiResponse.success("对话归档成功");
    }

    /**
     * 恢复对话
     */
    @PostMapping("/{conversationId}/restore")
    @Operation(summary = "恢复对话", description = "将归档的对话恢复为活跃状态")
    public ApiResponse<String> restoreConversation(@PathVariable Long conversationId) {
        conversationService.restoreConversation(conversationId);
        return ApiResponse.success("对话恢复成功");
    }

    /**
     * 获取对话统计信息
     */
    @GetMapping("/stats")
    @Operation(summary = "获取对话统计", description = "获取用户的对话统计信息")
    public ApiResponse<ConversationService.ConversationStatsResponse> getConversationStats() {
        ConversationService.ConversationStatsResponse stats = conversationService.getConversationStats();
        return ApiResponse.success(stats);
    }

    /**
     * 获取对话设置
     */
    @GetMapping("/{conversationId}/settings")
    @Operation(summary = "获取对话设置", description = "获取指定对话的设置信息")
    public ApiResponse<ConversationSettingsResponse> getConversationSettings(@PathVariable Long conversationId) {
        ConversationSettingsResponse settings = conversationService.getConversationSettings(conversationId);
        return ApiResponse.success(settings);
    }

    /**
     * 更新对话设置
     */
    @PutMapping("/{conversationId}/settings")
    @Operation(summary = "更新对话设置", description = "更新指定对话的设置信息")
    public ApiResponse<ConversationSettingsResponse> updateConversationSettings(
            @PathVariable Long conversationId,
            @Valid @RequestBody ConversationSettingsRequest request) {
        ConversationSettingsResponse settings = conversationService.updateConversationSettings(conversationId, request);
        return ApiResponse.success("对话设置更新成功", settings);
    }

    /**
     * 批量删除对话
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除对话", description = "批量删除多个对话")
    public ApiResponse<String> batchDeleteConversations(@RequestBody List<Long> conversationIds) {
        conversationService.batchDeleteConversations(conversationIds);
        return ApiResponse.success("批量删除成功");
    }
}
