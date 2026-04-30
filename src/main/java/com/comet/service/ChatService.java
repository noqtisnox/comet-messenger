package com.comet.service;

import com.comet.model.Message;
import com.comet.model.Contact;
import com.comet.model.enums.ContactStatus;
import com.comet.repository.ChatRepository;
import com.comet.repository.ContactRepository;

import java.util.List;
import java.util.Optional;

public class ChatService {

    private final ChatRepository chatRepository;
    private final ContactRepository contactRepository;

    public ChatService(ChatRepository chatRepository, ContactRepository contactRepository) {
        this.chatRepository = chatRepository;
        this.contactRepository = contactRepository;
    }

    /**
     * Saves a new message to the database.
     * @return The fully populated Message object (with timestamps/IDs), or null if it failed.
     */
    public Message sendMessage(Long chatId, Long senderId, String content) {
        if (content == null || content.trim().isEmpty()) {
            return null; // Don't save empty messages
        }

        return chatRepository.saveMessage(chatId, senderId, content.trim());
    }

    /**
     * Retrieves the history of a specific chat.
     */
    public List<Message> getChatHistory(Long chatId, int limit) {
        return chatRepository.getMessagesForChat(chatId, limit);
    }

    /**
     * Sends a friend request to another user.
     */
    public boolean sendFriendRequest(Long userId, Long contactId) {
        if (userId.equals(contactId)) {
            return false; // Can't add yourself
        }
        Optional<Contact> request = contactRepository.addContactRequest(userId, contactId);
        return request.isPresent();
    }

    /**
     * Accepts a pending friend request.
     */
    public boolean acceptFriendRequest(Long userId, Long contactId) {
        return contactRepository.updateContactStatus(userId, contactId, ContactStatus.ACCEPTED);
    }

    /**
     * Gets all accepted friends for a user.
     */
    public List<Contact> getFriendsList(Long userId) {
        return contactRepository.getContactsByStatus(userId, ContactStatus.ACCEPTED);
    }
}