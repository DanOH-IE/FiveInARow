package com.dohalloran.code.challenge.fiveinarow.controller;

import com.dohalloran.code.challenge.fiveinarow.api.V1Api;
import com.dohalloran.code.challenge.fiveinarow.model.GameUpdate;
import com.dohalloran.code.challenge.fiveinarow.model.JoinRequest;
import com.dohalloran.code.challenge.fiveinarow.model.JoinResponse;
import com.dohalloran.code.challenge.fiveinarow.model.MoveRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import javax.validation.Valid;

@Slf4j
@Controller
public class FiveInARowController implements V1Api {

    @Autowired
    private GamesManager gamesManager;

    @Override
    public ResponseEntity<JoinResponse> joinGame(JoinRequest joinRequest) {
        String methodName = "joinGame";

        LOG.info("{}, received request to join a game [{}]", methodName, joinRequest);
        JoinResponse joinResponse = gamesManager.joinGame(joinRequest);
        return new ResponseEntity<>(joinResponse, HttpStatus.ACCEPTED);
    }

    @Override
    public ResponseEntity<Void> move(String gameId, @Valid MoveRequest moveRequest) {
        String methodName = "move";

        LOG.info("{}, received request to move [{}]", methodName, moveRequest);
        gamesManager.playerMove(gameId, moveRequest);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @Override
    public ResponseEntity<GameUpdate> update(String gameId) {
        String methodName = "update";

        LOG.info("{}, received an update request", methodName);
        GameUpdate gameUpdate = gamesManager.getGameUpdate(gameId);
        return new ResponseEntity<>(gameUpdate, HttpStatus.ACCEPTED);
    }
}
