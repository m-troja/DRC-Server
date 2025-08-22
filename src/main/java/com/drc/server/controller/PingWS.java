package com.drc.server.controller;

import com.drc.server.entity.PingMessage;
import com.drc.server.entity.User;
import com.drc.server.service.UserService;
import com.drc.server.websocket.WebSocketSessionRegistry;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@AllArgsConstructor
@Slf4j
@Controller
public class PingWS {

    private final WebSocketSessionRegistry sessionRegistry;
    private final UserService userService;

    @MessageMapping("/ping")
    public void pingListener(@Payload PingMessage pingMessage,
                             SimpMessageHeaderAccessor headerAccessor) {

        String sessionId = headerAccessor.getSessionId();
        Principal principal = headerAccessor.getUser();
        User user = userService.getByStompSessionId(sessionId);
        log.debug("Received ping from {}, message {}", user , pingMessage);

        sessionRegistry.updateLastPingMap(user, System.currentTimeMillis());
    }
}
