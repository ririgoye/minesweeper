package com.irigoyen.challenge.minesweeper;

import com.irigoyen.challenge.minesweeper.infrastructure.Response;
import com.irigoyen.challenge.minesweeper.model.Game;
import com.irigoyen.challenge.minesweeper.repository.GameRepository;
import com.irigoyen.challenge.minesweeper.service.GameService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class GameServiceTests extends GameTestsBase {
    @Mock
    private GameRepository gameRepository;
    @InjectMocks
    private GameService gameService;

    @Test
    public void validClick() throws Exception {
        //Just ignore save
        Mockito.when(
                gameRepository.save(Mockito.any())
        ).thenReturn(null);
        Response<Game> resultGame = gameService.performAction(mockGame, "CLICK", 5);
        Assert.assertTrue(resultGame.isOK());
        Game game = resultGame.getPayload();
        String clickGameState = "HHHHH HHHHHHHHHHHHHH";
        Assert.assertEquals(game.getLayout(),gameLayout);
        Assert.assertEquals(game.getState(),clickGameState);
    }

    @Test
    public void mineClicked() throws Exception {
        //Just ignore save
        Mockito.when(
                gameRepository.save(Mockito.any())
        ).thenReturn(null);
        //find a mind
        int mineIndex = mockGame.getLayout().indexOf("M",3);
        Response<Game> resultGame = gameService.performAction(mockGame, "CLICK", mineIndex);
        Assert.assertTrue(resultGame.isOK());
        Game game = resultGame.getPayload();
        String clickGameState = replaceChar("HHHHHHHHHHHHHHHHHHHH",mineIndex,'X');
        Assert.assertEquals(game.getLayout(),gameLayout);
        Assert.assertEquals(game.getState(),clickGameState);
        Assert.assertEquals(game.getStatus(),Game.Status.LOST);
    }

    @Test
    public void spaceClicked() {
        //Just ignore save
        Mockito.when(
                gameRepository.save(Mockito.any())
        ).thenReturn(null);
        //blank cell should clear cells around it
        int mineIndex = 3;
        Response<Game> resultGame = gameService.performAction(mockGame, "CLICK", mineIndex);
        Assert.assertTrue(resultGame.isOK());
        Game game = resultGame.getPayload();
        String clickGameState ="HH   HH   HHHHHHHHHH";
        Assert.assertEquals(game.getLayout(),gameLayout);
        Assert.assertEquals(game.getState(),clickGameState);
    }

    String replaceChar(String string, int index, char newChar){
        char[] myNameChars = string.toCharArray();
        myNameChars[index] = newChar;
        return String.valueOf(myNameChars);
    }
}
