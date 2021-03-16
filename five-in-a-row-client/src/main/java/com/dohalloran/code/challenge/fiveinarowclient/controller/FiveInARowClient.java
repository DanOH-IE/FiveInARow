package com.dohalloran.code.challenge.fiveinarowclient.controller;

import com.dohalloran.code.challenge.fiveinarow.api.JoinApi;
import com.dohalloran.code.challenge.fiveinarow.api.MoveApi;
import com.dohalloran.code.challenge.fiveinarow.api.UpdateApi;
import com.dohalloran.code.challenge.fiveinarow.client.ApiException;
import com.dohalloran.code.challenge.fiveinarow.model.GameUpdate;
import com.dohalloran.code.challenge.fiveinarow.model.JoinRequest;
import com.dohalloran.code.challenge.fiveinarow.model.JoinResponse;
import com.dohalloran.code.challenge.fiveinarow.model.MoveRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class FiveInARowClient {

    @Autowired
    private JoinApi joinApi;

    @Autowired
    private MoveApi moveApi;

    @Autowired
    private UpdateApi updateApi;

    public JoinResponse joinGame(JoinRequest joinRequest) {

        try {
            return joinApi.joinGame(joinRequest);
        } catch (ApiException exception) {
            LOG.error("Error while trying to join game", exception);
            return null;
        }
    }

    public void move(String gameId, MoveRequest moveRequest){

        try{
            moveApi.move(gameId, moveRequest);
        } catch (ApiException exception) {
            LOG.error("Error while trying to join game", exception);
        }
    }

    public GameUpdate getUpdate(String gameId){

        try {
           return updateApi.update(gameId);
        } catch (ApiException exception) {
            LOG.error("Error while trying to join game", exception);
            return null;
        }
    }

}
