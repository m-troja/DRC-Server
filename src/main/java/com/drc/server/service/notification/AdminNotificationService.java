package com.drc.server.service.notification;

import com.drc.server.entity.Game;
import com.drc.server.entity.User;

public interface AdminNotificationService {

    void notifyAdminThatNewUserConnected(User user);
    void notifyAdminThatUserDisconnected(Integer userId);
    void sendAllAnswersForAdmin(Game game);
}
