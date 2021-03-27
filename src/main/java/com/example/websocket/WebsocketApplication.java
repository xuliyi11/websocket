package com.example.websocket;

import com.example.websocket.websocket.WebsocketNettyServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebsocketApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebsocketApplication.class, args);

        try {
            new WebsocketNettyServer(9999).start();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
