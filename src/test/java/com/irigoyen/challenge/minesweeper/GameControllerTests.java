package com.irigoyen.challenge.minesweeper;

import com.irigoyen.challenge.minesweeper.controller.GameController;
import com.irigoyen.challenge.minesweeper.infrastructure.Response;
import com.irigoyen.challenge.minesweeper.repository.GameRepository;
import com.irigoyen.challenge.minesweeper.service.GameService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

@RunWith(SpringRunner.class)
@WebMvcTest(value = GameController.class)
public class GameControllerTests  extends GameTestsBase {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private GameService gameService;
    @MockBean
    private GameRepository gameRepository;

    private void checkRequest(RequestBuilder requestBuilder, String expectedValue) throws Exception {
        MvcResult result = mockMvc
                .perform(requestBuilder)
                .andReturn();
        MockHttpServletResponse response = result.getResponse();
        System.out.println(
                response.getContentAsString());
        //compare with test data ignoring date.
        JSONAssert
                .assertEquals(
                        expectedGame,
                        response.getContentAsString(),
                        new CustomComparator(JSONCompareMode.LENIENT,
                                new Customization("payload.startTime", (o1, o2) -> true)
                        ));
    }


    @Test
    public void createNewGame() throws Exception {
        Mockito.when(
                gameService.createGame(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt())
        ).thenReturn(mockGame);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/v1/games")
                .content(newGameBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        checkRequest(requestBuilder, expectedGame);
    }

    @Test
    public void getExistingGame() throws Exception {
        Mockito.when(
                gameRepository.findById(Mockito.anyLong())
        ).thenReturn(Optional.ofNullable(mockGame));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/v1/games/1?userId=1")
                .accept(MediaType.APPLICATION_JSON);

        checkRequest(requestBuilder, expectedGame);
    }

    @Test
    public void perFormAction() throws Exception {
        Mockito.when(
                gameService.performAction(Mockito.any(),Mockito.anyString(),Mockito.anyInt())
        ).thenReturn(new Response<>(mockGame));
        Mockito.when(
                gameRepository.findById(Mockito.anyLong())
        ).thenReturn(Optional.ofNullable(mockGame));


        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/v1/games/1/action")
                .content(clickActionBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        checkRequest(requestBuilder, clickActionResponse);
    }
}
