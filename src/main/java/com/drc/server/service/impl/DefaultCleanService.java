package com.drc.server.service.impl;

import com.drc.server.service.CleanService;
import com.drc.server.service.GameService;
import com.drc.server.service.UserService;
import com.drc.server.websocket.WebSocketSessionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultCleanService implements CleanService {

    private final GameService gameService;
    private final UserService userService;
    private final WebSocketSessionRegistry webSocketSessionRegistry;

    public void deleteAllGames() {
        userService.deleteAllUsers();
        gameService.deleteAllGames();

    }

    public void cleanServer() {
        userService.deleteAllUsers();
        gameService.deleteAllGames();
        webSocketSessionRegistry.getLastPingMap().clear();
    }
}
