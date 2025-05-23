package com.ChatHub.chathub_backend;

import com.ChatHub.chathub_backend.websocket.ChatWebSocketHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@SpringBootApplication
public class ChathubBackendApplication {

    public static void main(String[] args) {
		SpringApplication.run(ChathubBackendApplication.class, args);


	}

}
