package com.dohalloran.code.challenge.fiveinarowclient.model;

import com.dohalloran.code.challenge.fiveinarow.model.GameStatus;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Data
@Component
public class GameModel {

    private String gameId;
    private String displayName;
    private String playerId;
    private String currentPlayerId;
    private String winningPlayerId;
    private Player player;
    private GameBoardModel gameBoard;
    private final AtomicBoolean waiting = new AtomicBoolean(false);
    private GameStatus gameStatus = GameStatus.NOT_STARTED;


}
