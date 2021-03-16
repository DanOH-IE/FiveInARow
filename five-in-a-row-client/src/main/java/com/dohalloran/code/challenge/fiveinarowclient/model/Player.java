package com.dohalloran.code.challenge.fiveinarowclient.model;

import com.dohalloran.code.challenge.fiveinarow.model.Colour;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Player {

    private String displayName;
    private String playerId;
    private Colour colour;
}
