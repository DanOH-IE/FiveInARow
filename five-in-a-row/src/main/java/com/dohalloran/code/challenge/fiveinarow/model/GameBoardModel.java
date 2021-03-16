package com.dohalloran.code.challenge.fiveinarow.model;

import com.dohalloran.code.challenge.fiveinarow.utils.Direction;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.dohalloran.code.challenge.fiveinarow.model.Colour.GREY;
import static com.dohalloran.code.challenge.fiveinarow.model.Colour.RED;

@Slf4j
@Getter
public class GameBoardModel {

    private final Colour[][] board;
    private final int numRows;
    private final int numColumns;
    private final int winningNum;

    public GameBoardModel(int numRows, int numColumns, int winningNum) {

        this.numRows = numRows;
        this.numColumns = numColumns;
        this.winningNum = winningNum;

        // Initialise the board
        board = new Colour[numRows][numColumns];

        for (Colour[] row : board) {
            Arrays.fill(row, GREY);
        }
    }

    public boolean move(MoveRequest moveRequest, Player currentPlayer) {
        String methodName = "move";
        int row = calculateRow(moveRequest);
        int column = moveRequest.getColumn();

        board[row][column] = currentPlayer.getColour();
        if (isWinningMove(row, column)) {
            LOG.info("{}, game over . . . [{}] wins !!!", methodName, currentPlayer.getDisplayName());
            return true;
        } else {
            LOG.debug("{}, not a winning move", methodName);
            return false;
        }
    }

    private boolean isWinningMove(int row, int column) {

        return numberInDirection(row, column, Direction.UP) + numberInDirection(row, column, Direction.DOWN) > winningNum
            || numberInDirection(row, column, Direction.LEFT) + numberInDirection(row, column, Direction.RIGHT) > winningNum
            || numberInDirection(row, column, Direction.UP_LEFT) + numberInDirection(row, column, Direction.DOWN_RIGHT) > winningNum
            || numberInDirection(row, column, Direction.UP_RIGHT) + numberInDirection(row, column, Direction.DOWN_LEFT) > winningNum;
    }

    private int numberInDirection(int row, int column, Direction direction) {
        String methodName = "numberInDirection";
        Colour colour = board[row][column];

        int horizontalPosition = row;
        int verticalPosition = column;

        LOG.info("{}, checking number of [{}] discs from position [{},{}], direction=[{}]",
            methodName, colour, row, column, direction);
        int numInDirection = 0;

        while (colour.equals(board[horizontalPosition][verticalPosition])) {

            horizontalPosition += direction.getHorizontal();
            verticalPosition += direction.getVertical();
            numInDirection++;

            if (horizontalPosition < 0 || verticalPosition < 0 || horizontalPosition >= numRows || verticalPosition >= numColumns) {
                LOG.warn("{}, reached edge of the board row=[{}], column=[{}]", methodName, row, column);
                break;
            }
        }

        LOG.info("{}, direction=[{}], total=[{}]", methodName, direction, numInDirection);
        return numInDirection;

    }


    private int calculateRow(MoveRequest moveRequest) {
        String methodName = "calculateRow";

        int row = 0;
        if (moveRequest.getColumn() < 0 || moveRequest.getColumn() >= numColumns) {
            LOG.error("{}, user has input column which is out of bounds, move=[{}], bounds from [0 - {}]", methodName, moveRequest.getColumn(), (numColumns - 1));
            throw new IllegalArgumentException("INVALID MOVE, PLEASE PICK A NUMBER BETWEEN 0 -" + (numColumns - 1));
        }

        while (!GREY.equals(board[row][moveRequest.getColumn()])) {
            row++;

            if (row >= numRows) {
                LOG.error("{}, COLUMN [{}] FULL", methodName, moveRequest.getColumn());
                throw new IllegalArgumentException("THIS COLUMN IS FULL");
            }
        }

        return row;
    }

    public List<List<Colour>> getBoard() {

        return Arrays.stream(board)
            .map(colours -> Arrays.stream(colours)
                .collect(Collectors.toList()))
            .collect(Collectors.toList());
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("GameBoard: \n");
        for (int y = numRows - 1; y >= 0; y--) {
            stringBuilder.append("|");
            for (int x = 0; x < numColumns; x++) {
                stringBuilder.append("[");

                Colour colour = board[y][x];
                if (RED.equals(colour)) {
                    // Add a whitespace to prettify toString when red square
                    stringBuilder.append(colour.name().concat(" "));
                } else {
                    stringBuilder.append(colour.name());
                }
                stringBuilder.append("]");
            }
            stringBuilder.append("|");
            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }
}