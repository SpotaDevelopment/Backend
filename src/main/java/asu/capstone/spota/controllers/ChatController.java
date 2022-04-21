package asu.capstone.spota.controllers;


import asu.capstone.spota.chatmodels.ChatMessage;
import asu.capstone.spota.chatmodels.ChatNotification;
import asu.capstone.spota.chatmodels.Greeting;
import asu.capstone.spota.chatmodels.HelloMessage;
import asu.capstone.spota.model.UserAccount;
import asu.capstone.spota.services.ChatService;
import asu.capstone.spota.services.UserDataService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
@RestController
public class ChatController {
    @Value("${spring.datasource.url}")
    private String DB_URL;

    @Value("${spring.datasource.username}")
    private String USER;

    @Value("${spring.datasource.password}")
    private String PASS;

    private static final Gson gson = new Gson();


    @Autowired private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatService chatService;

    public ChatController() {

    }

    public void sendNotification(String senderID, String destination, String message) {
        System.out.println("sendNotification getting called by userDataService");
        this.messagingTemplate.convertAndSendToUser(
                senderID,
                destination,
                message
        );
    }

    //request for getting all conversations for a user
    @GetMapping(path = "/users/getConversations")
    public ResponseEntity<String> getConversations(@RequestBody UserAccount user ) {
        if(user == null) {
            return new ResponseEntity<>("user account cannot be null", HttpStatus.BAD_REQUEST);
        }
        try {
            String response = chatService.getConversations(user);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("could not get conversations", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("there was an unexpected error", HttpStatus.BAD_REQUEST);
    }

    //request for uploading a new chat to the database
    @PostMapping(path = "/users/messages/saveMessage")
    public ResponseEntity<String> saveMessage(@RequestBody ChatMessage message) {
        try {
            if(chatService.saveMessage(message)) {
                return new ResponseEntity<>("successfully uploaded the message", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("unable to upload the message", HttpStatus.BAD_REQUEST);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>("unable to upload the message", HttpStatus.BAD_REQUEST);
    }


    @MessageMapping("/message")
    @SendTo("/topic/messages")
    public ChatMessage getMessage(ChatMessage message) throws InterruptedException {
        Thread.sleep(1000); //delay
        return new ChatMessage(HtmlUtils.htmlEscape(message.getMessageContent()));
    }

    @MessageMapping("/greeting")
    @SendTo("/topic/greetings")
    public ChatMessage greeting(ChatMessage message) throws Exception {
        Thread.sleep(1000); // simulated delay
        return new ChatMessage(HtmlUtils.htmlEscape(message.getMessageContent()) + "!");
    }

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage message) throws SQLException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        LocalDateTime now = LocalDateTime.now();
        String messageDate = dtf.format(now);
        System.out.println(dtf.format(now)); //printing the time of the message
        System.out.println("Message senderID: " + message.getSenderId());
        System.out.println("Message recepientID: " + message.getRecipientId());

        /*TODO -> first verify that the sender is a part of the group chat that is in the message body */

        /*TODO -> get all users who are in the group defined in the message and for each user
            call convertAndSendToUser method with userID, destination, and ChatNotification.
         */

        try {
            Connection dbc = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = dbc.createStatement();

            //query for returning all users who are part of the group chat that the message is for (not including the sender)
            String sqlQuery = String.format(
                    "SELECT u " +
                    "FROM users u, group_chat gr, hasgroupchat h " +
                    "WHERE (u.email != '%s' " +
                    "AND h.groupcreator=gr.groupcreator " +
                    "AND gr.groupcreator='%s' " +
                    "AND h.groupname=gr.groupname " +
                    "AND h.groupname='%s' " +
                    "AND h.username=u.username " +
                    "AND h.email=u.email);",
                    message.getSenderId(),
                    message.getSenderId(),
                    message.getGroupChat());

            //System.out.println("SQL QUERY: " + sqlQuery);

            ResultSet resultSet = stmt.executeQuery(sqlQuery);

        /*iterating through each user in the group chat and sending the ChatMessage object as payload
          to the destination -> /users/username/messages
        */
            while (resultSet.next()) {
                messagingTemplate.convertAndSendToUser(
                        message.getSenderId(), "messages",
                        new ChatMessage(
                                message.getMessageContent(),
                                message.getSenderId(),
                                message.getRecipientId(),
                                message.getGroupChat()
                        )
                );
            }



            dbc.close(); //closing DB connection

        } catch(SQLException e) {
            System.out.println("sql exception");
            e.printStackTrace();
        }
    }
}
