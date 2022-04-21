package asu.capstone.spota.services;

import asu.capstone.spota.chatmodels.ChatMessage;
import asu.capstone.spota.chatmodels.Conversation;
import asu.capstone.spota.model.UserAccount;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatService {

    @Value("${spring.datasource.url}")
    private String DB_URL;

    @Value("${spring.datasource.username}")
    private String USER;

    @Value("${spring.datasource.password}")
    private String PASS;

    @Autowired
    private NBAService nbaService;

    @Autowired @Lazy
    private UserDataService userDataService;

    private static final Gson gson = new Gson();

    public String getConversations(UserAccount user) {
        try(Connection dbc = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = dbc.createStatement();) {

            List<Conversation> conversationList = new ArrayList<>();
            String groupChatQuery = String.format(
                    "SELECT * FROM hasgroupchat WHERE groupcreator='%s' OR email='%s' OR username='%s';", user.getEmail(), user.getEmail(), user.getUsername());

            ResultSet groupChatResults = stmt.executeQuery(groupChatQuery);

            while(groupChatResults.next()) {
                Conversation conversation = new Conversation();
                String groupName = groupChatResults.getString("groupname");
                conversation.setGroupChatName(groupName);

                List<ChatMessage> messages = new ArrayList<>();

                String messageQuery = String.format(
                        "SELECT * FROM conversations WHERE groupname='%s'", groupName
                );

                ResultSet messageResults = stmt.executeQuery(messageQuery);

                while(messageResults.next()) {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setMessageContent(messageResults.getString("chatmessage"));
                    chatMessage.setGroupChat(messageResults.getString("groupname"));
                    chatMessage.setSenderId(messageResults.getString("senderid"));
                    chatMessage.setChatTimeStamp(messageResults.getString("chatdate"));
                    messages.add(chatMessage);
                }
                conversation.setMessageList(messages);
                conversationList.add(conversation);
            }
            return gson.toJson(conversationList);

        } catch(SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean saveMessage(ChatMessage message) {
        String sqlCommand = String.format(
                "INSERT INTO conversations values ('%s', '%s', '%s', '%s');",
                message.getMessageContent(),
                message.getChatTimeStamp(),
                message.getGroupChat(),
                message.getSenderId()
        );

        if(userDataService.updateDB(sqlCommand)) {
            return true;
        } else {
            return false;
        }
    }

}