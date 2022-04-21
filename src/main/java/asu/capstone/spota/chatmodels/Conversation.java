package asu.capstone.spota.chatmodels;

import lombok.Data;

import java.util.List;

@Data
public class Conversation {
    private String groupChatName;
    private List<ChatMessage> messageList;

}
