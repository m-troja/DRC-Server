package com.drc.server.websocket;

import com.drc.server.entity.User;
import com.drc.server.service.DisconnectService;
import com.drc.server.service.UserService;
import com.drc.server.service.notification.AdminNotificationService;
import lombok.Getter;
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
@Getter
public class WebSocketSessionRegistry {

    private final Map<Integer, Long> lastPingMap = new ConcurrentHashMap<>();
    private final AdminNotificationService adminNotificationService;
    private final UserService userService;

    @Value("${timeout.miliseconds}")
    private Long TIMEOUT_MILLIS; // 20 s

    public void register(String stompSessionId, Integer userId) {
        lastPingMap.put(userId, System.currentTimeMillis());
        log.debug("Updated last ping for user {}: {}", userService.getUserById(userId).getName(), System.currentTimeMillis());
    }

    public void unregister(Integer userId) {
        User user = userService.getUserById(userId);
        log.debug("Unregistering user {}", user);
        lastPingMap.remove(userId);
        log.debug("Notifying admin about disconnecting: {}", user);
        adminNotificationService.notifyAdminThatUserDisconnected(userId);
        log.debug("Trying to delete {}", user);
        try {
            userService.delete(user);
        } catch (Exception e) {
            log.debug("Error deleting user from DB: {}", user);
        }
        log.debug("Deleted user from DB: {}", user);}

    public void updateLastPingMap(Integer userId, Long newTime) {
        lastPingMap.put(userId, newTime);
        log.debug("Updated last ping for {}: {}", userService.getUserById(userId).getName(), newTime);
        log.debug("Map {userId=TimeOfLastPing} {}", lastPingMap);
    }

    // Automatically disconnect inactive users
    @Scheduled(fixedDelay = 5000)
    public void disconnectInactiveUsers() {

        if (!getLastPingMap().isEmpty())
        {
            log.debug("Checking inactive users...");

            Long now = System.currentTimeMillis();
            for ( Integer userId : lastPingMap.keySet()) {
                if (now - lastPingMap.get(userId) > TIMEOUT_MILLIS) {
                    log.debug("Found inactive userId: {}", userId);
                    unregister(userId);
                }
            }
        }
    }
}

