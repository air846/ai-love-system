package com.ai.love.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final FileUploadConfig fileUploadConfig;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置文件上传访问路径
        registry.addResourceHandler(fileUploadConfig.getAccessUrlPrefix() + "/**")
                .addResourceLocations("file:" + fileUploadConfig.getUploadDir() + "/");
    }
}
