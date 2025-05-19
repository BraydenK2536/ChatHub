package com.ChatHub.chathub_backend.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration // 声明这是一个 Spring 配置类
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final com.ChatHub.chathub_backend.websocket.ChatWebSocketHandler chatWebSocketHandler;

    // 通过构造函数注入我们创建的 ChatWebSocketHandler
    @Autowired
    public WebSocketConfig(ChatWebSocketHandler chatWebSocketHandler) {
        this.chatWebSocketHandler = chatWebSocketHandler;
    }

    /**
     * 注册 WebSocket 处理器。
     * @param registry WebSocket 处理器注册表。
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 将 "/chat" 路径映射到我们的 ChatWebSocketHandler
        registry.addHandler(chatWebSocketHandler, "/chat")
                .setAllowedOrigins("*"); // 允许所有来源的连接
    }
}
