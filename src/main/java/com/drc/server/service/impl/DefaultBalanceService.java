package com.drc.server.service.impl;

import com.drc.server.entity.Answer;
import com.drc.server.entity.BalanceAction;
import com.drc.server.entity.User;
import com.drc.server.exception.BalanceActionException;
import com.drc.server.exception.BalanceUsernameException;
import com.drc.server.exception.BalanceValueException;
import com.drc.server.persistence.AnswerRepo;
import com.drc.server.service.AnswerService;
import com.drc.server.service.BalanceService;
import com.drc.server.service.GameService;
import com.drc.server.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultBalanceService implements BalanceService {

    private final UserService userService;
    private final GameService gameService;

    public Double handleActionRequest(BalanceAction action, String username, Double value) {
        log.debug("handleActionRequest actionRequest {}, username {}, value {}", action, username, value);

        if (value < 0 || value > Double.MAX_VALUE) {
            throw new BalanceValueException("Value out of allowed range: " + value);
        }
//
//        BalanceAction action;
//        try {
//            action = BalanceAction.valueOf(actionRequest);
//        } catch (IllegalArgumentException e) {
//            throw new BalanceActionException("Action must be one of: DECREASE, INCREASE, SET, DIVIDE");
//        }

        if (userService.getUserByname(username) == null) {
            throw new BalanceUsernameException("User not found: " + username);
        }
        User user = userService.getUserByname(username);
        Double userMoney = user.getMoney();

        log.debug("Balance {}", user);

        switch (action) {
            case INCREASE -> {
                userMoney += value;
                log.debug("Money of {} was increased by {} ", username, value);
            }
            case DECREASE -> {
                userMoney -= value;
                log.debug("Money of {} was decreased by {} ", username, value);
            }
            case SET -> {
                userMoney = value;
                log.debug("Money of {} was set to {} ", username, value);
            }
            case DIVIDE -> {
                userMoney = userMoney / value;
                log.debug("Money of {} was divided by {} ", username, value);
            }
            default -> throw new BalanceActionException("Unsupported action: " + action);
        }


        log.debug("After checks: actionRequest {}, action: {}", action, action);
        log.debug("After checks: {}", user);
        user.setMoney(userMoney);
        log.debug("After set money {}", user);
        userService.update(user);
        log.debug("After user update: {}", user);

        gameService.broadcastUserObjectsInGameByUsername(username);
        return userMoney;
    }

    public void increaseBalanceOfUser(Double value, String username) {
        User user = userService.getUserByname(username);
        Double currentMoney = user.getMoney();
        Double newMoney = handleActionRequest(BalanceAction.INCREASE, username, value);
        log.debug("Increasing money of {} by {}. Result money: {}", username, value, newMoney);

    }
}
