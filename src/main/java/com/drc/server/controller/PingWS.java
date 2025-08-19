package com.drc.server.controller;

import com.drc.server.entity.PingMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class PingWS {

    @MessageMapping("/ping")
    @SendTo("/ping")
    public void pingListener(PingMessage pingMessage) {
//        log.debug("Received ping from client: {}" , pingMessage);
    }
}
