package com.drc.server.controller;

import com.drc.server.entity.BalanceAction;
import com.drc.server.entity.Response;
import com.drc.server.entity.ResponseType;
import com.drc.server.service.BalanceService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@Slf4j
@RestController
@RequestMapping("/v1")
public class BalanceRestController {

    private final BalanceService balanceService;

    @GetMapping("/balance")
    public Response doAccount(@RequestParam("action") BalanceAction action, @RequestParam("username") String username,
                              @RequestParam("value") Double value) {
        log.debug("Action {}, username {}, value {}", action, username, value);

        Double money = balanceService.handleActionRequestForSingleUser(action, username, value);

        return new Response(ResponseType.BALANCE_ACTION_OK,"Money of user '" + username + "' = " + money);
    }
}
