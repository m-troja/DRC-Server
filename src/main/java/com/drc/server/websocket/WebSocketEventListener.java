package com.drc.server.websocket;

import com.drc.server.entity.User;
import com.drc.server.service.GameService;
import com.drc.server.service.NotificationService;
import com.drc.server.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@RequiredArgsConstructor
@Slf4j
@Component
public class WebSocketEventListener {
    private final GameService gameService;
    private final UserService userService;
    private final NotificationService notificationService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId =  (String) headerAccessor.getSessionAttributes().get(WebSocketHandshakeInterceptor.HTTP_SESSION_ID_PARAM_NAME);
        String username = userService.getUserByHttpSesssionid(sessionId).getName();
        log.debug("SessionConnectEvent: sessionId: {} username: {}", sessionId, username);
        log.debug("SessionConnectEvent attributes: {}", headerAccessor.getSessionAttributes());
        String stompSessionId = headerAccessor.getSessionId();
        log.info("STOMP CONNECT event, stompSessionId: {}", stompSessionId);
        User user = userService.getUserByHttpSesssionid(sessionId);
        user.setStompSessionId(stompSessionId);
        if (user == null) {
            log.debug("User not found for stompSessionId: {}", stompSessionId);
        } else {
            log.debug("User found for stompSessionId: {}, user: {}", stompSessionId, user.getName());
            userService.update(user);
            notificationService.notifyAdminThatNewUserConnected(user);
        }
        log.debug("Debug: ");
        log.debug("user by stompSessionId: {} ", userService.getByStompSessionId(stompSessionId));
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        String stompSessionId = event.getSessionId();
        log.debug("SessionDisconnectEvent: stompSessionId: {}", stompSessionId);
        User user = userService.getByStompSessionId(stompSessionId);
        if (user == null) {
            log.debug("No user found for stompSessionId: {}", stompSessionId);
            return;
        }
    }

    public void disconnectUser(User user) {
        if (user == null) {
            log.debug("No user found ");
            return;
        }

            log.debug("Disconnect user: {}", user);
            notificationService.notifyAdminThatUserDisconnected(user);
            userService.delete(user);
            log.debug("Unregistered and deleted user: {}", user);

    }
}