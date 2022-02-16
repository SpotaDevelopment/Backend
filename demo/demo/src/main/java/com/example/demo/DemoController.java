package com.example.demo;

import com.google.gson.Gson;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
    private static final Gson gson = new Gson();

	@GetMapping("/hello/{name}")
	public String hello(@PathVariable String name) {
		return String.format("Hello %s!", name);
	}

    @PostMapping(value = "/users/{id}/friend",
                 consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public Friend createFriend(@RequestBody String friend) {
        Friend result = new Friend();

        try {
            Friend req = gson.fromJson(friend, Friend.class); 
            result.setFirstName(req.getFirstName());
            result.setLastName(req.getLastName());
            result.setId("42");
        } catch (Exception e) {}



        return result;
    }
    
}
