package com.dohalloran.code.challenge.fiveinarow.utils;

import lombok.Getter;

@Getter
public enum Direction {

    UP(0, 1),
    DOWN(0,-1),
    RIGHT(1, 0),
    LEFT(-1, 0),
    UP_RIGHT(1, 1),
    UP_LEFT(1, -1),
    DOWN_RIGHT(-1, 1),
    DOWN_LEFT(-1, -1);

    private final int horizontal;
    private final int vertical;

    Direction(int horizontal, int vertical){
        this.horizontal = horizontal;
        this.vertical = vertical;
    }
}
