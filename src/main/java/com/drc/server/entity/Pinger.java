package com.drc.server.entity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@RequiredArgsConstructor
@Component
@Slf4j
public class Pinger {

    private final SimpMessagingTemplate messagingTemplate;
    private final String PING_MESSAGE = "KeepAlive from server!";

    @Scheduled(fixedRate = 10000) // every 10k ms = every 10 s
    public void sendPing() {
        PingMessage pingMessage = new PingMessage(PING_MESSAGE, Instant.now().toString());
        messagingTemplate.convertAndSend("/client/ping", pingMessage);
        log.debug("Sent ping to client: {}" , pingMessage);
    }
}
