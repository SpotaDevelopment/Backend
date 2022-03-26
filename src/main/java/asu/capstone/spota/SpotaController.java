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
    final int foo = 8;

    @Autowired
    private UserDataService userDataService;

    private static final Gson gson = new Gson();

    @GetMapping("/")
    String helloWorld() {
        return "Hello World";
    }

	@GetMapping(value = "/hello/{name}")
	public String hello(@PathVariable String name) {

        return String.format("Hello %s!", name);
	}

    //request for getting a user object
    @GetMapping(path = "/users/getUserByEmail/{email}")
    public ResponseEntity<String> getUserByEmail(@PathVariable String email) throws IOException, InterruptedException {
        String response = userDataService.getUserByEmail(email);

        if(response == null) {
            return new ResponseEntity<>("there is no user with that email", HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    //request for getting a user by username
    @GetMapping(path = "/users/getUserByUsername/{username}")
    public ResponseEntity<String> getUserByUsername(@PathVariable String username) throws IOException, InterruptedException {
        String response = userDataService.getUserByUsername(username);

        if(response == null) {
            return new ResponseEntity<>("there is no user with that username", HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    //request for getting a list of users with first and last name
    @GetMapping(path = "/users/getUsersByName/{name}")
    public ResponseEntity<String> getUserByName(@PathVariable String name) {
        String[] names = name.split(" ");

        String response = userDataService.getUsersByName(names);

        if(response == null) {
            return new ResponseEntity<>("there is no user with that name", HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    //request for getting all NBA news for a user given their email address
    @GetMapping(path = "/users/getNews/{email}")
    public ResponseEntity<String> getNews(@PathVariable String email) throws IOException, InterruptedException {
        String response = userDataService.getNews(email);

        if(response == null) {
            return new ResponseEntity<>("there is no news to get.", HttpStatus.BAD_REQUEST);
        } else if(response == "user doesn't exist") {
            return new ResponseEntity<>("user doesn't exist.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(path = "/users/getGeneralNews")
    public String getGeneralNews() throws IOException, InterruptedException {
        String response = userDataService.getGeneralNews();

        return response;
    }

    //request for getting the latest NBA scores for a user
    @GetMapping(path = "/users/getScores/{email}")
    public ResponseEntity<String> getScores(@PathVariable String email) {
        String response = userDataService.getScores(email);

        if(response == null) {
            return new ResponseEntity<>("there are no scores to get.", HttpStatus.BAD_REQUEST);
        } else if(response == "user doesn't exist") {
            return new ResponseEntity<>("user doesn't exist.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //request for getting latest NBA scores across the league
    @GetMapping(path = "/users/getGeneralScores")
    public ResponseEntity<String> getGeneralScores() throws IOException, InterruptedException {
        String response = userDataService.getGeneralScores();

        if(response == null) {
            return new ResponseEntity<>("there are no scores to get.", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
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
    @PostMapping(path = "/users/addTeamSubscriptions/{email}")
    public ResponseEntity<String> addTeamSubscriptions(@RequestBody List<String> teamNames, @PathVariable String email) {
        try {
            if(userDataService.userExists(email)) {
                if(userDataService.addTeamsSubscription(email, teamNames)) {
                    //successfully added team to user account subscription
                    return new ResponseEntity<>("successfully added team", HttpStatus.OK);
                } else {
                    //could not add team since team already exists in subscriptions
                    return new ResponseEntity<>("there was an error adding team to the database", HttpStatus.BAD_REQUEST);
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
    @PostMapping(path = "/users/removeTeamSubscriptions/{email}")
    public ResponseEntity<String> removeTeamSubscription(@PathVariable List<String> teamNames, @PathVariable String email) {
        try {
            if(userDataService.userExists(email)) {
                if(userDataService.removeTeamsSubscription(email, teamNames)) {
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
