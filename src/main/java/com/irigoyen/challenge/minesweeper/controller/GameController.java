package com.irigoyen.challenge.minesweeper.controller;

import com.irigoyen.challenge.minesweeper.infrastructure.DynamicBody;
import com.irigoyen.challenge.minesweeper.infrastructure.Response;
import com.irigoyen.challenge.minesweeper.infrastructure.Utils;
import com.irigoyen.challenge.minesweeper.model.Board;
import com.irigoyen.challenge.minesweeper.repository.BoardRepository;
import com.irigoyen.challenge.minesweeper.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/games")
public class GameController {
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private GameService gameService;

    @GetMapping("{id}")
    public Response<Board> getBoard(@PathVariable("id") Long boardId,
                                        @RequestParam("userId") Long userId) {
        //TODO: change userId for an auth token.
        return getBoardById(userId, boardId);
    }

    //TODO: move this method to a UserService
    private Response<Board> getBoardById(Long userId, Long boardId) {
        Board board = boardRepository.findById(boardId).orElse(null);
        if (board != null && board.getUserId() != userId)
            return new Response<Board>().setStatus(HttpStatus.FORBIDDEN, "The user is not the board's owner");
        return new Response<>(board);
    }

    @PostMapping("")
    public Response<Board> createBoard(@RequestBody DynamicBody body) {
        List<String> missingFields = body.checkRequiredValues("rows", "columns", "mines", "userId");
        if (missingFields.size() > 0)
            return new Response<>(HttpStatus.BAD_REQUEST, "Following fields are required: " + missingFields.toString());
        //DynamicBody allows us to get api data without having to create DTO objects.
        //We can't use Board class as a DTO because fields (like mines) won't match
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
        Board board = gameService.createBoard(userId, rows, columns, mines);
        return new Response<>(board);
    }
    @PostMapping("{id}/action")
    public Response<Board> doAction(@PathVariable("id") Long boardId,
                                    @RequestBody DynamicBody body) {
        List<String> missingFields = body.checkRequiredValues("action", "userId");
        if (missingFields.size() > 0)
            return new Response<>(HttpStatus.BAD_REQUEST, "Following fields are required: " + missingFields.toString());
        String action = body.getString("action");
        int cell = body.getInt("cell",-1);
        Long userId = body.getLong("userId");
        Response<Board> response = getBoardById(userId, boardId);
        if (!response.isOK())
            return response;
        return gameService.performAction(response.getPayload(),  action, cell);
    }

}
