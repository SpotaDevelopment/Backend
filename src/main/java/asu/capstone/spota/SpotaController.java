package asu.capstone.spota;

import asu.capstone.spota.model.*;
import asu.capstone.spota.services.*;
import com.google.gson.Gson;

import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
public class SpotaController {

    @Autowired
    private UserDataService userDataService;

    private static final Gson gson = new Gson();

	@GetMapping(value = "/hello/{name}")
	public String hello(@PathVariable String name) {

        return String.format("Hello %s!", name);
	}

    //request for getting all NBA news for a user given their email address
    @GetMapping(path = "/users/getNews/{email}")
    public String getNews(@PathVariable String email) throws IOException, InterruptedException {
        String response = userDataService.getNews(email);

        System.out.println(response);
        return response;
    }

    @GetMapping(path = "/users/getGeneralNews")
    public String getGeneralNews() throws IOException, InterruptedException {
        String response = userDataService.getGeneralNews();

        System.out.println(response);
        return response;
    }

    //request for getting the latest NBA scores for a user
    @GetMapping(path = "/users/getScores/{email}")
    public String getScores(@PathVariable String email) {
        String response = userDataService.getScores(email);

        System.out.println(response);
        return response;
    }

    //request for getting latest NBA scores across the league
    @GetMapping(path = "/users/getGeneralScores")
    public String getGeneralScores() {
        String response = userDataService.getGeneralScores();

        System.out.println(response);
        return response;
    }

    //request for signing up a new user account and adding them to DB
    @PostMapping(path = "/users/signUp",
                 consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createUserAccount(@RequestBody String userAccount) {
        UserAccount newUser = null;

        try {
            UserAccount reqBody = gson.fromJson(userAccount, UserAccount.class);
            newUser = new UserAccount(reqBody.getFirstName(), reqBody.getLastName(), reqBody.getUsername(), reqBody.getEmail(), reqBody.getBirthday());

        } catch (Exception e) {
            e.printStackTrace();
        }
        if(userDataService.addNewUserAccount(newUser)) {
            return new ResponseEntity<>(gson.toJson(userAccount), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("user already exists in database", HttpStatus.BAD_REQUEST);
        }
    }

    //request for adding a team subscription to a users account
    @PostMapping(path = "/users/teamSubscription/{teamName}/{email}")
    public ResponseEntity<String> addTeamSubscription(@PathVariable String teamName, @PathVariable String email) {
        try {
            if(userDataService.userExists(email)) {
                if(userDataService.addTeamSubscription(email, teamName)) {
                    //successfully added team to user account subscription
                    return new ResponseEntity<>("successfully added team", HttpStatus.OK);
                } else {
                    //could not add team since team already exists in subscriptions
                    return new ResponseEntity<>("team already exists in subscriptions", HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<>("user doesn't exist in system", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<String>("could not parse user account", HttpStatus.BAD_REQUEST);
    }

    //request for removing a team from the subscriptions of a user account
    @PostMapping(path = "/users/removeTeamSubscription/{teamName}/{email}")
    public ResponseEntity<String> removeTeamSubscription(@PathVariable String teamName, @PathVariable String email) {
        try {
            if(userDataService.userExists(email)) {
                if(userDataService.removeTeamSubscription(email, teamName)) {
                    //successfully added team to user account subscription
                    return new ResponseEntity<>("successfully removed the team", HttpStatus.OK);
                } else {
                    //could not remove the team since team does not exist in subscriptions
                    return new ResponseEntity<>("team does not exist in subscriptions", HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<>("user doesn't exist in system", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<String>("could not parse user account", HttpStatus.BAD_REQUEST);
    }
}
