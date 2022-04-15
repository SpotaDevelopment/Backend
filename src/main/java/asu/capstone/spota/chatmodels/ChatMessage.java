package asu.capstone.spota.chatmodels;

import lombok.Data;

@Data
public class ChatMessage {
    private String message;
    private String senderId;
    private String recipientId;
    private String chatId;

    public ChatMessage() {

    }

    public ChatMessage(String message) {
        this.message = message;
    }

    public ChatMessage(String message, String senderId, String recipientId) {
        this.message = message;
        this.senderId = senderId;
        this.recipientId = recipientId;
    }
}
