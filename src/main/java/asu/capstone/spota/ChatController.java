package asu.capstone.spota;


import asu.capstone.spota.chatmodels.ChatMessage;
import asu.capstone.spota.chatmodels.ChatNotification;
import asu.capstone.spota.chatmodels.Greeting;
import asu.capstone.spota.chatmodels.HelloMessage;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class ChatController {
    private static final Gson gson = new Gson();

    @Autowired private SimpMessagingTemplate messagingTemplate;

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
    public void processMessage(@Payload ChatMessage message) {

        /*TODO -> first verify that the sender is a part of the group chat that is in the message body */

        /*TODO -> get all users who are in the group defined in the message and for each user
            call convertAndSendToUser method with userID, destination, and ChatNotification.
         */

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        System.out.println(dtf.format(now)); //printing the time of the message



        messagingTemplate.convertAndSendToUser(
                message.getRecipientId(), message.getGroupChat(),
                new ChatNotification(
                        "1", message.getSenderId(), dtf.format(now)
                )
        );
    }


}
