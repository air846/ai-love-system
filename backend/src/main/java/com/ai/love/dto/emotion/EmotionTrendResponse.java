package com.ai.love.dto.emotion;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.sql.Date;

/**
 * 情感趋势响应DTO
 */
@Data
@Builder
public class EmotionTrendResponse {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date date;
    
    private Double averageValence;
    private Double averageIntensity;
    
    /**
     * 获取情感倾向描述
     */
    public String getValenceDescription() {
        if (averageValence == null) return "未知";
        if (averageValence > 0.3) return "积极";
        if (averageValence < -0.3) return "消极";
        return "中性";
    }
    
    /**
     * 获取强度描述
     */
    public String getIntensityDescription() {
        if (averageIntensity == null) return "未知";
        if (averageIntensity > 0.7) return "强烈";
        if (averageIntensity > 0.4) return "中等";
        return "平和";
    }
}
