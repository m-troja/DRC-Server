package com.drc.server.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
@Slf4j
public class UserPrincipalChannelInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (accessor.getUser() == null) {
            String username = accessor.getFirstNativeHeader("username");
            if (username == null) {
                username = (String) accessor.getSessionAttributes().get("username");
            }

            if (username != null) {
                String finalUsername = username;
                Principal principal = () -> finalUsername;
                accessor.setUser(principal);

                log.debug("Set Principal: {}", principal.getName());
            } else {
                log.debug("No username in header or session - principal was not set");
            }
        } else {
            log.debug("Principal already exists: {}", accessor.getUser().getName());
        }

        return message;
    }
}

