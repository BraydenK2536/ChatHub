package com.ChatHub.chathub_backend.websocket;

import com.ChatHub.chathub_backend.message.BaseMessage;
import com.ChatHub.chathub_backend.message.SystemMessage;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Scanner;

@Service
public class ServerBroadcast {

    private final ChatWebSocketHandler chatWebSocketHandler;
    @Autowired
    public ServerBroadcast(ChatWebSocketHandler chatWebSocketHandler) {
        this.chatWebSocketHandler = chatWebSocketHandler;
    }

    @PostConstruct
    public void startConsoleListener() {
        Thread consoleListenerThread = new Thread(() -> {
            System.out.println("服务器控制台广播功能已启动。");
            System.out.println("在此控制台输入任何文本并按回车，消息将被广播给所有连接的 WebSocket 客户端。");
            System.out.println("输入 'exit_broadcast' 来停止此控制台广播功能。");

            try (Scanner scanner = new Scanner(System.in)) { // 使用 try-with-resources 确保 Scanner 关闭
                while (true) {
                    if (Thread.currentThread().isInterrupted()) {
                        System.out.println("控制台监听线程中断");
                        break;
                    }

                    System.out.print("输入要广播的消息: ");
                    if (scanner.hasNextLine()) {
                        String input = scanner.nextLine();

                        if (input.equals("消息轰炸")) {
                            System.out.println("输入消息");
                            String message = scanner.nextLine();
                            System.out.println("输入次数");
                            int times = scanner.nextInt();
                            for (int i = 0; i < times; i++) {
                                chatWebSocketHandler.broadcastMessage(null,new SystemMessage(message,"SYSTEM"));
                            }
                        }

                        if ("exit_broadcast".equalsIgnoreCase(input.trim())) {
                            System.out.println("控制台广播功能已停止。");
                            break;
                        }

                        String messageToBroadcast = input;

                        if (chatWebSocketHandler != null) {
                            chatWebSocketHandler.broadcastMessage(null, new SystemMessage(messageToBroadcast,"SYSTEM"));
                            System.out.println("消息已广播: \"" + input + "\"");
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("控制台监听线程发生错误: " + e.getMessage());
                e.printStackTrace();
            } finally {
                System.out.println("控制台监听线程已结束。");
            }
        });

        consoleListenerThread.setName("ConsoleBroadcasterThread");
        consoleListenerThread.setDaemon(true); // 设置为守护线程，主应用程序退出时，此线程也会自动退出。
        consoleListenerThread.start();
    }
}
