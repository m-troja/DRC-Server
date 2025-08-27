package com.drc.server.service.notification.impl;

import com.drc.server.dto.AnswerDto;
import com.drc.server.dto.CorrectAnswerResponseDto;
import com.drc.server.dto.QuestionDto;
import com.drc.server.entity.*;
import com.drc.server.service.notification.UserNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultUserNotificationService implements UserNotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private static final String clientQuestionEndpoint = "/client/question";
    private static final String queueAnswerEndpoint = "/queue/answer";
    private static final String queueAnswersEndpoint = "/queue/all-answers";
    private static final String kickEventEndpoint = "/queue/kick";

    public void sendQuestionToAllClients(QuestionDto questionDto) {

        messagingTemplate.convertAndSend(clientQuestionEndpoint, questionDto);
        log.debug("Sent question to {} : {}", clientQuestionEndpoint,  questionDto);
    }

    public void sendAnswerToUsers(AnswerDto answerDto, List<User> users) {
         log.debug("sendAnswerToUsers: {}, {} ", answerDto, users);
        for (User user : users) {
            messagingTemplate.convertAndSendToUser(user.getName(), queueAnswerEndpoint, answerDto);
            log.debug("Sent {} to {}, ws: {}", answerDto, user, queueAnswerEndpoint);
        }
    }

    public void sendKickRequest(KickRequest kickRequest) {
        messagingTemplate.convertAndSendToUser(kickRequest.username(), kickEventEndpoint, kickRequest);
        log.debug("Sent kick request: user {}, endpoint {}, {}", kickRequest.username(), kickEventEndpoint, kickRequest);
    }

    public void sendCorrectAnswerResponseToUsers(CorrectAnswerResponseDto answerDto, List<User> users) {
        log.debug("sendCorrectAnswerResponseToUsers: {}, {} ", answerDto, users);
        for (User user : users) {
            messagingTemplate.convertAndSendToUser(user.getName(), clientAnswerEndpoint, answerDto);
            log.debug("Sent {} to {}, ws: {}", answerDto, user, clientAnswerEndpoint);

        }
    }
}
