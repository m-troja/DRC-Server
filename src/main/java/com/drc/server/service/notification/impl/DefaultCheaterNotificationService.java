package com.drc.server.service.notification.impl;

import com.drc.server.dto.AnswerDto;
import com.drc.server.dto.CorrectAnswerResponseDto;
import com.drc.server.dto.UserDto;
import com.drc.server.dto.cnv.AnswerCnv;
import com.drc.server.entity.*;
import com.drc.server.service.*;
import com.drc.server.service.notification.CheaterNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultCheaterNotificationService implements CheaterNotificationService {


    private final AnswerService answerService;
    private final SimpMessagingTemplate messagingTemplate;
    private final AnswerCnv answerCnv;
    private final UserService userService;
    private final RoleService roleService;
    private static final String usersEndpoint = "/queue/users";

    private static final String clientAllAnswersEndpoint = "/queue/all-answers"; // Sends message for specific user
    private static final String clientAnswerEndpoint = "/queue/answer";
    private static final String areYouCheaterEndpoint = "/queue/are-you-cheater";


    public void sendAllAnswersForCheater(Game game) {
        List<User> cheaters = userService.getUsersByRoleAndGame(roleService.getRoleByName(RoleService.ROLE_CHEATER),game);

        List<Answer> answers = answerService.getAnswersForQuestionId(game.getCurrentQuestionId());
        List<AnswerDto> answerDtos = answerCnv.convertAnswersToAnswerDtos(answers);
        log.debug("answers found: {}", answers);
        log.debug("answerDtos found: {}", answerDtos);

        for (User cheater : cheaters) {
            messagingTemplate.convertAndSendToUser(cheater.getName(), clientAllAnswersEndpoint, answerDtos );
            log.debug("Sent answers to {} : {}", cheater, answerDtos);
        }
    }

    public void sendCorrectAnswerResponseToCheaters(CorrectAnswerResponseDto answerDto, List<User> users) {
        log.debug("sendCorrectAnswerResponseToCheaters: {}, {} ", answerDto, users);
        for (User user : users) {
            messagingTemplate.convertAndSendToUser(user.getName(), clientAnswerEndpoint, answerDto);
            log.debug("Sent {} to {}, ws: {}", answerDto, user, clientAnswerEndpoint);
        }
    }

    public void updateUsersObjects(List<UserDto> userDtos, List<User> users) {
        log.debug("updateUsersObjects: {}, {} ", userDtos, users);
        for (User user : users) {
            messagingTemplate.convertAndSendToUser(user.getName(), usersEndpoint, userDtos);
            log.debug("Sent {} to {}, ws: {}", userDtos, user, usersEndpoint);
        }
    }

    public void tellPlayerIfHeIsCheater(Response response,  List<User> users) {
        log.debug("tellPlayerIfHeIsCheater: {}, {} ", response, users);
        for (User user : users) {
            messagingTemplate.convertAndSendToUser(user.getName(), areYouCheaterEndpoint, response);
            log.debug("Sent {} to {}, ws: {}", response, user, areYouCheaterEndpoint);
        }
    }
}
