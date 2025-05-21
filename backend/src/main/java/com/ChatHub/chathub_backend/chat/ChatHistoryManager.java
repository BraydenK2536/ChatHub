package com.ChatHub.chathub_backend.chat;

import com.ChatHub.chathub_backend.message.BaseMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ChatHistoryManager {
    private static final String HISTORY_FILE_PATH = "src/main/resources/chat_history.txt";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ChatHistoryManager() {
        // 注册 JavaTimeModule 以支持 LocalDateTime 的序列化和反序列化
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 确保存储聊天记录的文件存在
        try {
            Path filePath = Paths.get(HISTORY_FILE_PATH);
            if (!Files.exists(filePath.getParent())) {
                Files.createDirectories(filePath.getParent());
            }
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            // 替换为日志记录
            System.err.println("创建聊天记录文件时出错: " + e.getMessage());
        }
    }

    /**
     * 将消息保存到聊天记录文件中
     * @param message 要保存的消息
     */
    public void saveMessage(BaseMessage message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HISTORY_FILE_PATH, true))) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String timestamp = now.format(formatter);
            // 设置消息的时间
            message.setTime(timestamp);

            String jsonMessage = objectMapper.writeValueAsString(message);
            writer.write(jsonMessage);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从聊天记录文件中读取所有消息
     * @return 聊天记录消息列表
     */
    public List<BaseMessage> loadMessages() {
        List<BaseMessage> messages = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(HISTORY_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    BaseMessage message = objectMapper.readValue(line, BaseMessage.class);
                    messages.add(message);
                } catch (JsonProcessingException e) {
                    // 替换为日志记录
                    System.err.println("解析聊天记录消息时出错: " + e.getMessage());
                }
            }
            // 替换为日志记录

        } catch (IOException e) {
            e.printStackTrace();
        }
        return messages;
    }
}