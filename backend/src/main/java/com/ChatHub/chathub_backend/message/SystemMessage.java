package com.ChatHub.chathub_backend.message;

public class SystemMessage extends BaseMessage {
    public static final String SYSTEM_MESSAGE = "SYSTEM_MESSAGE";

    private String name;

    public SystemMessage() {
        super(SYSTEM_MESSAGE);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SystemMessage(String message, String name) {
        this();
        this.setMessage(message);
        this.name = name;
    }


}