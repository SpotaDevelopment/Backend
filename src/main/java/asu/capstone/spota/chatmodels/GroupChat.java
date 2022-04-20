package asu.capstone.spota.chatmodels;

import asu.capstone.spota.model.UserAccount;
import lombok.Data;

import java.util.List;

@Data
public class GroupChat {
    private String groupName;
    private List<UserAccount> users;
}
