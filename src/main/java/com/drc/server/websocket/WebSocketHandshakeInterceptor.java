package com.drc.server.websocket;

import com.drc.server.entity.User;
import com.drc.server.service.RoleService;
import com.drc.server.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    private final UserService userService;
    private final RoleService roleService;
    private final @Lazy WebSocketSessionRegistry sessionRegistry;

    private static final String ROLE_ADMIN_VALUE = "admin";
    public static final String HTTP_SESSION_ID_PARAM_NAME = "HTTP_SESSION_ID";

    private final Map<String, Long> blockedSessions = new ConcurrentHashMap<>();

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;

        // Get username from http query
        String query = request.getURI().getQuery();
        for (String param : query.split("&")) {
            if (param.startsWith("username=")) {
                attributes.put("username", param.split("=")[1]);
            }
        }
        String username = (String) attributes.get("username");

        String httpSessionId = servletRequest.getServletRequest().getSession().getId();
        String role = servletRequest.getServletRequest().getParameter("role");
        attributes.put(HTTP_SESSION_ID_PARAM_NAME, httpSessionId);

        log.debug("Handshake started, username: {}, role: {}, httpSessionId: {}", username, role, httpSessionId);
        log.debug("Handshake attributes before return: {}", attributes);

        // Block repeated user's request
        Long blockedUntil = blockedSessions.get(httpSessionId);
        long now = System.currentTimeMillis();

        if (blockedUntil != null && blockedUntil > now) {
            log.debug("Handshake blocked for session {} until {}", httpSessionId, blockedUntil);
            setHttpStatus(response, HttpServletResponse.SC_FORBIDDEN);
            return false;
        } else {
            blockedSessions.remove(httpSessionId);
        }

        if (username != null && httpSessionId != null && role != null) {
            User user;
            if (role.equalsIgnoreCase(ROLE_ADMIN_VALUE)) {
                log.debug("Role=admin detected!");
                user = new User(httpSessionId, username, 0.0, roleService.getRoleByName(RoleService.ROLE_ADMIN));
            } else {
                log.debug("Role=user");
                user = new User(httpSessionId, username, 0.0, roleService.getRoleByName(RoleService.ROLE_USER));
            }

            try {
                String userValidationResult = userService.save(user);
                if (UserService.VALIDATE_OK.equals(userValidationResult)) {
                    sessionRegistry.register(httpSessionId, user);
                    log.debug("Registered: httpSessionId={}, username={}", httpSessionId, username);
                    return true;
                } else {
                    log.debug("Failed to register user! Error: {}", userValidationResult);
                    blockedSessions.put(httpSessionId, now + 5000);
                    setHttpStatus(response, HttpServletResponse.SC_CONFLICT);
                    return false;
                }
            } catch (Exception e) {
                log.debug("Invalid user header values: {}", e.getMessage());
                blockedSessions.put(httpSessionId, now + 5000);
                setHttpStatus(response, HttpServletResponse.SC_FORBIDDEN);
                return false;
            }
        } else {
            log.debug("Missing user headers in WebSocket connect for session {}", httpSessionId);
            setHttpStatus(response, HttpServletResponse.SC_BAD_REQUEST);
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }

    private void setHttpStatus(ServerHttpResponse response, int statusCode) {
        if (response instanceof ServletServerHttpResponse) {
            HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
            servletResponse.setStatus(statusCode);
        }
    }
}