package com.drc.server.exception;

public class GameCommandNotSupportedException extends RuntimeException {

    public GameCommandNotSupportedException(String message) {
        super(message);
    }
}
