package asu.capstone.spota;

import asu.capstone.spota.model.UserAccount;
import asu.capstone.spota.services.UserDataService;
import com.google.gson.Gson;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpotaController {

    @Autowired
    private UserDataService userDataService;

    private static final Gson gson = new Gson();

	@GetMapping(value = "/hello/{name}")
	public String hello(@PathVariable String name) {

        return String.format("Hello %s!", name);
	}

    @PostMapping(path = "/users/userAccount",
                 consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserAccount> createUserAccount(@RequestBody String userAccount) {
        UserAccount newUser = null;

        try {
            UserAccount req = gson.fromJson(userAccount, UserAccount.class);
            newUser = new UserAccount(req.getFirstName(), req.getLastName(), req.getUsername(), req.getEmail());

        } catch (Exception e) {
            e.printStackTrace();
        }
        userDataService.addNewUserAccount(newUser);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }
}
