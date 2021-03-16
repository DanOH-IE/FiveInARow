package com.dohalloran.code.challenge.fiveinarowclient.model;

import com.dohalloran.code.challenge.fiveinarow.model.Colour;

import java.util.List;
import java.util.Objects;

import static com.dohalloran.code.challenge.fiveinarow.model.Colour.RED;

public class GameBoardModel {

    private final Colour[][] board;

    public GameBoardModel(List<List<Colour>> gameBoard) {
        if (Objects.isNull(gameBoard)) {
            board = new Colour[0][0];
        } else {
            this.board = gameBoard.stream()
                .map(row -> row.toArray(Colour[]::new))
                .toArray(Colour[][]::new);
        }
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("GameBoard: \n");
        for (int y = board.length - 1; y >= 0; y--) {
            stringBuilder.append("|");
            for (int x = 0; x < board[0].length - 1; x++) {
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
