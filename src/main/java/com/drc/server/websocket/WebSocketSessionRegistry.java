package com.drc.server.websocket;

import com.drc.server.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Slf4j
@Component
public class WebSocketSessionRegistry {

    private final Map<String, User> mapOfSessionIdAndUser = new ConcurrentHashMap<>();
    private final Map<User, Long> lastPingMap = new ConcurrentHashMap<>();
    private final Map<String, User> sessions = new ConcurrentHashMap<>();

    @Lazy
    @Autowired
    WebSocketEventListener webSocketEventListener;

    @Value("${timeout.miliseconds}")
    private Long TIMEOUT_MILLIS; // 20 s

    public void register(String sessionId, User user) {
        lastPingMap.put(user, System.currentTimeMillis());
        sessions.put(sessionId, user);
        log.debug("Registered {}, time {}", user, System.currentTimeMillis());
        log.debug("TIMEOUT_MILLIS {}", TIMEOUT_MILLIS);
    }

    public void unregister(String sessionId) {

    }

    public void updateLastPingMap(User user, Long newTime) {
        lastPingMap.put(user, newTime);
        log.debug("Updated last ping for user {}: {}", user, newTime);
        log.debug("Entire map {}", lastPingMap);
    }

    // Automatically disconnect inactive users
    @Scheduled(fixedDelay = 5000)
    public void disconnectInactiveUsers() {
        log.debug("Checking inactive users...");

        Long now = System.currentTimeMillis();
        for ( User user : lastPingMap.keySet()) {
            if (now - lastPingMap.get(user) > TIMEOUT_MILLIS) {
                log.debug("Found inactive user: {}", user);
                lastPingMap.remove(user);
                unregister(user.getStompSessionId());
                webSocketEventListener.disconnectUser(user);
            }
        }
    }

    public Map<String, User> getAllUsers() {
        return mapOfSessionIdAndUser;
    }

    public Long getLastPingOfUser(User user) {
        return lastPingMap.get(user);
    }
    public User getUserFromSessionBySessionId(String session) {
        return sessions.get(session);
    }

}

