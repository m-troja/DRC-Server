package com.drc.server.controller;

import com.drc.server.entity.BalanceAction;
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
    public ResponseEntity<String> doAccount(@RequestParam("action") BalanceAction actionRequest, @RequestParam("username") String username,
                                    @RequestParam("value") String value) {
        log.debug("Action {}, username {}, value {}", actionRequest, username, value);
        Double valueDouble = Double.valueOf(value);

        if (!actionRequest.equals(BalanceAction.DECREASE) && !actionRequest.equals(BalanceAction.INCREASE) && !actionRequest.equals(BalanceAction.SET) && !actionRequest.equals(BalanceAction.DIVIDE)) {
            return ResponseEntity.badRequest().body("Wrong action");
        } else if (userService.getUserByname(username) == null) {
            return ResponseEntity.badRequest().body("Wrong username");
        } else if (valueDouble < 0 || valueDouble > Double.MAX_VALUE) {
            return ResponseEntity.badRequest().body("Wrong valueDouble");
        }
        Double money = userService.updateBalance(actionRequest, username, value);

        return ResponseEntity.ok().body("Money of user '" + username + "' = " + money);
    }
}
