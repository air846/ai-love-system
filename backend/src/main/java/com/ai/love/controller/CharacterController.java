package com.ai.love.controller;

import com.ai.love.common.ApiResponse;
import com.ai.love.dto.character.*;
import com.ai.love.service.AiCharacterService;
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
 * AI角色控制器
 */
@Slf4j
@RestController
@RequestMapping("/characters")
@RequiredArgsConstructor
@Tag(name = "AI角色管理", description = "AI角色创建、配置、管理等接口")
public class CharacterController {

    private final AiCharacterService aiCharacterService;

    /**
     * 创建AI角色
     */
    @PostMapping
    @Operation(summary = "创建AI角色", description = "创建新的AI角色")
    public ApiResponse<CharacterResponse> createCharacter(@Valid @RequestBody CreateCharacterRequest request) {
        CharacterResponse response = aiCharacterService.createCharacter(request);
        return ApiResponse.success("AI角色创建成功", response);
    }

    /**
     * 获取用户的AI角色列表
     */
    @GetMapping
    @Operation(summary = "获取角色列表", description = "获取当前用户的所有AI角色")
    public ApiResponse<List<CharacterResponse>> getUserCharacters() {
        List<CharacterResponse> characters = aiCharacterService.getUserCharacters();
        return ApiResponse.success(characters);
    }

    /**
     * 获取AI角色详情
     */
    @GetMapping("/{characterId}")
    @Operation(summary = "获取角色详情", description = "获取指定AI角色的详细信息")
    public ApiResponse<CharacterResponse> getCharacter(@PathVariable Long characterId) {
        CharacterResponse character = aiCharacterService.getCharacter(characterId);
        return ApiResponse.success(character);
    }

    /**
     * 更新AI角色
     */
    @PutMapping("/{characterId}")
    @Operation(summary = "更新AI角色", description = "更新指定AI角色的信息")
    public ApiResponse<CharacterResponse> updateCharacter(
            @PathVariable Long characterId,
            @Valid @RequestBody UpdateCharacterRequest request) {
        
        CharacterResponse response = aiCharacterService.updateCharacter(characterId, request);
        return ApiResponse.success("AI角色更新成功", response);
    }

    /**
     * 删除AI角色
     */
    @DeleteMapping("/{characterId}")
    @Operation(summary = "删除AI角色", description = "删除指定的AI角色")
    public ApiResponse<String> deleteCharacter(@PathVariable Long characterId) {
        aiCharacterService.deleteCharacter(characterId);
        return ApiResponse.success("AI角色删除成功");
    }

    /**
     * 测试AI角色
     */
    @PostMapping("/{characterId}/test")
    @Operation(summary = "测试AI角色", description = "测试AI角色的对话效果")
    public ApiResponse<String> testCharacter(
            @PathVariable Long characterId,
            @Valid @RequestBody TestCharacterRequest request) {
        
        String response = aiCharacterService.testCharacter(characterId, request.getMessage());
        return ApiResponse.success("测试完成", response);
    }

    /**
     * 复制AI角色
     */
    @PostMapping("/{characterId}/clone")
    @Operation(summary = "复制AI角色", description = "复制现有的AI角色")
    public ApiResponse<CharacterResponse> cloneCharacter(
            @PathVariable Long characterId,
            @Valid @RequestBody CloneCharacterRequest request) {
        
        CharacterResponse response = aiCharacterService.cloneCharacter(characterId, request.getNewName());
        return ApiResponse.success("AI角色复制成功", response);
    }

    /**
     * 搜索AI角色
     */
    @GetMapping("/search")
    @Operation(summary = "搜索AI角色", description = "根据关键词搜索AI角色")
    public ApiResponse<Page<CharacterResponse>> searchCharacters(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        
        Page<CharacterResponse> characters = aiCharacterService.searchCharacters(keyword, pageable);
        return ApiResponse.success(characters);
    }

    /**
     * 获取热门AI角色
     */
    @GetMapping("/popular")
    @Operation(summary = "获取热门角色", description = "获取使用次数最多的AI角色")
    public ApiResponse<Page<CharacterResponse>> getPopularCharacters(
            @PageableDefault(size = 20) Pageable pageable) {
        
        Page<CharacterResponse> characters = aiCharacterService.getPopularCharacters(pageable);
        return ApiResponse.success(characters);
    }
}
