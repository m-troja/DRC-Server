package com.drc.server.service.impl;

import com.drc.server.entity.PingMessage;
import com.drc.server.service.PingService;
import com.drc.server.websocket.WebSocketSessionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class DefaultPingService implements PingService {

    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketSessionRegistry webSocketSessionRegistry;

    @Scheduled(fixedRate = 10000) // every 10k ms = every 10 s
    public void sendPing() {
        if ( !webSocketSessionRegistry.getAllSessions().isEmpty())
        {
            PingMessage pingMessage = new PingMessage(System.currentTimeMillis());
            messagingTemplate.convertAndSend("/client/ping", pingMessage);
            log.debug("Sent ping to clients: {}" , pingMessage);
        }
    }


}
