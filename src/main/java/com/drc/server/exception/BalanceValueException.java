package com.drc.server.exception;

import lombok.Getter;

@Getter
public class BalanceValueException extends RuntimeException {

    public BalanceValueException(String message) {
        super(message);
    }
}
