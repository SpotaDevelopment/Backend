package asu.capstone.spota.services;

import asu.capstone.spota.chatmodels.ChatMessage;
import asu.capstone.spota.model.UserAccount;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    @Autowired
    private UserDataService userDataService;

    private static final Gson gson = new Gson();

    public String getConversations(UserAccount user) {
        return null;
    }

    public boolean saveMessage(ChatMessage message) {
        return true;
    }

}