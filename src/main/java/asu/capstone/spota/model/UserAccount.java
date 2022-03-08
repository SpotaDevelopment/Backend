package asu.capstone.spota.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;

@Data
public class UserAccount {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private Date birthday;
    //private Image accountPicture;
    private ArrayList<League> favLeagues;
    private ArrayList<ChatRoom> chats;
    private ArrayList<UserAccount> friends;
    private ArrayList<Notification> notifications; //will not be entirely persistent but updated since last login

    public UserAccount() { }

    public UserAccount(String firstName, String lastName, String username, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        //this.birthday = parseBirthday(birthday);
    }

    public Date parseBirthday(String birthday) {
        int year = 0;
        int mon = 0;
        int date = 0;
        int count = 0;
        int start = 0;
        int end = 0;

        if(birthday.length() > 10) {
            return null;
        } else {
            for(int i = 0; i < birthday.length(); i++) {
                if(birthday.charAt(i) == '-') {
                    end = i;
                    switch(count) {
                        case 0: year = Integer.parseInt(birthday.substring(start, end));
                        case 1: mon = Integer.parseInt(birthday.substring(start, end));
                    }
                    start = i+1;
                }
            }
        }
        return new Date(year, mon, date);
    }
}