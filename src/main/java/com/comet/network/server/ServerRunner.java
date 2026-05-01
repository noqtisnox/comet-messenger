package com.comet.network.server;

import com.comet.repository.ChatRepository;
import com.comet.repository.ContactRepository;
import com.comet.service.ChatService;

public class ServerRunner {
    public static void main(String[] args) {
        ChatRepository chatRepo = new ChatRepository();
        ContactRepository contactRepo = new ContactRepository();
        ChatService chatService = new ChatService(chatRepo, contactRepo);

        CometServer server = new CometServer(8887, chatService);
        server.start();
    }
}
