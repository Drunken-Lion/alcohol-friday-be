package com.drunkenlion.alcoholfriday.global.config;

import com.drunkenlion.alcoholfriday.domain.member.util.ReviewStatusParamConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/v1/**")
                .allowedOrigins(
                        "http://localhost:3000",
                        "http://localhost:8080",
                        "https://api.alcoholfriday.shop",
                        "https://api.alcoholfriday.store"
                )
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    // @RequestParam 값을 enum 타입으로 매핑하기 위해 작성
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new ReviewStatusParamConverter());
    }
}
