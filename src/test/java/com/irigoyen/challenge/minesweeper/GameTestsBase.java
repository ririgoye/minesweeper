package com.irigoyen.challenge.minesweeper;

import com.irigoyen.challenge.minesweeper.model.Game;
import org.junit.Before;

import java.time.LocalDateTime;

public class GameTestsBase {
    Game mockGame;
    String newGameBody;
    String clickActionBody;
    String expectedGame;
    String clickActionResponse;
    String gameLayout;

    @Before
    public void setUpStreams() {
        gameLayout = "MM100221111111M1M111";

        mockGame = new Game();
        mockGame.setId(1l);
        mockGame.setUserId(1l);
        mockGame.setRows(5);
        mockGame.setColumns(5);
        mockGame.setMines(4);
        mockGame.setLayout(gameLayout);
        mockGame.setState("HHHHHHHHHHHHHHHHHHHH");
        mockGame.setStatus(Game.Status.STARTED);
        mockGame.setStartTime(LocalDateTime.now());
        newGameBody = "{\"userId\":1, \"rows\":4, \"columns\":5, \"mines\":4}";

        expectedGame = "{\"status\":\"OK\",\"message\":\"\",\"payload\":{\"id\":1,\"userId\":1,\"rows\":5,\"columns\":5,\"mines\":4,\"layout\":\""+gameLayout+"\",\"state\":\"HHHHHHHHHHHHHHHHHHHH\",\"status\":\"STARTED\",\"startTime\":\"2021-03-08T20:47:53.6749486\",\"elapsedTime\":null,\"size\":25},\"ok\":true}";
        clickActionBody ="{\"userId\":1,\"action\":\"CLICK\",\"cell\":4}";
        clickActionResponse = "{\"status\":\"OK\",\"message\":\"\",\"payload\":{\"id\":1,\"userId\":1,\"rows\":5,\"columns\":5,\"mines\":4,\"layout\":\""+gameLayout+"\",\"state\":\"HHHHHHHHHHHHHHHHHHHH\",\"status\":\"STARTED\",\"startTime\":\"2021-03-08T20:47:53.6749486\",\"elapsedTime\":null,\"size\":25},\"ok\":true}";
    }
}
