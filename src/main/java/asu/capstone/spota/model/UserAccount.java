package asu.capstone.spota.model;

import lombok.Data;

import java.time.temporal.TemporalQueries;
import java.util.ArrayList;
import java.util.Date;

@Data
public class UserAccount {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String birthday;
    private String profile_color;
    //private Image accountPicture;
    private ArrayList<League> favLeagues;
    private ArrayList<ChatRoom> chats;
    private ArrayList<String> friends;
    private ArrayList<Notification> notifications; //will not be entirely persistent but updated since last login

    public UserAccount() { }

    public UserAccount(String firstName, String lastName, String username, String email, String birthday, String profile_color) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.birthday = birthday;
        this.profile_color = profile_color;
    }

    public UserAccount(String username, String email) {
        this.username = username;
        this.email = email;
    }
}