package com.drc.server.controller;

import com.drc.server.entity.Response;
import com.drc.server.entity.ResponseType;
import com.drc.server.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/v1/cheater")
@RestController
@Slf4j
@RequiredArgsConstructor
public class CheaterRestController {

    private final GameService gameService;

    @GetMapping("/shoot")
    public Response shootPlayer(@RequestParam("username") String username) {
        log.debug("Cheater has shoot {} :(", username);
        gameService.shootPlayer(username);
        gameService.broadcastUserObjectsInGameByUsername(username);
        return new Response(ResponseType.SHOOT_PLAYER, "Cheater has shoot " + username);
    }
}
