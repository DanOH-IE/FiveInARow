package com.dohalloran.code.challenge.fiveinarow.model;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.dohalloran.code.challenge.fiveinarow.utils.TestConstants.GAME_ID;
import static com.dohalloran.code.challenge.fiveinarow.utils.TestConstants.PLAYER_ONE_ID;
import static com.dohalloran.code.challenge.fiveinarow.utils.TestConstants.PLAYER_ONE_NAME;
import static com.dohalloran.code.challenge.fiveinarow.utils.TestConstants.PLAYER_TWO_ID;
import static com.dohalloran.code.challenge.fiveinarow.utils.TestConstants.PLAYER_TWO_NAME;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class GameBoardModelTest {

    private final int numColumns = 9;
    private final int numRows = 6;
    private final int winningNum = 5;

    private GameBoardModel gameBoardModel;
    private Player playerOne;
    private Player playerTwo;

    @BeforeEach
    void setup() {
        gameBoardModel = new GameBoardModel(numRows, numColumns, winningNum);
        playerOne = new Player(PLAYER_ONE_NAME, PLAYER_ONE_ID, GAME_ID, Colour.RED);
        playerTwo = new Player(PLAYER_TWO_NAME, PLAYER_TWO_ID, GAME_ID, Colour.BLUE);
    }

    @Test
    void testOutOfBoundsMove() {

        assertThrows(IllegalArgumentException.class,
            () -> gameBoardModel.move(createMoveRequest(-1), playerOne));


        assertThrows(IllegalArgumentException.class,
            () -> gameBoardModel.move(createMoveRequest(10), playerOne));

    }

    @Test
    void columnFull() {
        String testName = "columnFull";
        assertFalse(gameBoardModel.move(createMoveRequest(0), playerOne));
        assertFalse(gameBoardModel.move(createMoveRequest(0), playerTwo));
        assertFalse(gameBoardModel.move(createMoveRequest(0), playerOne));
        assertFalse(gameBoardModel.move(createMoveRequest(0), playerTwo));
        assertFalse(gameBoardModel.move(createMoveRequest(0), playerOne));
        assertFalse(gameBoardModel.move(createMoveRequest(0), playerTwo));
        assertThrows(IllegalArgumentException.class,
            () -> gameBoardModel.move(createMoveRequest(0), playerOne));
        LOG.info("{}, {}", testName, gameBoardModel.toString());
    }


    /*
        |[GREY][GREY][GREY][GREY][GREY][GREY][GREY][GREY][GREY]|
        |[GREY][GREY][GREY][GREY][GREY][GREY][GREY][GREY][GREY]|
        |[GREY][GREY][GREY][GREY][GREY][GREY][GREY][GREY][GREY]|
        |[GREY][GREY][GREY][GREY][GREY][GREY][GREY][GREY][GREY]|
        |[GREY][GREY][GREY][GREY][GREY][GREY][GREY][GREY][GREY]|
        |[RED ][RED ][RED ][RED ][RED ][GREY][GREY][GREY][GREY]|
     */
    @Test
    void testFiveHorizontal() {
        String testName = "testFiveHorizontal";

        assertFalse(gameBoardModel.move(createMoveRequest(0), playerOne));
        assertFalse(gameBoardModel.move(createMoveRequest(1), playerOne));
        assertFalse(gameBoardModel.move(createMoveRequest(2), playerOne));
        assertFalse(gameBoardModel.move(createMoveRequest(3), playerOne));
        assertTrue(gameBoardModel.move(createMoveRequest(4), playerOne));
        LOG.info("{}, {}", testName, gameBoardModel.toString());
    }


    /*
        |[GREY][GREY][GREY][GREY][GREY][GREY][GREY][GREY][GREY]|
        |[GREY][GREY][GREY][GREY][GREY][GREY][GREY][GREY][GREY]|
        |[GREY][GREY][GREY][GREY][GREY][GREY][GREY][GREY][GREY]|
        |[GREY][GREY][GREY][GREY][GREY][GREY][GREY][GREY][GREY]|
        |[GREY][GREY][GREY][GREY][GREY][GREY][GREY][GREY][GREY]|
        |[GREY][GREY][GREY][GREY][RED ][RED ][RED ][RED ][RED ]|
     */
    @Test
    void testFiveHorizontal_midBoard() {
        String testName = "testFiveHorizontal_midBoard";

        assertFalse(gameBoardModel.move(createMoveRequest(6), playerOne));
        assertFalse(gameBoardModel.move(createMoveRequest(7), playerOne));
        assertFalse(gameBoardModel.move(createMoveRequest(8), playerOne));
        assertFalse(gameBoardModel.move(createMoveRequest(4), playerOne));
        assertTrue(gameBoardModel.move(createMoveRequest(5), playerOne));
        LOG.info("{}, {}", testName, gameBoardModel.toString());
    }


    /*
        |[GREY][GREY][GREY][GREY][GREY][GREY][GREY][GREY][GREY]|
        |[RED ][GREY][GREY][GREY][GREY][GREY][GREY][GREY][GREY]|
        |[RED ][GREY][GREY][GREY][GREY][GREY][GREY][GREY][GREY]|
        |[RED ][GREY][GREY][GREY][GREY][GREY][GREY][GREY][GREY]|
        |[RED ][GREY][GREY][GREY][GREY][GREY][GREY][GREY][GREY]|
        |[RED ][GREY][GREY][GREY][GREY][GREY][GREY][GREY][GREY]|
     */
    @Test
    void testFiveVertical() {
        String testName = "testFiveVertical";

        assertFalse(gameBoardModel.move(createMoveRequest(0), playerOne));
        assertFalse(gameBoardModel.move(createMoveRequest(0), playerOne));
        assertFalse(gameBoardModel.move(createMoveRequest(0), playerOne));
        assertFalse(gameBoardModel.move(createMoveRequest(0), playerOne));
        assertTrue(gameBoardModel.move(createMoveRequest(0), playerOne));
        LOG.info("{}, {}", testName, gameBoardModel.toString());
    }

    /*
        |[GREY][GREY][GREY][GREY][GREY][GREY][GREY][GREY][GREY]|
        |[GREY][GREY][GREY][GREY][RED ][GREY][GREY][GREY][GREY]|
        |[GREY][GREY][BLUE][RED ][BLUE][GREY][GREY][GREY][GREY]|
        |[GREY][GREY][RED ][BLUE][RED ][GREY][GREY][GREY][GREY]|
        |[BLUE][RED ][BLUE][RED ][BLUE][GREY][GREY][GREY][GREY]|
        |[RED ][BLUE][RED ][BLUE][RED ][GREY][GREY][GREY][GREY]|
     */
    @Test
    void testDiagonalRightUp() {
        String testName = "testFiveVertical";

        assertFalse(gameBoardModel.move(createMoveRequest(0), playerOne));
        assertFalse(gameBoardModel.move(createMoveRequest(1), playerTwo));
        assertFalse(gameBoardModel.move(createMoveRequest(1), playerOne));
        assertFalse(gameBoardModel.move(createMoveRequest(0), playerTwo));
        assertFalse(gameBoardModel.move(createMoveRequest(2), playerOne));
        assertFalse(gameBoardModel.move(createMoveRequest(2), playerTwo));
        assertFalse(gameBoardModel.move(createMoveRequest(2), playerOne));
        assertFalse(gameBoardModel.move(createMoveRequest(3), playerTwo));
        assertFalse(gameBoardModel.move(createMoveRequest(3), playerOne));
        assertFalse(gameBoardModel.move(createMoveRequest(3), playerTwo));
        assertFalse(gameBoardModel.move(createMoveRequest(3), playerOne));
        assertFalse(gameBoardModel.move(createMoveRequest(2), playerTwo));
        assertFalse(gameBoardModel.move(createMoveRequest(4), playerOne));
        assertFalse(gameBoardModel.move(createMoveRequest(4), playerTwo));
        assertFalse(gameBoardModel.move(createMoveRequest(4), playerOne));
        assertFalse(gameBoardModel.move(createMoveRequest(4), playerTwo));
        assertTrue(gameBoardModel.move(createMoveRequest(4), playerOne));
        LOG.info("{}, {}", testName, gameBoardModel.toString());
    }

    /*
        |[GREY][GREY][GREY][GREY][GREY][GREY][GREY][GREY][GREY]|
        |[GREY][GREY][GREY][GREY][RED ][GREY][GREY][GREY][GREY]|
        |[GREY][GREY][GREY][GREY][BLUE][RED ][BLUE][GREY][GREY]|
        |[GREY][GREY][GREY][GREY][RED ][BLUE][RED ][GREY][GREY]|
        |[GREY][GREY][GREY][GREY][BLUE][RED ][BLUE][RED ][BLUE]|
        |[GREY][GREY][GREY][GREY][RED ][BLUE][RED ][BLUE][RED ]|
     */
    @Test
    void testDiagonalLeftUp() {
        String testName = "testDiagonalLeftUp";

        assertFalse(gameBoardModel.move(createMoveRequest(8), playerOne));
        assertFalse(gameBoardModel.move(createMoveRequest(7), playerTwo));
        assertFalse(gameBoardModel.move(createMoveRequest(7), playerOne));
        assertFalse(gameBoardModel.move(createMoveRequest(8), playerTwo));
        assertFalse(gameBoardModel.move(createMoveRequest(6), playerOne));
        assertFalse(gameBoardModel.move(createMoveRequest(6), playerTwo));
        assertFalse(gameBoardModel.move(createMoveRequest(6), playerOne));
        assertFalse(gameBoardModel.move(createMoveRequest(5), playerTwo));
        assertFalse(gameBoardModel.move(createMoveRequest(5), playerOne));
        assertFalse(gameBoardModel.move(createMoveRequest(5), playerTwo));
        assertFalse(gameBoardModel.move(createMoveRequest(5), playerOne));
        assertFalse(gameBoardModel.move(createMoveRequest(6), playerTwo));
        assertFalse(gameBoardModel.move(createMoveRequest(4), playerOne));
        assertFalse(gameBoardModel.move(createMoveRequest(4), playerTwo));
        assertFalse(gameBoardModel.move(createMoveRequest(4), playerOne));
        assertFalse(gameBoardModel.move(createMoveRequest(4), playerTwo));
        assertTrue(gameBoardModel.move(createMoveRequest(4), playerOne));
        LOG.info("{}, {}", testName, gameBoardModel.toString());
    }

    private MoveRequest createMoveRequest(int column) {

        return new MoveRequest()
            .column(column);
    }
}