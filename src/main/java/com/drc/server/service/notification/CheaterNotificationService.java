package com.drc.server.service.notification;

import com.drc.server.entity.Game;

public interface CheaterNotificationService {

    void sendAllAnswersForCheater(Game game);
}
