package com.drc.server.service;

import com.drc.server.entity.BalanceAction;

public interface BalanceService {
    Double handleActionRequest(BalanceAction action, String username, Double value );
    void increaseBalanceOfUser(Double value, String username);

}
