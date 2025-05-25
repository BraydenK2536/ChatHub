package com.ChatHub.chathub_backend.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

//使用@JsonTypeInfo和@JsonSubTypes识别多态并自动创建对应对象
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SystemMessage.class, name = "SYSTEM_MESSAGE"),
        @JsonSubTypes.Type(value = UserMessage.class, name = "USER_MESSAGE")
})
public class BaseMessage {

    private String type;

    private String message;

    private String time;

    public BaseMessage() {
        this.time = formatTime(LocalDateTime.now());
    }

    public BaseMessage(String type) {
        this.type = type;
        this.time = formatTime(LocalDateTime.now());
    }

    public BaseMessage(String type, String message) {
        this.type = type;
        this.time = formatTime(LocalDateTime.now());
        this.message = message;
    }

    @JsonIgnore//防止序列化时打印两遍
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    private String formatTime(LocalDateTime time) {
        return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
