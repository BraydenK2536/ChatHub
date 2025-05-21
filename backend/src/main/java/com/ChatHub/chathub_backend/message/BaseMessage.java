package com.ChatHub.chathub_backend.message;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.LocalDateTime;

//使用@JsonTypeInfo和@JsonSubTypes识别多态并自动创建对应对象
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SystemMessage.class, name = "SYSTEM_MESSAGE"),
        @JsonSubTypes.Type(value = UserMessage.class, name = "USER_MESSAGE")
})
public class BaseMessage {

    private String type;

    private String message;

    private long timestamp;

    private String time;

    public BaseMessage() {
        this.timestamp = System.currentTimeMillis();
    }

    public BaseMessage(String type) {
        this.type = type;
        this.timestamp = System.currentTimeMillis();
        this.time = time;
    }

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
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
