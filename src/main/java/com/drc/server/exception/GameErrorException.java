package com.drc.server.exception;

public class GameErrorException extends RuntimeException {

    public GameErrorException(String message) {
        super(message);
    }
}
