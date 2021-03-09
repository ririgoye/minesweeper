package com.irigoyen.challenge.minesweeper.controller;

import com.irigoyen.challenge.minesweeper.infrastructure.DynamicBody;
import com.irigoyen.challenge.minesweeper.infrastructure.Response;
import com.irigoyen.challenge.minesweeper.infrastructure.Utils;
import com.irigoyen.challenge.minesweeper.model.Game;
import com.irigoyen.challenge.minesweeper.repository.GameRepository;
import com.irigoyen.challenge.minesweeper.service.GameService;
import com.irigoyen.challenge.minesweeper.service.UserService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API for the games information
 * Controls creation, actions (CLICK, FLAG, etc), and game search
 */
@RestController
@RequestMapping("/api/v1/games")
public class GameController {
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private GameService gameService;
    @Autowired
    private UserService userService;

    /**
     * Get a game using it's id.
     * User id needs to be provided for security reasons (game/user validation)
     * @param gameId game to get
     * @param userId game owner
     * @return requested game or null if not found
     */
    @GetMapping("{id}")
    public Response<Game> getGame(@PathVariable("id") Long gameId,
                                        @RequestParam("userId") Long userId) {
        //TODO: change userId for an auth token.
        return getGameById(userId, gameId);
    }

    //Configures swagger Example value
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/NewGameBody")))
    /**
     * Creates a new game using provided parameters.
     * Example body:
     * {
     *     "userId":1,
     *     "rows":4,
     *     "columns":5,
     *     "mines":4
     * }
     * @param body parameters to create the game
     * @return new game
     */
    @PostMapping("")
    public Response<Game> createGame(@RequestBody DynamicBody body) {
        List<String> missingFields = body.checkRequiredValues("rows", "columns", "mines", "userId");
        if (missingFields.size() > 0)
            return new Response<>(HttpStatus.BAD_REQUEST, "Following fields are required: " + missingFields.toString());
        //DynamicBody allows us to get api data without having to create DTO objects.
        //We can't use Game class as a DTO because fields (like mines) won't match
        int rows = body.getInt("rows");
        int columns = body.getInt("columns");
        int mines = body.getInt("mines");

        double difficulty = (mines * 1.0) / (rows * columns);
        //TODO: Should this be a warning and just use the right/maximum difficulty?
        if (difficulty > .25) {
            int maxMines = Utils.toInt(rows * columns * 0.25);
            return new Response<>(HttpStatus.BAD_REQUEST, "Mines coverage cannot be greater than 25% (" + maxMines + "). ");
        }

        //TODO: this should be a token
        Long userId = body.getLong("userId");
        //TODO: check for user existence
        Game game = gameService.createGame(userId, rows, columns, mines);
        return new Response<>(game);
    }

    /**
     * Performs an action on specified game.
     * Actions can be:PAUSE,RESUME,RESET,CLICK,FLAG
     * Example body:
     * {
     *     "userId":1,
     *     "action":"CLICK",
     *     "cell":4
     * }
     * @param gameId game where action is applied
     * @param body parameters to apply
     * @return modified game
     */
    @PostMapping("{id}/action")
    public Response<Game> doAction(@PathVariable("id") Long gameId,
                                    @RequestBody DynamicBody body) {
        List<String> missingFields = body.checkRequiredValues("action", "userId");
        if (missingFields.size() > 0)
            return new Response<>(HttpStatus.BAD_REQUEST, "Following fields are required: " + missingFields.toString());
        String action = body.getString("action");
        int cell = body.getInt("cell",-1);
        Long userId = body.getLong("userId");
        Response<Game> response = getGameById(userId, gameId);
        if (!response.isOK())
            return response;
        return gameService.performAction(response.getPayload(),  action, cell);
    }

    /**
     * Helper method to get user game by
     * @param userId game owner
     * @param gameId game to get
     * @return requested game
     */
    private Response<Game> getGameById(Long userId, Long gameId) {
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game != null && game.getUserId() != userId)
            return new Response<Game>().setStatus(HttpStatus.FORBIDDEN, "The user is not the game's owner");
        return new Response<>(game);
    }

}
