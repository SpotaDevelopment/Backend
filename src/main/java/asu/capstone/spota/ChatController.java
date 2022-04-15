package asu.capstone.spota;


import asu.capstone.spota.chatmodels.ChatMessage;
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

@Controller
public class ChatController {
    private static final Gson gson = new Gson();

    @Autowired private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/greeting")
    @SendTo("/topic/greetings")
    public ChatMessage greeting(ChatMessage message) throws Exception {
        Thread.sleep(1000); // simulated delay
        return new ChatMessage(HtmlUtils.htmlEscape(message.getMessage()) + "!");
    }

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage message) {
        messagingTemplate.convertAndSendToUser(
                message.getRecipientId(),"/queue/messages","hello Brian");
    }
}
