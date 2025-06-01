package com.ChatHub.chathub_backend.entity;

import com.ChatHub.chathub_backend.message.UserMessage;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_history")
public class ChatHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;//唯一标识

    @Column()
    private LocalDateTime time;

    @Column()
    private String username;

    @Lob
    private String message;

    @Column(nullable = false, length = 64)
    private String type;


    public ChatHistoryEntity() {

    }

    public ChatHistoryEntity(UserMessage userMessage) {
        this.time = userMessage.getTime();
        this.username = userMessage.getName();
        this.message = userMessage.getMessage();
        this.type = userMessage.getType();
    }

    public ChatHistoryEntity(String username, String message, String type) {
        this.time = LocalDateTime.now();
        this.username = username;
        this.message = message;
        this.type = type;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ChatHistoryEntity{" +
                "id=" + id +
                ", time=" + time +
                ", username='" + username + '\'' +
                ", message='" + message + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
