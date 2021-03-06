package com.irigoyen.challenge.minesweeper.service;

import com.irigoyen.challenge.minesweeper.infrastructure.Utils;
import com.irigoyen.challenge.minesweeper.model.Board;
import com.irigoyen.challenge.minesweeper.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

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
        int size = rows * columns;

        //completely covered grid (Hidden cells)
        String status = new String(Utils.fillArray('h', size));
        board.setStatus(status);
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
         *  Cells around C are: [0,1],[0,2],[0,3],[1,1],-,[1,3],[2,1],[2,2],[2,3]
         *  Those correspond to 1,2,3,7,-,9,13,14,15 = currentCell-columns-1, currentCell-columns, currentCell-columns+1...
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
        int count = 0;
        int row = (currentCell / columns) + 1;
        int col = (currentCell % columns) + 1;
        for (int i = 0; i < deltas.length; i++) {
            if (row == 1 && i < 3) //top
                continue;
            if (col == columns && (i == 2 || i == 5 || i == 8)) //right
                continue;
            if (row == rows && i > 5) //bottom
                continue;
            if (col == 1 && (i == 0 || i == 3 || i == 6)) //left
                continue;
            int delta = deltas[i];
            if (delta != 0) {
                int cDelta = currentCell + delta;
                //ignore negative cDelta (it is outside the box)
                if (cDelta >= 0 && minePositions.contains(cDelta))
                    count++;
            }
        }
        return (char) (count + '0');
    }
}
