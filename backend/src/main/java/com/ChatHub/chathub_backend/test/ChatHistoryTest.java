package com.ChatHub.chathub_backend.test;

import com.ChatHub.chathub_backend.entity.ChatHistoryEntity;
import com.ChatHub.chathub_backend.entity.UserAccountEntity;
import com.ChatHub.chathub_backend.message.UserMessage;
import com.ChatHub.chathub_backend.repository.UserAccountRepository;
import com.ChatHub.chathub_backend.service.ChatHistoryService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;

import java.util.Optional;

//@Service
public class ChatHistoryTest {

    private ChatHistoryService chatHistoryService;

//    @Autowired
    public ChatHistoryTest(ChatHistoryService chatHistoryService) {
        this.chatHistoryService = chatHistoryService;
    }

    @PostConstruct
    public void runTests() {
        for (ChatHistoryEntity messages : chatHistoryService.getMessagesFrom5Days()) {
            System.out.println(messages);
        }
    }

}
