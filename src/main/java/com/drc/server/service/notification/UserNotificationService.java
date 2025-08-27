package com.drc.server.service.notification;

import com.drc.server.dto.AnswerDto;
import com.drc.server.dto.QuestionDto;
import com.drc.server.entity.*;

import java.util.List;

public interface UserNotificationService {

    void sendQuestionToAllClients(QuestionDto questionDto);
    void sendAnswerToUsers(AnswerDto answerDto, List<User> users);
    void sendKickRequest(KickRequest kickRequest);
    void sendAllAnswersToUsersInGame(List<AnswerDto> answers, List<User> users);
}
