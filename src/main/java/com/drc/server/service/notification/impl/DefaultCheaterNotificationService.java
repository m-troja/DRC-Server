package com.drc.server.service.notification.impl;

import com.drc.server.dto.AnswerDto;
import com.drc.server.dto.cnv.AnswerCnv;
import com.drc.server.dto.cnv.QuestionCnv;
import com.drc.server.dto.cnv.UserCnv;
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

    private static final String clientAllAnswersEndpoint = "/queue/all-answers"; // Sends message for specific user


    public void sendAllAnswersForCheater(Game game) {
        List<User> cheaters = userService.getUsersByRoleAndGame(roleService.getRoleByName(RoleService.ROLE_CHEATER),game);

        List<Answer> answers = answerService.getAnswersForQuestionId(game.getCurrentQuestionId());
        List<AnswerDto> answerDtos = answerCnv.converAnswersToAnswerDtos(answers);
        log.debug("answers found: {}", answers);
        log.debug("answerDtos found: {}", answerDtos);

        for (User cheater : cheaters) {
            messagingTemplate.convertAndSendToUser(cheater.getName(), clientAllAnswersEndpoint, answerDtos );
            log.debug("Sent answers to {} : {}", cheater, answerDtos);
        }
    }
}
