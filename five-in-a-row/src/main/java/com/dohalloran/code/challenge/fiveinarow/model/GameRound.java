package com.dohalloran.code.challenge.fiveinarow.model;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class GameRound {

    private String gameId;
    private Player currentPlayer;
    private Player winningPlayer;
    private Player playerOne;
    private Player playerTwo;
    private GameStatus gameStatus;
    private final GameBoardModel gameBoardModel;

    public GameRound(int numRows, int numCols, int winningNum) {
        gameBoardModel = new GameBoardModel(numRows, numCols, winningNum);
    }
}
