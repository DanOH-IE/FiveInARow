package com.dohalloran.code.challenge.fiveinarow.controller;

import com.dohalloran.code.challenge.fiveinarow.model.GameError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@ControllerAdvice
public class FiveInARowExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<GameError> handleGameError(Exception exception, WebRequest webRequest){

        GameError gameError = new GameError()
            .errorMessage(exception.getMessage());

        LOG.error("{}, returning error", gameError);

        return new ResponseEntity<>(gameError, HttpStatus.BAD_REQUEST);
    }
}
