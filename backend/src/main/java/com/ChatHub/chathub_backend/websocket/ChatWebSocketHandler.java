package com.ChatHub.chathub_backend.websocket;


import com.ChatHub.chathub_backend.chat.ChatHistoryManager;
import com.ChatHub.chathub_backend.message.BaseMessage;
import com.ChatHub.chathub_backend.repository.UserAccountRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component// 将这个 Handler 注册为一个 Spring Bean
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final UserAccountRepository userAccountRepository;

    private String username;

    @Autowired
    public ChatWebSocketHandler(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        //链接建立后，把当前用户加入sessions
        sessions.add(session);

        ChatHistoryManager chatHistoryManager = new ChatHistoryManager();
        List<BaseMessage> messages = chatHistoryManager.loadMessages();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fiveDaysAgo = now.minusDays(5);

        for (BaseMessage message : messages) {
            LocalDateTime messageTime = LocalDateTime.parse(message.getTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            if (messageTime.isAfter(fiveDaysAgo)) {
                String jsonMessage = objectMapper.writeValueAsString(message);
                session.sendMessage(new TextMessage(jsonMessage));
            }
        }
    }

@Override
public void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
    //处理用户发来的json并使用objectMapper.readValue反序列化
    String jsonPayload = textMessage.getPayload();
    BaseMessage message = objectMapper.readValue(jsonPayload, BaseMessage.class);
    System.out.println("[" + System.currentTimeMillis() + "]收到" + session.getId() + "的发送: " + jsonPayload);
    ChatHistoryManager  chatHistoryManager = new ChatHistoryManager();
    chatHistoryManager.saveMessage(message);

    if(message.getType().equals(USER_MESSAGE)) {
        broadcastMessage(null, message);
    }
}
private BaseMessage getMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
    // 处理用户发来的 JSON 并使用 objectMapper.readValue 反序列化
    String jsonPayload = textMessage.getPayload();
    BaseMessage message = objectMapper.readValue(jsonPayload, BaseMessage.class);
    System.out.println("[" + System.currentTimeMillis() + "]收到" + session.getId() + "的发送: " + jsonPayload);
    ChatHistoryManager chatHistoryManager = new ChatHistoryManager();
    chatHistoryManager.saveMessage(message);
    return message;
}



    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws IOException {
        //如果出错，则把当前用户从在线列表中移除
        System.err.println(session.getId() + "发生错误，已终止会话。");
        if (session.isOpen()) {
            session.close(CloseStatus.SERVER_ERROR);
        }
        sessions.remove(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("WebSocket链接断开: " + session.getId() + "关闭状态:" + status.getCode());
        //broadcastMessage(session, new BaseMessage());
        sessions.remove(session);
    }

    public void broadcastMessage(WebSocketSession blackListSession, BaseMessage baseMessage) throws JsonProcessingException {
        String jsonMessage =  objectMapper.writeValueAsString(baseMessage);
        TextMessage textMessage = new TextMessage(jsonMessage);
        //遍历sessions发送消息
        for (WebSocketSession wss : sessions) {
            //向打开的非黑名单sessions广播消息
            if (wss.isOpen() && (blackListSession == null || !wss.getId().equals(blackListSession.getId()))) {
                try {
                    wss.sendMessage(textMessage);
                } catch (IOException e) {
                    //出现错误时，输出错误信息并试图关闭错误WebSocketSession
                    System.err.println("广播时出现错误,sessions ID: " + wss.getId() + ": " + e.getMessage());
                    try {
                        wss.close();
                    } catch (IOException ex) {

                    }
                }
            }
        }
    }



}
