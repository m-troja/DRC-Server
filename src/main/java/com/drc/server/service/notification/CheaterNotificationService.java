package com.drc.server.service.notification;

import com.drc.server.dto.CorrectAnswerResponseDto;
import com.drc.server.entity.Game;
import com.drc.server.entity.User;

import java.util.List;

public interface CheaterNotificationService {

    void sendAllAnswersForCheater(Game game);
    void sendCorrectAnswerResponseToCheaters(CorrectAnswerResponseDto answerDto, List<User> users);
}
