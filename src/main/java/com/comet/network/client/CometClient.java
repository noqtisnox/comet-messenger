package com.comet.network.client;

import com.comet.model.Message;
import com.comet.network.payload.ChatMessagePayload;
import com.comet.network.payload.NetworkEnvelope;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.function.Consumer;

public class CometClient extends WebSocketClient {

    private final ObjectMapper mapper;

    private Consumer<Message> onMessageReceived;

    public CometClient(URI serverUri) {
        super(serverUri);
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
    }

    public void setOnMessageReceived(Consumer<Message> onMessageReceived) {
        this.onMessageReceived = onMessageReceived;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected to Comet Server!");
    }

    @Override
    public void onMessage(String jsonMessage) {
        try {
            NetworkEnvelope envelope = mapper.readValue(jsonMessage, NetworkEnvelope.class);

            if ("NEW_MESSAGE".equals(envelope.type())) {

                Message incomingMessage = mapper.treeToValue(envelope.payload(), Message.class);

                if (onMessageReceived != null) {
                    // CRITICAL: WebSockets run on a background thread.
                    // Platform.runLater forces this update to happen on the JavaFX Main thread!
                    Platform.runLater(() -> onMessageReceived.accept(incomingMessage));
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to parse incoming message: " + e.getMessage());
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Disconnected from server: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("WebSocket Client Error: " + ex.getMessage());
    }

    /**
     * Called by your JavaFX Controller when the user clicks "Send"
     */
    public void sendChatMessage(Long chatId, Long senderId, String content) {
        try {
            ChatMessagePayload payload = new ChatMessagePayload(chatId, senderId, content);

            NetworkEnvelope envelope = new NetworkEnvelope("SEND_MESSAGE", mapper.valueToTree(payload));

            String jsonOutput = mapper.writeValueAsString(envelope);
            send(jsonOutput);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}