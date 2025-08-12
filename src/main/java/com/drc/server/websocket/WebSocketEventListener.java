package com.drc.server.websocket;

import com.drc.server.entity.User;
import com.drc.server.service.RoleService;
import com.drc.server.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Component
public class WebSocketEventListener {
    private final WebSocketSessionRegistry sessionRegistry;
    private final RoleService roleService;
    private final UserService userService;

    public WebSocketEventListener(WebSocketSessionRegistry sessionRegistry, RoleService roleService, UserService userService) {
        this.sessionRegistry = sessionRegistry;
        this.roleService = roleService;
        this.userService = userService;
    }

    /*
              {
                 username: 'john',
                 userId: '123',
                 role: 'USER'
            },
             */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String username = headerAccessor.getFirstNativeHeader("username");
        String roleStr = headerAccessor.getFirstNativeHeader("role");

        if (username != null && roleStr != null) {
            try {
                User user = new User(
                        username,
                        0.0,
                        roleService.getRoleByName(roleStr) // enum Role
                );
                log.debug("Connected: sessionId={}, username={}, roleStr={}", sessionId, username, roleStr);
                sessionRegistry.register(sessionId, user);
                userService.save(user);
            } catch (Exception e) {
                log.debug("Invalid user header values: {}", e.getMessage());
            }
        } else {
            log.debug("Missing user headers in WebSocket connect for session {}", sessionId);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        log.debug("Client disconnected. Session ID: {}", headerAccessor.getSessionId());
    }
}
