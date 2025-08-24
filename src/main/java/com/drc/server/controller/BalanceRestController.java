package com.drc.server.controller;

import com.drc.server.entity.BalanceAction;
import com.drc.server.entity.Response;
import com.drc.server.entity.ResponseType;
import com.drc.server.exception.BalanceActionException;
import com.drc.server.exception.BalanceUsernameException;
import com.drc.server.exception.BalanceValueException;
import com.drc.server.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@Slf4j
@RestController
@RequestMapping("/v1")
public class BalanceRestController {

    private final UserService userService;

    @GetMapping("/balance")
    public Response doAccount(@RequestParam("action") String actionRequest, @RequestParam("username") String username,
                              @RequestParam("value") String value) {
        log.debug("Action {}, username {}, value {}", actionRequest, username, value);
        double valueDouble ;

        try {
            valueDouble = Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new BalanceValueException("Invalid numeric value: " + value);
        }
        if (valueDouble < 0 || valueDouble > Double.MAX_VALUE) {
            throw new BalanceValueException("Value out of allowed range: " + valueDouble);
        }

        BalanceAction action;
        try {
            action = BalanceAction.valueOf(actionRequest.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BalanceActionException("Action must be one of: DECREASE, INCREASE, SET, DIVIDE");
        }

        if (userService.getUserByname(username) == null) {
            throw new BalanceUsernameException("User not found: " + username);
        }
        Double money = userService.updateBalance(BalanceAction.valueOf(actionRequest), username, value);

        return new Response(ResponseType.BALANCE_ACTION_OK,"Money of user '" + username + "' = " + money);
    }
}
