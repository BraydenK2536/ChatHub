package com.ChatHub.chathub_backend.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class timeUtil {

    public static String getTime() {
        // 格式化当前日期和时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "[" + LocalDateTime.now().format(formatter) + "]";
    }

}
