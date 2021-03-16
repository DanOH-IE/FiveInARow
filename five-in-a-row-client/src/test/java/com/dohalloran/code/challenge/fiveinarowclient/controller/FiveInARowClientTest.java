package com.dohalloran.code.challenge.fiveinarowclient.controller;

import com.dohalloran.code.challenge.fiveinarow.api.JoinApi;
import com.dohalloran.code.challenge.fiveinarow.api.MoveApi;
import com.dohalloran.code.challenge.fiveinarow.api.UpdateApi;
import com.dohalloran.code.challenge.fiveinarow.client.ApiException;
import com.dohalloran.code.challenge.fiveinarow.model.GameUpdate;
import com.dohalloran.code.challenge.fiveinarow.model.JoinRequest;
import com.dohalloran.code.challenge.fiveinarow.model.JoinResponse;
import com.dohalloran.code.challenge.fiveinarow.model.MoveRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FiveInARowClientTest {

    private final String gameId = "gameId";

    @Mock
    private JoinApi joinApi;

    @Mock
    private MoveApi moveApi;

    @Mock
    private UpdateApi updateApi;

    @Mock
    private JoinRequest joinRequest;

    @Mock
    private JoinResponse joinResponse;

    @Mock
    private GameUpdate gameUpdate;

    @Mock
    private MoveRequest moveRequest;

    @InjectMocks
    private FiveInARowClient fiveInARowClient;

    @Test
    void move() throws ApiException {
        fiveInARowClient.move(gameId, moveRequest);
        verify(moveApi, times(1)).move(gameId, moveRequest);
    }

    @Test
    void update() throws ApiException {
        when(updateApi.update(gameId)).thenReturn(gameUpdate);
        assertSame(gameUpdate, fiveInARowClient.getUpdate(gameId));
        verify(updateApi, times(1)).update(gameId);
    }

    @Test
    void join() throws ApiException {
        when(joinApi.joinGame(joinRequest)).thenReturn(joinResponse);

        assertSame(joinResponse, fiveInARowClient.joinGame(joinRequest));

        verify(joinApi, times(1)).joinGame(joinRequest);
    }
}