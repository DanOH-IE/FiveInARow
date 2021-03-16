package com.dohalloran.code.challenge.fiveinarow;

import com.dohalloran.code.challenge.fiveinarow.controller.FiveInARowController;
import com.dohalloran.code.challenge.fiveinarow.model.Colour;
import com.dohalloran.code.challenge.fiveinarow.model.GameStatus;
import com.dohalloran.code.challenge.fiveinarow.model.GameUpdate;
import com.dohalloran.code.challenge.fiveinarow.model.JoinRequest;
import com.dohalloran.code.challenge.fiveinarow.model.JoinResponse;
import com.dohalloran.code.challenge.fiveinarow.model.MoveRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import static com.dohalloran.code.challenge.fiveinarow.utils.TestConstants.PLAYER_ONE_NAME;
import static com.dohalloran.code.challenge.fiveinarow.utils.TestConstants.PLAYER_TWO_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(
    properties = "game.deletionTimeoutSeconds=2"
)
class FiveInARow_IT {

    @Autowired
    private FiveInARowController fiveInARowController;

    @Test
    void test() throws InterruptedException {

        // Player One joins game
        JoinRequest playerOneJoinRequest = new JoinRequest()
            .name(PLAYER_ONE_NAME);

        ResponseEntity<JoinResponse> responseEntity = fiveInARowController.joinGame(playerOneJoinRequest);
        JoinResponse joinResponse = responseEntity.getBody();
        assertNotNull(joinResponse);
        String gameId = joinResponse.getGameId();
        String playerOneId = joinResponse.getPlayerId();
        assertEquals(PLAYER_ONE_NAME, joinResponse.getDisplayName());
        assertEquals(Colour.RED, joinResponse.getColour());

        GameUpdate gameUpdate = getGameUpdate(gameId);
        assertEquals(GameStatus.WAITING_FOR_PLAYER_TO_JOIN, gameUpdate.getGameStatus());


        // Player Two Joins
        JoinRequest playerTwoJoinRequest = new JoinRequest()
            .name(PLAYER_TWO_NAME);

        responseEntity = fiveInARowController.joinGame(playerTwoJoinRequest);
        JoinResponse playerTwoJoinResponse = responseEntity.getBody();
        assertNotNull(playerTwoJoinResponse);
        assertEquals(gameId, playerTwoJoinResponse.getGameId());
        String playerTwoId = playerTwoJoinResponse.getPlayerId();
        assertEquals(PLAYER_TWO_NAME, playerTwoJoinResponse.getDisplayName());
        assertEquals(Colour.BLUE, playerTwoJoinResponse.getColour());

        gameUpdate = getGameUpdate(gameId);
        assertEquals(GameStatus.STARTED, gameUpdate.getGameStatus());
        assertEquals(playerOneId, gameUpdate.getCurrentPlayerId());
        assertNull(gameUpdate.getWinningPlayerId());

        // Player Two moves
        assertThrows(IllegalArgumentException.class,
            () -> move(gameId, playerTwoId, 0));

        // Player One moves
        move(gameId, playerOneId, 0);
        validateMove(gameId, Colour.RED, 0, 0, false);
        move(gameId, playerTwoId, 1);
        validateMove(gameId, Colour.BLUE, 0, 1, false);
        move(gameId, playerOneId, 0);
        validateMove(gameId, Colour.RED, 1, 0, false);
        move(gameId, playerTwoId, 1);
        validateMove(gameId, Colour.BLUE, 1, 1, false);
        move(gameId, playerOneId, 0);
        validateMove(gameId, Colour.RED, 2, 0, false);
        move(gameId, playerTwoId, 1);
        validateMove(gameId, Colour.BLUE, 2, 1, false);
        move(gameId, playerOneId, 0);
        validateMove(gameId, Colour.RED, 3, 0, false);
        move(gameId, playerTwoId, 1);
        validateMove(gameId, Colour.BLUE, 2, 1, false);
        move(gameId, playerOneId, 0);
        validateMove(gameId, Colour.RED, 3, 0, true);

        gameUpdate = getGameUpdate(gameId);
        assertEquals(playerOneId, gameUpdate.getWinningPlayerId());

        Thread.sleep(2500L);
        assertThrows(IllegalArgumentException.class,
            () -> getGameUpdate(gameId));

    }

    private void move(String gameId, String playerId, int column) {
        MoveRequest moveRequest = new MoveRequest()
            .playerId(playerId)
            .column(column);

        fiveInARowController.move(gameId, moveRequest);
    }

    private void validateMove(String gameId, Colour colour, int row, int col, boolean isWinningMove) {

        // Get game update
        GameUpdate gameUpdate = getGameUpdate(gameId);
        Colour[][] board = gameUpdate.getGameboard().stream()
            .map(boardRow -> boardRow.toArray(Colour[]::new))
            .toArray(Colour[][]::new);

        assertEquals(colour, board[row][col]);
        if (isWinningMove) {
            assertNotNull(gameUpdate.getWinningPlayerId());
        } else {
            assertNull(gameUpdate.getWinningPlayerId());
        }
    }

    private GameUpdate getGameUpdate(String gameId) {
        ResponseEntity<GameUpdate> gameUpdateResponseEntity = fiveInARowController.update(gameId);
        GameUpdate gameUpdate = gameUpdateResponseEntity.getBody();
        assertNotNull(gameUpdate);
        assertEquals(gameId, gameUpdate.getGameId());
        return gameUpdate;
    }

}
