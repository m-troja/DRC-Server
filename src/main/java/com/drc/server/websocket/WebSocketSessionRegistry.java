package com.drc.server.websocket;

import com.drc.server.entity.User;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketSessionRegistry {
    private final Map<String, User> sessions = new ConcurrentHashMap<>();

    public void register(String sessionId, User user) {
        sessions.put(sessionId, user);
    }

    public void unregister(String sessionId) {
        sessions.remove(sessionId);
    }

    public User getUser(String sessionId) {
        return sessions.get(sessionId);
    }

    public Map<String, User> getAllUsers() {
        return sessions;
    }
}

