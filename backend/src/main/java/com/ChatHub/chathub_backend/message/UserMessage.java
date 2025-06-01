package com.ChatHub.chathub_backend.message;

import com.ChatHub.chathub_backend.entity.ChatHistoryEntity;

public class UserMessage extends BaseMessage {
    public static final String USER_MESSAGE = "USER_MESSAGE";
    private String name;

    public UserMessage() {
        super(USER_MESSAGE);
    }

    public UserMessage(String message, String name) {
        this();
        this.setMessage(message);
        this.name = name;
    }

    public UserMessage(ChatHistoryEntity chatHistoryEntity) {
        this.setType(chatHistoryEntity.getType());
        this.setMessage(chatHistoryEntity.getMessage());
        this.name = chatHistoryEntity.getUsername();
        this.setTime(chatHistoryEntity.getTime());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}