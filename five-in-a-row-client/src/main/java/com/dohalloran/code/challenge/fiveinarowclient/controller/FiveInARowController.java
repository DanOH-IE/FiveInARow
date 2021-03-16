package com.dohalloran.code.challenge.fiveinarowclient.controller;

import com.dohalloran.code.challenge.fiveinarow.model.GameUpdate;
import com.dohalloran.code.challenge.fiveinarow.model.JoinRequest;
import com.dohalloran.code.challenge.fiveinarow.model.JoinResponse;
import com.dohalloran.code.challenge.fiveinarow.model.MoveRequest;
import com.dohalloran.code.challenge.fiveinarowclient.model.GameBoardModel;
import com.dohalloran.code.challenge.fiveinarowclient.model.GameModel;
import com.dohalloran.code.challenge.fiveinarowclient.model.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.dohalloran.code.challenge.fiveinarow.model.GameStatus.GAME_OVER;
import static com.dohalloran.code.challenge.fiveinarow.model.GameStatus.NOT_STARTED;
import static com.dohalloran.code.challenge.fiveinarow.model.GameStatus.STARTED;
import static com.dohalloran.code.challenge.fiveinarow.model.GameStatus.WAITING_FOR_PLAYER_TO_JOIN;

@Slf4j
public class FiveInARowController {

    @Autowired
    private GameModel gameModel;

    @Autowired
    private FiveInARowClient fiveInARowClient;

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    @PostConstruct
    public void init() {
        startGame();
    }

    public void startGame() {

        if (NOT_STARTED.equals(gameModel.getGameStatus())) {
            joinGame();
        }

        executorService.scheduleAtFixedRate(this::getGameUpdate, 1, 2, TimeUnit.SECONDS);

    }


    public void getGameUpdate() {
        GameUpdate gameUpdate = fiveInARowClient.getUpdate(gameModel.getGameId());
        eventReceived(gameUpdate);
    }


    public void eventReceived(GameUpdate gameUpdate) {

        try {
            updateModel(gameUpdate);
            promptUser();
        } catch (Exception exception) {
            LOG.error("error", exception);
        }
    }

    public void promptUser() {

        if (WAITING_FOR_PLAYER_TO_JOIN.equals(gameModel.getGameStatus()) && !gameModel.getWaiting().get()) {
            System.out.println("WAITING FOR A PLAYER TO JOIN");
            gameModel.getWaiting().set(true);
        } else if (STARTED.equals(gameModel.getGameStatus())) {
            if (gameModel.getPlayerId().equals(gameModel.getCurrentPlayerId())) {
                gameModel.getWaiting().set(false);
                displayBoard();
                move();
            } else if (!gameModel.getWaiting().get()) {
                gameModel.getWaiting().set(true);
                System.out.println("WAIT YOUR TURN");
            }
        } else if (GAME_OVER.equals(gameModel.getGameStatus())) {
            displayBoard();
            System.out.println("GAME OVER " + (gameModel.getPlayerId().equals(gameModel.getWinningPlayerId()) ? "YOU'RE A WINNER!!!!!" : "YOU LOSE!!!"));
            System.exit(0);
        }
    }

    public void joinGame() {

        System.out.println("HELLO . . . ");
        System.out.println("WELCOME TO 5 IN A ROW . . . ");
        System.out.println("PLEASE ENTER YOUR NAME: ");
        Scanner scanner = new Scanner(System.in);
        String displayName = scanner.nextLine();

        JoinRequest joinRequest = new JoinRequest()
            .name(displayName);

        JoinResponse joinResponse = fiveInARowClient.joinGame(joinRequest);
        if (Objects.isNull(joinResponse)) {
            System.out.println("Error while joining");
        }

        System.out.println("JOIN RESPONSE = " + joinResponse);
        Player player = Player.builder()
            .colour(joinResponse.getColour())
            .displayName(displayName)
            .playerId(joinResponse.getPlayerId())
            .build();
        gameModel.setGameId(joinResponse.getGameId());
        gameModel.setPlayerId(joinResponse.getPlayerId());
        gameModel.setDisplayName(displayName);
        gameModel.setPlayer(player);
    }

    public void move() {

        System.out.printf("It's your turn " + gameModel.getDisplayName() + ", place a " + gameModel.getPlayer().getColour() + " chip by entering a number between (1-9): ");
        Scanner scanner = new Scanner(System.in);
        int column = scanner.nextInt();

        MoveRequest playerMoveRequest = new MoveRequest()
            .playerId(gameModel.getPlayerId())
            .column(column);

        fiveInARowClient.move(gameModel.getGameId(), playerMoveRequest);
    }


    public void updateModel(GameUpdate gameUpdate) {
        gameModel.setGameStatus(gameUpdate.getGameStatus());
        gameModel.setGameId(gameUpdate.getGameId());
        gameModel.setCurrentPlayerId(gameUpdate.getCurrentPlayerId());
        gameModel.setWinningPlayerId(gameUpdate.getWinningPlayerId());
        gameModel.setGameBoard(new GameBoardModel(gameUpdate.getGameboard()));
    }

    public void displayBoard() {
        System.out.println(gameModel.getGameBoard());
    }

}
