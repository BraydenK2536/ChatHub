package com.ChatHub.chathub_backend.message;

public class BaseMessage {

    public BaseMessage() {
    }

    public BaseMessage(String type) {
        this.type = type;
    }

    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
