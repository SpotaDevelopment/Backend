package asu.capstone.spota.chatmodels;

import lombok.Data;

@Data
public class ChatMessage {
    private String messageContent;
    private String senderId;
    private String recipientId;
    private String groupChat;
    private String chatTimeStamp;
    //private String chatId;

    public ChatMessage() {

    }

    public ChatMessage(String message) {
        this.messageContent = message;
    }

    public ChatMessage(String message, String senderId, String recipientId, String groupChat, String chatTimeStamp) {
        this.messageContent = message;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.groupChat = groupChat;
        this.chatTimeStamp = chatTimeStamp;
    }
}
