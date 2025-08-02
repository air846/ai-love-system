package com.ai.love.controller;

import com.ai.love.common.ApiResponse;
import com.ai.love.dto.emotion.EmotionAnalysisResponse;
import com.ai.love.dto.emotion.EmotionStatsResponse;
import com.ai.love.dto.emotion.EmotionTrendResponse;
import com.ai.love.service.EmotionAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 情感分析控制器
 */
@Slf4j
@RestController
@RequestMapping("/emotions")
@RequiredArgsConstructor
@Tag(name = "情感分析", description = "情感分析、趋势统计等接口")
public class EmotionController {

    private final EmotionAnalysisService emotionAnalysisService;

    /**
     * 分析消息情感
     */
    @PostMapping("/analyze/{messageId}")
    @Operation(summary = "分析消息情感", description = "对指定消息进行情感分析")
    public ApiResponse<EmotionAnalysisResponse> analyzeMessage(@PathVariable Long messageId) {
        EmotionAnalysisResponse analysis = emotionAnalysisService.analyzeMessage(messageId);
        return ApiResponse.success("情感分析完成", analysis);
    }

    /**
     * 获取对话的情感分析
     */
    @GetMapping("/conversations/{conversationId}")
    @Operation(summary = "获取对话情感分析", description = "获取指定对话的所有情感分析结果")
    public ApiResponse<List<EmotionAnalysisResponse>> getConversationEmotions(@PathVariable Long conversationId) {
        List<EmotionAnalysisResponse> emotions = emotionAnalysisService.getConversationEmotions(conversationId);
        return ApiResponse.success(emotions);
    }

    /**
     * 获取情感趋势
     */
    @GetMapping("/trends")
    @Operation(summary = "获取情感趋势", description = "获取用户的情感变化趋势")
    public ApiResponse<List<EmotionTrendResponse>> getEmotionTrend(
            @RequestParam(defaultValue = "30") int days) {
        
        List<EmotionTrendResponse> trends = emotionAnalysisService.getEmotionTrend(days);
        return ApiResponse.success(trends);
    }

    /**
     * 获取情感统计
     */
    @GetMapping("/stats")
    @Operation(summary = "获取情感统计", description = "获取用户的情感分析统计信息")
    public ApiResponse<EmotionStatsResponse> getEmotionStats() {
        EmotionStatsResponse stats = emotionAnalysisService.getEmotionStats();
        return ApiResponse.success(stats);
    }
}
