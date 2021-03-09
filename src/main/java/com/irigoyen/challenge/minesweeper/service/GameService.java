package com.irigoyen.challenge.minesweeper.service;

import com.irigoyen.challenge.minesweeper.infrastructure.Response;
import com.irigoyen.challenge.minesweeper.infrastructure.Utils;
import com.irigoyen.challenge.minesweeper.model.Game;
import com.irigoyen.challenge.minesweeper.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Game management service
 * Prodives methods to do all the game operations required
 */
@Service
public class GameService {
    @Autowired
    GameRepository gameRepository;

    /**
     * Create a game using provided parameters.
     * It will create a rows X columns "grid" that would be stored as a flat string
     * There is no limit to how many rows/columns it could have but there should be obviously withing a reasonable size.
     * @param userId owner of the game
     * @param rows how many rows to create
     * @param columns how many columns to create
     * @param mines amount of mines to include
     * @return new game
     */
    public Game createGame(Long userId, int rows, int columns, int mines) {
        Game game = new Game();
        game.setUserId(userId);
        game.setRows(rows);
        game.setColumns(columns);
        game.setMines(mines);
        game.resetGame();

        int size = game.getSize();
        //game with no mines
        char[] layout = Utils.fillArray('0', size);

        //using a set instead of a list so duplicated values are automatically ignored
        Set<Integer> minePositions = new HashSet<>();
        Random random = new Random();
        while (minePositions.size() < mines) {
            int position = random.nextInt(size);
            minePositions.add(position);
        }

        int[] cellDeltas = getDeltas(columns);
        for (int i = 0; i < size; i++) {
            layout[i] = getCellValue(i, minePositions, rows, columns, cellDeltas);
        }
        game.setLayout(new String(layout));
        gameRepository.save(game);
        return game;
    }

    //TODO: This could be a cached value

    /**
     * generate cell deltas. Used to figure out what cells are around selected cell
     * @param columns how many columns are there on the grid
     * @return cell deltas (upper left, upper, upper right, left, right ... etc)
     */
    private int[] getDeltas(int columns) {
        /**
         * Logic explained
         * Considering this layout
         *
         *      012345
         *    0 --*---
         *    1 --C-*-
         *    2 -*----
         *
         *  columns = 6
         *  currentCell = 8
         *  row = (int)currentCell/columns = 1
         *  col = currentCell % columns =2
         *  meaning a column position = row*columns + col
         *  Cells around C are: [0,1],[0,2],[0,3],[1,1],[1,3],[2,1],[2,2],[2,3]
         *  Those correspond to 1,2,3,7,9,13,14,15 = currentCell-columns-1, currentCell-columns, currentCell-columns+1...
         **/
        int[] deltas = {
                -columns - 1, -columns, -columns + 1,
                -1, 1,
                columns - 1, columns, columns + 1
        };
        return deltas;
    }

    /**
     * Calculate the amount of mines surrounding current cell
     * @param currentCell cell to calculate
     * @param minePositions list of mines used as a reference
     * @param rows game rows
     * @param columns game columns
     * @param deltas list of cell deltas
     * @return character (mine or number) to put on the selected cell
     */
    private static char getCellValue(int currentCell, Set<Integer> minePositions, int rows, int columns, int[] deltas) {
        if (minePositions.contains(currentCell))
            return 'M';
        AtomicInteger count = new AtomicInteger();
        GameService.cycleDeltas(currentCell, rows, columns, deltas, (deltaCell) -> {
            if (minePositions.contains(deltaCell))
                count.getAndIncrement();
        });
        return (char) (count.get() + '0'); //Converts number to it's char value
    }

    /**
     * Helper method to perform an action on all the cells surrounding selected cell.
     * @param currentCell cell to analise
     * @param rows game rows
     * @param columns game columns
     * @param deltas list of cell deltas
     * @param operation operation to perform on each of the surrounding cells
     */
    private static void cycleDeltas(int currentCell, int rows, int columns, int[] deltas, Consumer<Integer> operation) {
        int row = (currentCell / columns) + 1;
        int col = (currentCell % columns) + 1;
        for (int i = 0; i < deltas.length; i++) {
            //Check if cell is outside game bounds
            if (row == 1 && i < 3) //top
                continue;
            if (col == columns && (i == 2 || i == 4 || i == 7)) //right
                continue;
            if (row == rows && i > 4) //bottom
                continue;
            if (col == 1 && (i == 0 || i == 3 || i == 5)) //left
                continue;
            int delta = deltas[i];
            int cDelta = currentCell + delta;
            operation.accept(cDelta);
        }
    }

    /**
     * Perform an action (flag, click, etc) on a game
     * @param game game to modify
     * @param action action to perform
     * @param cell cel to click or flag (not use for pause, resume...)
     * @return Modified game
     */
    public Response<Game> performAction(Game game, String action, int cell) {
        if (game.getStatus() == Game.Status.STARTED) {
            //As long as the game is running, We need to always return the correct elapsed time.
            // No calculations should be done on the client
            game.calculateElapsedTime();
        }
        Response<Game> response = new Response<>(game);
        switch (action) {
            case "PAUSE":
                //Only pause started games
                if (game.getStatus() == Game.Status.STARTED) {
                    game.setStatus(Game.Status.PAUSED);
                    gameRepository.save(game);
                }
                return response;
            case "RESUME":
                //Only resume paused games
                if (game.getStatus() == Game.Status.PAUSED) {
                    game.setStatus(Game.Status.STARTED);
                    //Start time is never displayed, we just used to calculate the time difference.
                    // subtracting current elapsed time will allow us to continue with the timer.
                    LocalDateTime newStartTime = LocalDateTime.now().minusSeconds(game.getElapsedTime());
                    game.setStartTime(newStartTime);
                    gameRepository.save(game);
                }
                return response;
            case "RESET":
                game.resetGame();
                gameRepository.save(game);
                return response;
        }

        //We can only perform click/flag actions on running games
        if (game.getStatus() != Game.Status.STARTED)
            return response;

        if (cell < 0 || cell > game.getSize())
            return response.setStatus(HttpStatus.BAD_REQUEST, "Invalid cell number, it should be between 0 and " + game.getSize());

        action = action.toUpperCase();
        char[] state = game.getState().toCharArray();
        char[] layout = game.getLayout().toCharArray();
        char cellState = state[cell];
        switch (action) {
            case "CLICK":
                if (cellState == ' ')
                    //if cell is already clear ignore
                    break;
                //Clicked a mine
                if (layout[cell] == 'M') {
                    state[cell] = 'X'; //BOOM
                    game.setStatus(Game.Status.LOST);
                } else //clicked a number or blank
                {
                    int[] deltas = getDeltas(game.getColumns());
                    clearEmptyCells(cell, game.getRows(), game.getColumns(), layout, state, deltas);
                }
                break;
            case "FLAG":
                if (cellState == ' ')
                    //We don't want to flag clear cells
                    break;
                state[cell] = 'F'; //FLAG
                break;
            default:
                return response.setStatus(HttpStatus.BAD_REQUEST, "Invalid action (" + action + "). Valid actions are CLICK, FLAG, PAUSE, RESUME");
        }
        String newState = new String(state);
        if (!newState.equals(game.getState())) {
            //Only save if game changed
            game.setState(newState);
            checkGameStatus(game);
            gameRepository.save(game);
        }
        return response;
    }

    /**
     * Check if the game is over (won or lost)
     * @param game
     */
    private void checkGameStatus(Game game) {
        String state = game.getState();
        if (state.contains("X")) {
            game.setStatus(Game.Status.LOST);
            game.calculateElapsedTime();
            return;
        }
        //if there are no more hidden cells
        if (!state.contains("H")) {
            char[] chars = state.toCharArray();
            //count flags
            int flagsCount = 0;
            for (char c : chars) {
                if (c == 'F')
                    flagsCount++;
            }
            if (flagsCount == game.getMines()) {
                game.setStatus(Game.Status.WON);
                //We need to calculate the final elapsed time before returning
                game.calculateElapsedTime();
            }
        }
    }

    /**
     * When an empty cell is clicked we need to uncover all the empty cells surrounding it
     * @param cell cell to check
     * @param rows game rows
     * @param columns game columns
     * @param layout game layout
     * @param state game state (cells) to change
     * @param deltas list of cell deltas
     */
    private static void clearEmptyCells(int cell, int rows, int columns, char[] layout, char[] state, int[] deltas) {
        Set<Integer> toClear = new HashSet<>();
        clearEmptyCells(cell, rows, columns, layout, toClear, deltas);
        for (Integer i : toClear) {
            state[i] = ' ';
        }
    }

    private static void clearEmptyCells(int cell, int rows, int columns, char[] layout, Set<Integer> toClear, int[] deltas) {
        char c = layout[cell];
        if (c == 'M' || toClear.contains(cell))
            return;
        toClear.add(cell);
        //only clear surrounding cells if empty
        if (c == '0') {
            GameService.cycleDeltas(cell, rows, columns, deltas, (deltaCell) -> {
                clearEmptyCells(deltaCell, rows, columns, layout, toClear, deltas);
            });
        }
    }
}
