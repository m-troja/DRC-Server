package com.drc.server.exception;

import lombok.Getter;

@Getter
public class BalanceUsernameException extends RuntimeException {

    public BalanceUsernameException(String message) {
        super(message);
    }
}
