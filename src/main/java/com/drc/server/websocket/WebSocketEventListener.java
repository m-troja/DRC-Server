package com.drc.server.websocket;

import com.drc.server.entity.ErrorMessage;
import com.drc.server.entity.User;
import com.drc.server.service.RoleService;
import com.drc.server.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.Instant;

@RequiredArgsConstructor
@Slf4j
@Component
public class WebSocketEventListener {
    private final WebSocketSessionRegistry sessionRegistry;
    private final RoleService roleService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String username = headerAccessor.getFirstNativeHeader("username");
        String roleStr = headerAccessor.getFirstNativeHeader("role");
        log.debug("Session connected: sessionId: {} username: {} roleStr: {}", sessionId,username, roleStr );

        if (username != null && roleStr != null) {
            try
            {
                User user = new User(
                        sessionId,
                        username,
                        0.0,
                        roleService.getRoleByName(roleStr) // enum Role
                );

                boolean isUserSaved = userService.save(user);
                if (isUserSaved)
                {
                    sessionRegistry.register(sessionId, user);
                    log.debug("Registered: sessionId={}, username={}, roleStr={}", sessionId, username, roleStr);
                }
                else {
                    ErrorMessage error = new ErrorMessage("REGISTRATION_ERROR", "Username or session already exists or role invalid", sessionId, username, Instant.now().toString());
                    messagingTemplate.convertAndSend("/client/error/" + error);
                    log.debug("Failed to register user!", error);
                }

            }
            catch (Exception e) {
                log.debug("Invalid user header values: {}", e.getMessage());
            }
        } else {
            log.debug("Missing user headers in WebSocket connect for session {}", sessionId);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        User user = userService.getUserBySesssionid(headerAccessor.getSessionId());
        log.debug("Client disconnected. Session ID: {}, User: {}", headerAccessor.getSessionId(), user);
        sessionRegistry.unregister(event.getSessionId());
        userService.delete(user);
        log.debug("Unregistered and deleted user: {}" , user);

    }
}
