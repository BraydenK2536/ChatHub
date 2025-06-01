package com.ChatHub.chathub_backend.service;

import com.ChatHub.chathub_backend.entity.ChatHistoryEntity;
import com.ChatHub.chathub_backend.repository.ChatHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatHistoryService{

    private final ChatHistoryRepository chatHistoryRepository;

    @Autowired
    public ChatHistoryService(ChatHistoryRepository chatHistoryRepository) {
        this.chatHistoryRepository = chatHistoryRepository;
    }

    public void save(ChatHistoryEntity chatHistoryEntity) {
        chatHistoryRepository.save(chatHistoryEntity);
    }

    public List<ChatHistoryEntity> getMessagesFrom5Days() {
        LocalDateTime fiveDaysAgo = LocalDateTime.now().minusDays(5);
        return chatHistoryRepository.findByTimeAfter(fiveDaysAgo);
    }
}
