package com.dohalloran.code.challenge.fiveinarow.controller;

import com.dohalloran.code.challenge.fiveinarow.model.Colour;
import com.dohalloran.code.challenge.fiveinarow.model.GameRound;
import com.dohalloran.code.challenge.fiveinarow.model.GameUpdate;
import com.dohalloran.code.challenge.fiveinarow.model.JoinRequest;
import com.dohalloran.code.challenge.fiveinarow.model.JoinResponse;
import com.dohalloran.code.challenge.fiveinarow.model.MoveRequest;
import com.dohalloran.code.challenge.fiveinarow.model.Player;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.dohalloran.code.challenge.fiveinarow.model.GameStatus.GAME_OVER;
import static com.dohalloran.code.challenge.fiveinarow.model.GameStatus.STARTED;
import static com.dohalloran.code.challenge.fiveinarow.model.GameStatus.WAITING_FOR_PLAYER_TO_JOIN;

@Slf4j
@Getter
@Setter
@Component
public class GamesManager {

    @Value("${game.deletionTimeoutSeconds}")
    private long deletionTimeoutSeconds;

    @Value("${game.numColumns}")
    private int numColumns;

    @Value("${game.numRows}")
    private int numRows;

    @Value("${game.winningNum}")
    private int winningNum;

    private final Map<String, GameRound> gamesInProgress = new ConcurrentHashMap<>();
    private final Queue<GameRound> gamesWaitingForPlayer = new ConcurrentLinkedQueue<>();
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public JoinResponse joinGame(JoinRequest joinRequest) {
        String methodName = "joinGame";
        GameRound gameRound;
        if (Objects.isNull(gamesWaitingForPlayer.peek())) {
            String gameId = UUID.randomUUID().toString();
            Player player = createPlayer(joinRequest, gameId, Colour.RED);
            gameRound = createGameRound(gameId, player);
            LOG.info("{}, adding player one [{}] to round [{}]", methodName, joinRequest.getName(), gameRound);

            gamesWaitingForPlayer.add(gameRound);
            gamesInProgress.put(gameId, gameRound);
            return createJoinResponse(player);
        } else {
            gameRound = gamesWaitingForPlayer.poll();
            Player player = createPlayer(joinRequest, gameRound.getGameId(), Colour.BLUE);
            gameRound.setPlayerTwo(player);
            gameRound.setGameStatus(STARTED);

            gamesInProgress.put(gameRound.getGameId(), gameRound);

            LOG.info("{}, adding player two [{}] to round [{}]", methodName, joinRequest, gameRound);

            return createJoinResponse(player);
        }
    }

    public GameRound playerMove(String gameId, MoveRequest playerMove) {
        String methodName = "playerMove";

        if (Objects.isNull(playerMove) || StringUtils.isBlank(gameId)) {
            LOG.error("{}, invalid move [{}]", methodName, playerMove);
            throw new IllegalArgumentException("INVALID MOVE " + playerMove);
        }

        if (!gamesInProgress.containsKey(gameId)) {
            LOG.error("{}, no game ongoing for gameId [{}] player [{}]", methodName, gameId, playerMove);
            throw new IllegalArgumentException(String.format("CANNOT FIND GAME WITH ID=[%s]", gameId));
        }

        GameRound gameRound = gamesInProgress.get(gameId);
        if (!gameRound.getCurrentPlayer().getPlayerId().equals(playerMove.getPlayerId())) {
            LOG.error("{}, Player must wait their turn, playerMove=[{}]", methodName, playerMove);
            throw new IllegalArgumentException(String.format("NOT PLAYER [%s]'s move", playerMove.getPlayerId()));
        }

        if (!STARTED.equals(gameRound.getGameStatus())) {
            throw new IllegalArgumentException(String.format("CANNOT MOVE NOW, GAME STATUS = [%s]", gameRound.getGameStatus()));
        }

        if (gameRound.getGameBoardModel().move(playerMove, gameRound.getCurrentPlayer())) {
            gameRound.setGameStatus(GAME_OVER);
            gameRound.setWinningPlayer(gameRound.getCurrentPlayer());
            deleteGame(gameRound.getGameId());
        } else {
            if (gameRound.getCurrentPlayer().getPlayerId().equals(gameRound.getPlayerOne().getPlayerId())) {
                gameRound.setCurrentPlayer(gameRound.getPlayerTwo());
            } else {
                gameRound.setCurrentPlayer(gameRound.getPlayerOne());
            }
            LOG.info("{}, updating current player to [{}], round [{}]", methodName, gameRound.getCurrentPlayer(), gameRound);
        }

        return gameRound;
    }

    public GameUpdate getGameUpdate(String gameId) {

        if (StringUtils.isBlank(gameId)) {
            throw new IllegalArgumentException("Null game Id");
        }

        if (!gamesInProgress.containsKey(gameId)) {
            throw new IllegalArgumentException("Unexpected game Id " + gameId);
        }

        GameRound gameRound = gamesInProgress.get(gameId);

        GameUpdate gameUpdate = new GameUpdate()
            .gameId(gameRound.getGameId())
            .gameStatus(gameRound.getGameStatus())
            .currentPlayerId(gameRound.getCurrentPlayer().getPlayerId())
            .gameboard(gameRound.getGameBoardModel().getBoard());

        if (!Objects.isNull(gameRound.getWinningPlayer())) {
            gameUpdate.setWinningPlayerId(gameRound.getWinningPlayer().getPlayerId());
        }

        return gameUpdate;
    }

    public void deleteGame(String gameId) {
        String methodName = "deleteGame";
        if (StringUtils.isBlank(gameId)) {
            LOG.error("{}, cannot delete null gameId", methodName);
            return;
        }

        scheduleGameForDeletion(gameId);
    }

    private Player createPlayer(JoinRequest joinRequest, String gameId, Colour colour) {
        return new Player(joinRequest.getName(), UUID.randomUUID().toString(), gameId, colour);
    }

    private JoinResponse createJoinResponse(Player player) {
        return new JoinResponse()
            .colour(player.getColour())
            .displayName(player.getDisplayName())
            .gameId(player.getGameId())
            .playerId(player.getPlayerId());
    }

    private GameRound createGameRound(String gameId, Player player) {

        GameRound gameRound = new GameRound(numRows, numColumns, winningNum);
        gameRound.setPlayerOne(player);
        gameRound.setCurrentPlayer(player);
        gameRound.setGameId(gameId);
        gameRound.setGameStatus(WAITING_FOR_PLAYER_TO_JOIN);

        return gameRound;
    }

    private void scheduleGameForDeletion(String gameId) {
        String methodName = "scheduleGameForDeletion";

        LOG.info("{}, scheduling game for deletion in [{}]s, gameId=[{}]", methodName, deletionTimeoutSeconds, gameId);
        executorService.schedule(() -> {
            GameRound gameRound = gamesInProgress.remove(gameId);
            if (!Objects.isNull(gameRound)) {
                gamesWaitingForPlayer.remove(gameRound);
            }
            LOG.info("{}, expired from cache [{}]", methodName, gameRound);
        }, deletionTimeoutSeconds, TimeUnit.SECONDS);

    }

}
