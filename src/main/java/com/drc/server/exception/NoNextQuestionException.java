package com.drc.server.exception;

import lombok.Getter;

@Getter
public class NoNextQuestionException extends RuntimeException {

    public NoNextQuestionException(String message) {
        super(message);
    }
}


