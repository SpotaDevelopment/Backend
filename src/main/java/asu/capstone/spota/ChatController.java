package asu.capstone.spota;


import asu.capstone.spota.chatmodels.ChatMessage;
import asu.capstone.spota.chatmodels.ChatNotification;
import asu.capstone.spota.chatmodels.Greeting;
import asu.capstone.spota.chatmodels.HelloMessage;
import asu.capstone.spota.model.UserAccount;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ChatController {
    @Value("${spring.datasource.url}")
    private String DB_URL;

    @Value("${spring.datasource.username}")
    private String USER;

    @Value("${spring.datasource.password}")
    private String PASS;

    private static final Gson gson = new Gson();
    Connection dbc = DriverManager.getConnection(DB_URL, USER, PASS);



    @Autowired private SimpMessagingTemplate messagingTemplate;

    public ChatController() throws SQLException {
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

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String messageDate = dtf.format(now);
        System.out.println(dtf.format(now)); //printing the time of the message
        System.out.println("Message senderID: " + message.getSenderId());
        System.out.println("Message recepientID: " + message.getRecipientId());

        /*TODO -> first verify that the sender is a part of the group chat that is in the message body */

        /*TODO -> get all users who are in the group defined in the message and for each user
            call convertAndSendToUser method with userID, destination, and ChatNotification.
         */

        Statement stmt = dbc.createStatement();

        //query for returning all users who are part of the group chat that the message is for (not including the sender)
        String sqlQuery = String.format("u.email, u.username, u.firstname, u.lastname, u.birthday, u.profile_color" +
                "FROM Users u, groupchat gr, hasgroupchat h" +
                "WHERE (u.email = h.email AND u.email != '%s') AND h.groupID=gr.groupID AND h.groupname=gr.groupname;", message.getSenderId());


        ResultSet resultSet = stmt.executeQuery(sqlQuery);

        /*iterating through each user in the group chat and sending the ChatMessage object as payload
          to the destination -> /users/username/messages
        */
        while(resultSet.next()) {
            messagingTemplate.convertAndSendToUser(
                    message.getSenderId(),"messages",
                    new ChatMessage(
                            message.getMessageContent(),
                            message.getSenderId(),
                            message.getRecipientId(),
                            message.getGroupChat()
                    )
            );
        }

        /*if(names.length == 1) {
            sqlQuery = String.format("SELECT * FROM users WHERE firstname='%s' OR lastname='%s';", names[0]);
        } else if(names.length == 2) {
            sqlQuery = String.format("SELECT * FROM users WHERE firstname='%s' AND lastname='%s';", names[0], names[1]);
        }

        if(sqlQuery != null) {
            ResultSet resultSet = stmt.executeQuery(sqlQuery);

            while(resultSet.next()) {
                UserAccount user = new UserAccount();
                user.setEmail(resultSet.getString("email"));
                user.setFirstName(resultSet.getString("firstname"));
                user.setLastName(resultSet.getString("lastname"));
                user.setUsername(resultSet.getString("username"));
                user.setBirthday(resultSet.getString("birthday"));
                user.setProfile_color(resultSet.getString("profile_color"));
                users.add(user);
            }
        }*/

        //sending the message to a recipient who is in the group chat of the message being processed
        /*messagingTemplate.convertAndSendToUser(
                message.getGroupChat(),recepient + "/messages",
                new ChatMessage(
                        message.getMessageContent(),
                        message.getSenderId(),
                        message.getRecipientId(),
                        message.getGroupChat()
                )
        );*/
    }
}
