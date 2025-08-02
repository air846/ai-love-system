package com.ai.love;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * AI恋爱系统主应用类
 * 
 * @author AI Love System
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
@EnableAsync
public class AiLoveSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiLoveSystemApplication.class, args);
    }
}
