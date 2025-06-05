package com.ChatHub.chathub_backend.test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Formatter;

public class timetest {
    public static void main(String[] args) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 格式化当前日期和时间
        String formattedDateTime = LocalDateTime.now().format(formatter);
    }
}
