package com.drc.server.controller;

import com.drc.server.entity.BalanceAction;
import com.drc.server.entity.BalanceRequest;
import com.drc.server.entity.User;
import com.drc.server.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class AccountController {

    UserService userService;

    @MessageMapping("/balance")
    @SendTo("/balance")
    public HttpStatus doAccount(BalanceRequest balanceRequest) {

        BalanceAction action = balanceRequest.balanceAction();

        if (!action.equals(BalanceAction.DECREASE) && !action.equals(BalanceAction.INCREASE) && !action.equals(BalanceAction.SET)) {
            return HttpStatus.BAD_REQUEST;
        } else if (userService.getUserByname(balanceRequest.username()) == null) {
            return HttpStatus.BAD_REQUEST;
        } else if (balanceRequest.value() < 0 || balanceRequest.value() > Double.MAX_VALUE) {
            return HttpStatus.BAD_REQUEST;
        }

        User user = userService.getUserByname(balanceRequest.username());
        Double money = user.getMoney();

        if (action.equals(BalanceAction.INCREASE)) {
            money += balanceRequest.value();
            log.debug("Money of {} was increased by {} ", user, balanceRequest.value() );
        } else if (action.equals(BalanceAction.DECREASE)) {
            money -= balanceRequest.value();
            log.debug("Money of {} was decreased by {} ", user, balanceRequest.value() );
        } else if (action.equals(BalanceAction.SET)) {
            money = balanceRequest.value();
            log.debug("Money of {} was set to {} ", user, balanceRequest.value() );
        } else if (action.equals(BalanceAction.DIVIDE)) {
            money = balanceRequest.value();
            log.debug("Money of {} was divided by {} ", user, balanceRequest.value() );
        }

        return HttpStatus.OK;
    }
}
