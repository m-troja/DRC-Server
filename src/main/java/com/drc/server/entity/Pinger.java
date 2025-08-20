package com.drc.server.entity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class Pinger {

    private final SimpMessagingTemplate messagingTemplate;

    @Scheduled(fixedRate = 10000) // every 10k ms = every 10 s
    public void sendPing() {
        PingMessage pingMessage = new PingMessage(System.currentTimeMillis());
        messagingTemplate.convertAndSend("/client/ping", pingMessage);
        log.debug("Sent ping to clients: {}" , pingMessage);
    }
}
