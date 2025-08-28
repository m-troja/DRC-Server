package com.drc.server.service;

import com.drc.server.entity.BalanceAction;
import com.drc.server.entity.User;

import java.util.List;

public interface BalanceService {
    Double handleActionRequestForSingleUser(BalanceAction action, String username, Double value );
    void increaseBalanceOfUser(Double value, String username);
    void handleActionRequestOfMultipleUsers(BalanceAction action, List<User> users, Double value);
}
