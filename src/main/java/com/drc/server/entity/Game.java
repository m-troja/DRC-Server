package com.drc.server.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Data
@Component
public class Game {

    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

}
