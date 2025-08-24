package com.drc.server.exception;

import lombok.Getter;

@Getter
public class BalanceActionException extends RuntimeException {

    public BalanceActionException(String message) {
        super(message);
    }
}
