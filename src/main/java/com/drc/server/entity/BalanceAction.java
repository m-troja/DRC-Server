package com.drc.server.entity;

import com.drc.server.exception.BalanceActionException;

public enum BalanceAction {
    DECREASE,
    INCREASE,
    SET,
    DIVIDE;

    public static BalanceAction fromString(String action) {
        try {
            return BalanceAction.valueOf(action.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new BalanceActionException("Invalid action: " + action + ". Must be one of: DECREASE, INCREASE, SET, DIVIDE.");
        }
    }
}
