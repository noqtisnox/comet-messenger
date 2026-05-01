package com.comet.network.server;

import com.comet.model.Message;
import com.comet.network.payload.ChatMessagePayload;
import com.comet.network.payload.NetworkEnvelope;
import com.comet.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CometServer extends WebSocketServer {
    private final Set<WebSocket> activeConnections = Collections.synchronizedSet(new HashSet<>());

    private final ObjectMapper mapper;
    private final ChatService chatService;

    public CometServer(int port, ChatService chatService) {
        super(new InetSocketAddress(port));
        this.chatService = chatService;

        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        activeConnections.add(conn);
        System.out.println("New connection: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        activeConnections.remove(conn);
        System.out.println("Closed connection: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        try {
            NetworkEnvelope envelope = mapper.readValue(message, NetworkEnvelope.class);

            switch (envelope.type()) {
                case "SEND_MESSAGE" -> handleIncomingChatMessage(envelope.payload());
                // In the future: case "TYPING" -> handleTyping();
                default -> System.err.println("Unknown message type: " + envelope.type());
            }
        } catch (Exception e) {
            System.err.println("Failed to process message: " + e.getMessage());
        }
    }

    private void handleIncomingChatMessage(com.fasterxml.jackson.databind.JsonNode payloadNode) {
        try {
            ChatMessagePayload payload = mapper.treeToValue(payloadNode, ChatMessagePayload.class);

            Message savedMessage = chatService.sendMessage(
                    payload.chatId(),
                    payload.senderId(),
                    payload.content()
            );

            if (savedMessage != null) {
                NetworkEnvelope broadcastEnvelope = new NetworkEnvelope(
                        "NEW_MESSAGE",
                        mapper.valueToTree(savedMessage)
                );

                String jsonBroadcast = mapper.writeValueAsString(broadcastEnvelope);

                // Later, filter by chatId
                broadcast(jsonBroadcast);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("Comet WebSocket Server started on port: " + getPort());
    }
}