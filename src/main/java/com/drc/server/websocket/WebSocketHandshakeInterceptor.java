package com.drc.server.websocket;

import com.drc.server.entity.ErrorMessage;
import com.drc.server.entity.ErrorMessageType;
import com.drc.server.entity.User;
import com.drc.server.service.RoleService;
import com.drc.server.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.time.Instant;
import java.util.Map;
@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    private final UserService userService;
    private final RoleService roleService;
    private final WebSocketSessionRegistry sessionRegistry;
    private String ERROR_DESTINATION = "/client/error";
    private String ROLE_ADMIN_VALUE = "admin";
    public static String httpSessionIdParamName = "HTTP_SESSION_ID";
    private User user;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
        String username = servletRequest.getServletRequest().getParameter("username");
        String httpSessionId =  servletRequest.getServletRequest().getSession().getId();
        String role = servletRequest.getServletRequest().getParameter("role");
        attributes.put(httpSessionIdParamName, httpSessionId);

        log.debug("Handshake started, username: {}, role: {}, httpSessionId: {}", username, role, httpSessionId);
        log.debug("Handshake attributes before return: {}", attributes);

        if (username != null && httpSessionId != null)
        {
            if ( role.equals(ROLE_ADMIN_VALUE)) {
                log.debug("Role=admin detected!");
                user = new User(httpSessionId, username, 0.0, roleService.getRoleByName(RoleService.ROLE_ADMIN));
            }
            else {
                log.debug("Role=user");
                user = new User(httpSessionId, username, 0.0, roleService.getRoleByName(RoleService.ROLE_USER));
            }

            try
            {
                String userValidationResult = userService.save(user);

                if (userValidationResult.equals(UserService.VALIDATE_OK))
                {
                    sessionRegistry.register(httpSessionId, user);
                    log.debug("Registered: httpSessionId={}, username={}", httpSessionId, username);
                    return true;
                }
                else {
                    ErrorMessage error = new ErrorMessage( ErrorMessageType.REGISTRATION_FAILED, userValidationResult + ", httpSessionId: " + httpSessionId + ", username: " + username, Instant.now().toString());
//                    messagingTemplate.convertAndSend(ERROR_DESTINATION , error);
                    log.debug("Failed to register user! Error: {}", error);
                    return false;

                }

            }
            catch (Exception e) {
                log.debug("Invalid user header values: {}", e.getMessage());
            }
        } else {
            log.debug("Missing user headers in WebSocket connect for session {}", httpSessionId);
        }
        return false;

    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

        log.debug("afterHandshake request: {}, response: {}", request, response);
    }
}
