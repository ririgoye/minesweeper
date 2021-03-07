package com.irigoyen.challenge.minesweeper.service;

import com.irigoyen.challenge.minesweeper.infrastructure.Response;
import com.irigoyen.challenge.minesweeper.infrastructure.Utils;
import com.irigoyen.challenge.minesweeper.model.Board;
import com.irigoyen.challenge.minesweeper.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Service
public class GameService {
    @Autowired
    BoardRepository boardRepository;

    public Board createBoard(Long userId, int rows, int columns, int mines) {
        Board board = new Board();
        board.setUserId(userId);
        board.setRows(rows);
        board.setColumns(columns);
        board.setStartTime(LocalDateTime.now());
        board.setStatus(Board.Status.STARTED);

        int size = board.getSize();

        //completely covered grid (Hidden cells)
        String state = new String(Utils.fillArray('H', size));
        board.setState(state);
        //board with no mines
        char[] layout = Utils.fillArray('0', size);

        //using a set instead of a list so duplicated values are automatically ignored
        Set<Integer> minePositions = new HashSet<>();
        Random random = new Random();
        while (minePositions.size() < mines) {
            int position = random.nextInt(size);
            minePositions.add(position);
        }

        /*
            //Test data
            //01234567890123456789
            //MM100221111111M1M111
            minePositions.add(0);
            minePositions.add(1);
            minePositions.add(14);
            minePositions.add(16);
        */

        int[] cellDeltas = getDeltas(columns);
        for (int i = 0; i < size; i++) {
            layout[i] = getCellValue(i, minePositions, rows, columns, cellDeltas);
        }
        board.setLayout(new String(layout));
        boardRepository.save(board);
        return board;
    }

    //TODO: This could be a cached value
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

    private static void cycleDeltas(int currentCell, int rows, int columns, int[] deltas, Consumer<Integer> operation) {
        int row = (currentCell / columns) + 1;
        int col = (currentCell % columns) + 1;
        for (int i = 0; i < deltas.length; i++) {
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


    public Response<Board> performAction(Board board, String action, int cell) {
        Response<Board> response = new Response<>(board);
        switch (action) {
            case "PAUSE":
                return response;
            case "RESUME":
                return response;
            case "RESET":
                return response;
        }

        if (cell < 0 || cell > board.getSize())
            return response.setStatus(HttpStatus.BAD_REQUEST, "Invalid cell number, it should be between 0 and " + board.getSize());

        action = action.toUpperCase();
        char[] state = board.getState().toCharArray();
        char[] layout = board.getLayout().toCharArray();
        char cellState = state[cell];
        switch (action) {
            case "CLICK":
                if (cellState == ' ')
                    //if cell is already clear ignore
                    break;
                if (layout[cell] == 'M') //Clicked a mine
                    state[cell] = 'X'; //BOOM
                else //clicked a number or blank
                {
                    int[] deltas = getDeltas(board.getColumns());
                    clearEmptyCells(cell, board.getRows(), board.getColumns(), layout, state, deltas);
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
        if(!newState.equals(board.getState())) {
            board.setState(newState);
            boardRepository.save(board);
        }
        return response;
    }

    private static void clearEmptyCells(int cell, int rows, int columns, char[] layout, char[] state, int[] deltas) {
        Set<Integer> toClear = new HashSet<>();
        clearEmptyCells(cell, rows, columns, layout, toClear, deltas);

        for (Integer i : toClear) {
            state[i]=' ';
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
