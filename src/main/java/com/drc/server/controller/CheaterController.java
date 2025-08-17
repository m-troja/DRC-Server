package com.drc.server.controller;


import com.drc.server.entity.Role;
import com.drc.server.entity.User;
import com.drc.server.service.RoleService;
import com.drc.server.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class CheaterController {

    private RoleService roleService;
    private UserService userService;

    @MessageMapping("/cheater")
    @SendTo("/cheater")
    public HttpStatus selectCheater(String username) {

        User user = null;
        try {
            user = userService.getUserByname(username);
        } catch (Exception e) {
            log.debug("Error setting role cheater for {} : {} ", user , e);
        }
        Role roleCheater = roleService.getRoleByName(RoleService.ROLE_CHEATER);
        user.setRole(roleCheater);
        log.debug("Set {} for {} ", roleCheater, user);
        return HttpStatus.OK;
    }
}
