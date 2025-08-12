package com.drc.server.websocket;

import com.drc.server.entity.Game;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Slf4j
@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private final Game game;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        game.getSessions().add(session);
        log.debug("Added into sessions: {}", session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        game.getSessions().remove(session);
        log.debug("Removed from sessions: {}", session.getId());
    }
}