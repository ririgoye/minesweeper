package com.irigoyen.challenge.minesweeper.controller;

import com.irigoyen.challenge.minesweeper.infrastructure.DynamicBody;
import com.irigoyen.challenge.minesweeper.infrastructure.Response;
import com.irigoyen.challenge.minesweeper.model.Game;
import com.irigoyen.challenge.minesweeper.model.User;
import com.irigoyen.challenge.minesweeper.repository.GameRepository;
import com.irigoyen.challenge.minesweeper.repository.UserRepository;
import com.irigoyen.challenge.minesweeper.service.UserService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private UserService userService;

    /**
     * Gets user information by id
     * @param userId user to find
     * @return requested user
     */
    @GetMapping("{id}")
    public Response<User> getUserById(@PathVariable("id") Long userId) {
        //TODO: this should be secured with a token or authentication
        User user = userRepository.findById(userId).orElse(null);
        if (user == null)
            return new Response<User>().notFound("Unable to find user: " + userId);
        return new Response<>(user);
    }

    /**
     * Find latest active (started/paused) game for the user
     * Won and lost games are ignored
     * @param userId Owner of the game we want
     * @return Last user's game
     */
    @GetMapping("{id}/lastgame")
    public Response<Game> getLastGame(@PathVariable("id") Long userId) {
        List<Game.Status> statusFilter = new ArrayList<>();
        statusFilter.add(Game.Status.STARTED);
        statusFilter.add(Game.Status.PAUSED);
        List<Game> games = gameRepository.findByUserIdAndStatusInOrderByStartTimeDesc(userId, statusFilter);
        if (games == null || games.size() < 1)
            return new Response<Game>().setStatus(HttpStatus.NOT_FOUND, "User has no previous games");
        return new Response<>(games.get(0));
    }

    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/NewUserBody")))
    /**
     * Create/update user
     * @param user user details
     * @return new/updated user
     */
    @PostMapping()
    public Response<User> upsertUser(@RequestBody DynamicBody user) {
        List<String> missingFields = user.checkRequiredValues("name", "password");
        if (missingFields.size() > 0)
            return new Response<>(HttpStatus.BAD_REQUEST, "Following fields are required: " + missingFields.toString());
        return userService.upsert(user);
    }
}
