package com.comet.ui.controller;

import com.comet.App;
import com.comet.model.Chat;
import com.comet.model.Message;
import com.comet.model.User;
import com.comet.model.UserSession;
import com.comet.repository.ChatRepository;
import com.comet.repository.ContactRepository;
import com.comet.service.ChatService;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

public class ChatController {

    @FXML
    private ListView<Chat> chatListView;

    @FXML
    private ListView<String> contactListView;

    @FXML
    private ImageView userImageView;

    @FXML
    private Label userDisplayName;

    @FXML
    private TextArea chatArea;

    @FXML
    private TextField messageField;

    @FXML
    private Label currentChatLabel;

    private ChatService chatService;
    private User currentUser;
    private Chat currentChat;

    @FXML
    public void initialize() {
        this.currentUser = UserSession.getInstance().getCurrentUser();

        if (this.currentUser == null) {
            currentChatLabel.setText("Not authenticated.");
            messageField.setDisable(true);
            chatArea.setDisable(true);
            return;
        }

        this.chatService = new ChatService(
            new ChatRepository(),
            new ContactRepository()
        );

        userDisplayName.setText(currentUser.username());
        currentChatLabel.setText("No chat selected");
        chatArea.setEditable(false);

        setupListViews();

        if (App.cometClient != null) {
            App.cometClient.setOnMessageReceived(this::onMessageReceived);
        }

        loadChats();
        // loadContacts();
    }

    private void setupListViews() {
        chatListView.setCellFactory(param ->
            new ListCell<>() {
                @Override
                protected void updateItem(Chat chat, boolean empty) {
                    super.updateItem(chat, empty);
                    if (empty || chat == null) {
                        setText(null);
                    } else {
                        setText(
                            chat.isGroupChat()
                                ? chat.groupName()
                                : "Private Chat ID: " + chat.id()
                        );
                    }
                }
            }
        );

        chatListView
            .getSelectionModel()
            .selectedItemProperty()
            .addListener((obs, oldChat, newChat) -> {
                if (newChat != null) {
                    this.currentChat = newChat;
                    currentChatLabel.setText(
                        newChat.isGroupChat()
                            ? newChat.groupName()
                            : "Private Chat"
                    );
                    loadChatHistory(newChat.id());
                }
            });
    }

    private void loadChats() {
        // TODO: You will need to add a 'getChatsForUser(userId)' method to ChatService/ChatRepository
        // List<Chat> userChats = chatService.getChatsForUser(currentUser.id());
        // chatListView.getItems().setAll(userChats);
    }

    private void loadChatHistory(Long chatId) {
        chatArea.clear();
        List<Message> history = chatService.getChatHistory(chatId, 50);
        for (Message msg : history) {
            appendMessageToUI(msg);
        }
    }

    @FXML
    private void handleSend() {
        String text = messageField.getText().trim();
        if (!text.isEmpty() && currentChat != null) {
            App.cometClient.sendChatMessage(
                currentChat.id(),
                currentUser.id(),
                text
            );
            messageField.clear();
        } else if (currentChat == null) {
            System.err.println("Cannot send message: No chat selected.");
        }
    }

    /**
     * This is called automatically by CometClient when a message arrives from the server.
     */
    private void onMessageReceived(Message msg) {
        if (currentChat != null && msg.chatId().equals(currentChat.id())) {
            appendMessageToUI(msg);
        } else {
            if (!msg.senderId().equals(currentUser.id())) {
                showNotification(msg);
            }
        }
    }

    private void appendMessageToUI(Message msg) {
        String senderName = msg.senderId().equals(currentUser.id())
            ? "You"
            : "User " + msg.senderId();
        chatArea.appendText(senderName + ": " + msg.content() + "\n");
        chatArea.setScrollTop(Double.MAX_VALUE); // Auto-scroll to bottom
    }

    private void showNotification(Message msg) {
        Notifications.create()
            .title("New Message in Chat " + msg.chatId())
            .text("User " + msg.senderId() + ": " + msg.content())
            .hideAfter(Duration.seconds(4))
            .position(javafx.geometry.Pos.TOP_RIGHT)
            .darkStyle()
            .showInformation();
    }

    @FXML
    private void handleAddChat() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create Group Chat");
        dialog.setHeaderText("Enter a name for the new group chat:");
        dialog
            .showAndWait()
            .ifPresent(groupName -> {
                // TODO: Implement createGroupChat in ChatService
                // chatService.createGroupChat(groupName, currentUser.id());
                // loadChats(); // Refresh the list
            });
    }

    // handleAddUser(), handleUpdateProfile(), etc., follow the exact same pattern!
}
