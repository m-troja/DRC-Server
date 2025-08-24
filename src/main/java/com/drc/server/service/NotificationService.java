package com.drc.server.service;

import com.drc.server.entity.AnswerRequest;
import com.drc.server.entity.Game;
import com.drc.server.entity.KickRequest;
import com.drc.server.entity.User;

public interface NotificationService {

    void notifyAdminThatNewUserConnected(User user);
    void notifyAdminThatUserDisconnected(User user);
    void sendAllAnswersForAdminAndCheater(Game game);
    void sendQuestionToAllClients(Game game);
    void sendAnswerToUsers(AnswerRequest answerRequest);
    void sendKickRequest(KickRequest kickRequest);

}
