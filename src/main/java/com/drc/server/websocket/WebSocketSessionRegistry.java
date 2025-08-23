package com.drc.server.websocket;

import com.drc.server.entity.User;
import com.drc.server.service.UserService;
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
    private final Map<Integer, Long> lastPingMap = new ConcurrentHashMap<>();
    private final Map<String, User> sessions = new ConcurrentHashMap<>();
    private final UserService userService;

    @Lazy
    @Autowired
    WebSocketEventListener webSocketEventListener;

    @Value("${timeout.miliseconds}")
    private Long TIMEOUT_MILLIS; // 20 s

    public void register(String stompSessionId, Integer userId) {
        lastPingMap.put(userId, System.currentTimeMillis());
        log.debug("Updated last ping for user {}: {}", userId, System.currentTimeMillis());

        sessions.put(stompSessionId, userService.getUserById(userId));
        log.debug("Put into sessions sessionId {},{}", stompSessionId, userService.getUserById(userId));
        log.debug("Registered userId {}, time {}", userId, System.currentTimeMillis());
    }

    public void unregister(String sessionId) {
        log.debug("Unregister sessionId {}", sessionId);
        sessions.remove(sessionId);
    }

    public void updateLastPingMap(Integer userId, Long newTime) {
        lastPingMap.put(userId, newTime);
        log.debug("Updated last ping for userId {}: {}", userId, newTime);
        log.debug("Entire map lastPingUser ID=Time {}", lastPingMap);
    }

    // Automatically disconnect inactive users
    @Scheduled(fixedDelay = 5000)
    public void disconnectInactiveUsers() {

        if (!getAllSessions().isEmpty())
        {
            log.debug("Checking inactive users...");

            Long now = System.currentTimeMillis();
            for ( Integer userId : lastPingMap.keySet()) {
                if (now - lastPingMap.get(userId) > TIMEOUT_MILLIS) {
                    log.debug("Found inactive userId: {}", userId);

                    log.debug("Remove userId {} from lastPingMap", userId);
                    lastPingMap.remove(userId);
                    log.debug("Call Unregister with stompSessionId {}", userService.getUserById(userId).getStompSessionId());
                    unregister(userService.getUserById(userId).getStompSessionId());
                    log.debug("Go to webSocketEventListener.disconnectUser({})", userService.getUserById(userId));
                    webSocketEventListener.disconnectUser(userService.getUserById(userId));
                }
            }
        }
    }

    public Map<String, User> getAllSessions() {
        return sessions;
    }

    public Long getLastPingOfUser(Integer userId) {
        return lastPingMap.get(userId);
    }
    public User getUserFromSessionBySessionId(String session) {
        return sessions.get(session);
    }

}

