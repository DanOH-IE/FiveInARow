package com.dohalloran.code.challenge.fiveinarow.controller;

import com.dohalloran.code.challenge.fiveinarow.model.Colour;
import com.dohalloran.code.challenge.fiveinarow.model.GameRound;
import com.dohalloran.code.challenge.fiveinarow.model.GameStatus;
import com.dohalloran.code.challenge.fiveinarow.model.JoinRequest;
import com.dohalloran.code.challenge.fiveinarow.model.JoinResponse;
import com.dohalloran.code.challenge.fiveinarow.model.MoveRequest;
import com.dohalloran.code.challenge.fiveinarow.model.Player;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.dohalloran.code.challenge.fiveinarow.utils.TestConstants.GAME_ID;
import static com.dohalloran.code.challenge.fiveinarow.utils.TestConstants.PLAYER_ONE_ID;
import static com.dohalloran.code.challenge.fiveinarow.utils.TestConstants.PLAYER_ONE_NAME;
import static com.dohalloran.code.challenge.fiveinarow.utils.TestConstants.PLAYER_TWO_ID;
import static com.dohalloran.code.challenge.fiveinarow.utils.TestConstants.PLAYER_TWO_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class GamesManagerTest {

    private int numCols = 9;
    private int numRows = 6;
    private int winningNum = 5;

    private GamesManager gamesManager;

    @BeforeEach
    void setup() {
        gamesManager = new GamesManager();
        gamesManager.setNumColumns(numCols);
        gamesManager.setNumRows(numRows);
        gamesManager.setWinningNum(winningNum);
    }

    @Test
    void joinGame() {

        // Player One joins
        JoinRequest playerOneJoinRequest = new JoinRequest()
            .name(PLAYER_ONE_NAME);

        assertTrue(gamesManager.getGamesWaitingForPlayer().isEmpty());

        JoinResponse playerOneJoinResponse = gamesManager.joinGame(playerOneJoinRequest);

        assertEquals(Colour.RED, playerOneJoinResponse.getColour());
        assertEquals(PLAYER_ONE_NAME, playerOneJoinResponse.getDisplayName());

        String gameId = playerOneJoinResponse.getGameId();
        assertTrue(gamesManager.getGamesInProgress().containsKey(gameId));
        assertFalse(gamesManager.getGamesWaitingForPlayer().isEmpty());

        GameRound gameRound = gamesManager.getGamesWaitingForPlayer().peek();
        assertEquals(playerOneJoinResponse.getPlayerId(), gameRound.getPlayerOne().getPlayerId());
        assertNull(gameRound.getPlayerTwo());
        assertEquals(GameStatus.WAITING_FOR_PLAYER_TO_JOIN, gameRound.getGameStatus());
        assertFalse(gamesManager.getGamesWaitingForPlayer().isEmpty());

        // Player Two joins
        JoinRequest playerTwoJoinRequest = new JoinRequest()
            .name(PLAYER_TWO_NAME);

        assertFalse(gamesManager.getGamesWaitingForPlayer().isEmpty());

        JoinResponse playerTwoJoinResponse = gamesManager.joinGame(playerTwoJoinRequest);

        assertEquals(Colour.BLUE, playerTwoJoinResponse.getColour());
        assertEquals(PLAYER_TWO_NAME, playerTwoJoinResponse.getDisplayName());
        assertTrue(gamesManager.getGamesInProgress().containsKey(playerTwoJoinResponse.getGameId()));

        gameRound = gamesManager.getGamesInProgress().get(playerTwoJoinResponse.getGameId());
        assertEquals(playerTwoJoinResponse.getPlayerId(), gameRound.getPlayerTwo().getPlayerId());
        assertEquals(GameStatus.STARTED, gameRound.getGameStatus());
        assertTrue(gamesManager.getGamesWaitingForPlayer().isEmpty());

        // Player Three Joins - gets added to a new round
        String  playerThreeName = "Dan";
        JoinRequest playerThreeJoinRequest = new JoinRequest()
            .name(playerThreeName);

        assertTrue(gamesManager.getGamesWaitingForPlayer().isEmpty());

        JoinResponse playerThreeJoinResponse = gamesManager.joinGame(playerThreeJoinRequest);

        assertEquals(Colour.RED, playerThreeJoinResponse.getColour());
        assertEquals(playerThreeName, playerThreeJoinResponse.getDisplayName());

        assertNotEquals(gameId, playerThreeJoinResponse.getGameId());
        assertFalse(gamesManager.getGamesWaitingForPlayer().isEmpty());
    }

    @Test
    void moveNullMove() {
        assertThrows(IllegalArgumentException.class, () -> gamesManager.playerMove(null, null));
    }

    @Test
    void moveNullGameId() {
        MoveRequest moveRequest = new MoveRequest();
        assertThrows(IllegalArgumentException.class, () -> gamesManager.playerMove(null, moveRequest));
    }

    @Test
    void moveUnknownGameId() {

        MoveRequest moveRequest = new MoveRequest()
            .column(0)
            .playerId(PLAYER_ONE_ID);

        assertThrows(IllegalArgumentException.class, () -> gamesManager.playerMove(GAME_ID, moveRequest));
    }

    @Test
    void moveWrongPlayersMove() {

        MoveRequest moveRequest = new MoveRequest()
            .column(0)
            .playerId(PLAYER_ONE_ID);

        GameRound gameRound = mock(GameRound.class);
        Player currentPlayer = mock(Player.class);

        when(gameRound.getCurrentPlayer()).thenReturn(currentPlayer);
        when(currentPlayer.getPlayerId()).thenReturn(PLAYER_TWO_ID);

        gamesManager.getGamesInProgress().put(GAME_ID, gameRound);

        assertThrows(IllegalArgumentException.class, () -> gamesManager.playerMove(GAME_ID, moveRequest));
    }


    @Test
    void moveGameNotStarted() {

        MoveRequest moveRequest = new MoveRequest()
            .column(0)
            .playerId(PLAYER_ONE_ID);

        GameRound gameRound = mock(GameRound.class);
        Player currentPlayer = mock(Player.class);

        when(gameRound.getCurrentPlayer()).thenReturn(currentPlayer);
        when(currentPlayer.getPlayerId()).thenReturn(PLAYER_ONE_ID);

        gamesManager.getGamesInProgress().put(GAME_ID, gameRound);

        assertThrows(IllegalArgumentException.class, () -> gamesManager.playerMove(GAME_ID, moveRequest));
    }

    @Test
    void move() {

    }

}