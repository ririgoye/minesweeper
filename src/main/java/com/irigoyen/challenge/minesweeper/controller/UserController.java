package com.irigoyen.challenge.minesweeper.controller;

import com.irigoyen.challenge.minesweeper.infrastructure.Response;
import com.irigoyen.challenge.minesweeper.model.User;
import com.irigoyen.challenge.minesweeper.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("{id}")
    public Response<User> getUserById(@PathVariable("id") Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null)
            return new Response<User>().notFound("Unable to find user: " + userId);
        return new Response<>(user);
    }

    @PostMapping()
    public Response<User> upsertUser(@RequestBody User user) {
        if (user == null)
            return new Response<>(HttpStatus.BAD_REQUEST, "Invalid user data");
        Long userId = user.getId();
        if (userId != null) {
            //if userId already exists we will update the user details.
            User dbUser = userRepository.findById(userId).orElse(null);
            if (dbUser == null)
                return new Response<>(HttpStatus.BAD_REQUEST, "User id doesn't exist. User can't be updated");
            dbUser.setName(user.getName());
            dbUser.setImage(user.getImage());
            //TODO: update other fields when added
            user = dbUser;
        } else {
            //TODO: move this to another endpoint to check for existence before submitting (as user types)
            List<User> dbUsers = userRepository.findByName(user.getName());
            if(dbUsers.size()>0)
                return new Response<>(HttpStatus.BAD_REQUEST, "User name already exists");
        }
        user = userRepository.save(user);
        return new Response<>(user);
    }
}
