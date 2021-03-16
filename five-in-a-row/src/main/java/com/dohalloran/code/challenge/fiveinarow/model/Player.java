package com.dohalloran.code.challenge.fiveinarow.model;

import lombok.Data;

@Data
public class Player {

    private final String displayName;
    private final String playerId;
    private final String gameId;
    private final Colour colour;

}
