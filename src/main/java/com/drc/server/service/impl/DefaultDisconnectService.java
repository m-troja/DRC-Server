package com.drc.server.service.impl;

import com.drc.server.entity.User;
import com.drc.server.service.DisconnectService;
import com.drc.server.service.UserService;
import com.drc.server.service.notification.AdminNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultDisconnectService implements DisconnectService {

    private final UserService userService;
    private final AdminNotificationService adminNotificationService;

    public void disconnect(Integer userId) {
        User user = userService.getUserById(userId);

        if (user == null) {
            log.debug("No user found ");
            return;
        }
        log.debug("Notifying admin about disconnecting: {}", user);
        adminNotificationService.notifyAdminThatUserDisconnected(userId);
        log.debug("notifyAdminThatUserDisconnected: {}", user);
        try {
            userService.delete(user);
        } catch (Exception e) {
            log.debug("Error deleting user from DB: {}", user);
        }
        log.debug("Deleted user from DB: {}", user);
    }


}
