package com.ChatHub.chathub_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // 配置允许的源 (前端的地址)
        config.addAllowedOrigin("http://47.109.103.88:7833");
        config.addAllowedOrigin("http://192.168.137.1:7833");

        // 3. 是否允许发送 Cookie 等凭证信息
        config.setAllowCredentials(true);

        // 4. 允许的请求头 (允许所有自定义请求头)
        config.addAllowedHeader("*");

        // 5. 允许的请求方法
        config.addAllowedMethod("*"); // 允许所有方法

        // 6. 创建 UrlBasedCorsConfigurationSource 对象
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // 7. 为所有接口路径应用这个CORS配置
        source.registerCorsConfiguration("/**", config);

        // 8. 返回 CorsFilter 实例
        return new CorsFilter(source);
    }
}