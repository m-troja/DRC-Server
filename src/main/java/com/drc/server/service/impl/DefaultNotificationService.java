package com.drc.server.service.impl;

import com.drc.server.dto.AnswerDto;
import com.drc.server.dto.QuestionDto;
import com.drc.server.dto.cnv.AnswerCnv;
import com.drc.server.dto.cnv.QuestionCnv;
import com.drc.server.entity.*;
import com.drc.server.persistence.QuestionRepo;
import com.drc.server.service.*;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultNotificationService implements NotificationService {


    private final AnswerService answerService;
    private final QuestionService questionService;
    private final SimpMessagingTemplate messagingTemplate;
    private final AnswerCnv answerCnv;
    private final QuestionCnv questionCnv;
    private final UserService userService;
    private final RoleService roleService;
    private final GameService gameService;

    private static final String clientQuestionEndpoint = "/client/question";
    private static final String clientAllAnswersEndpoint = "/queue/all-answers"; // Sends message for specific user
    private static final String clientAnswerEndpoint = "/queue/answer";
    private static final String adminEventEndpoint = "/queue/admin-event";
    private static final String newUserConnectedMessage = "New user connected";
    private static final String userDisconnectedMessage = "User disconnected";

    public void sendQuestionToAllClients(Game game) {
        Question question = questionService.getQuestion(game.getCurrentQuestionId());
        QuestionDto questionDto = questionCnv.convertQuestionToQuestionDto(question);
        messagingTemplate.convertAndSend(clientQuestionEndpoint, questionDto);
        log.debug("Sent question to {} : {}", clientQuestionEndpoint,  questionDto);
    }

    public void sendAllAnswersForAdminAndCheater(Game game) {
        List<User> admins = userService.getUsersByRoleAndGame(roleService.getRoleByName(RoleService.ROLE_ADMIN), game);
        List<User> cheaters = userService.getUsersByRoleAndGame(roleService.getRoleByName(RoleService.ROLE_CHEATER),game);
        log.debug("Admins found: {}", admins);
        log.debug("Cheaters found: {}", cheaters);

        List<Answer> answers = answerService.getAnswersForQuestionId(game.getCurrentQuestionId());
        List<AnswerDto> answerDtos = answerCnv.converAnswersToAnswerDtos(answers);
        log.debug("answers found: {}", answers);
        log.debug("answerDtos found: {}", answerDtos);

        for (User admin : admins) {
            messagingTemplate.convertAndSendToUser(admin.getName(), clientAllAnswersEndpoint, answerDtos );
            log.debug("Sent answers to {} : {}", admin, answerDtos);
        }
        for (User cheater : cheaters) {
            messagingTemplate.convertAndSendToUser(cheater.getName(), clientAllAnswersEndpoint, answerDtos );
            log.debug("Sent answers to {} : {}", cheater, answerDtos);
        }
    }

    public void notifyAdminThatNewUserConnected(User user) {
        List<User> adminsWithNoGame = userService.getUsersByRoleAndGame(roleService.getRoleByName(RoleService.ROLE_ADMIN), null);
        HashMap<String, String> payload = new HashMap<>();
        payload.put(newUserConnectedMessage, user.getName());

        if (adminsWithNoGame.isEmpty()) {
            log.debug("Skip informing admin about user connected, since no admin is connected");
        }

        for ( User admin : adminsWithNoGame) {
            messagingTemplate.convertAndSendToUser(admin.getName(), adminEventEndpoint, payload);
            log.debug("Informed admin about new user connected: {}, endpoint: {}", user, adminEventEndpoint);
            log.debug("payload: {}",payload);
        }
        payload.clear();;
    }

    public void notifyAdminThatUserDisconnected(User user) {
        List<User> admins = userService.getUsersByRoleAndGame(roleService.getRoleByName(RoleService.ROLE_ADMIN), user.getGame());
        HashMap<String, String> payload = new HashMap<>();
        payload.put(userDisconnectedMessage, user.getName());

        if (admins.isEmpty()) {
            log.debug("Skip informing admin about user disconnected, since no admin is connected");
        }

        for ( User admin : admins) {
            messagingTemplate.convertAndSendToUser(admin.getName(), adminEventEndpoint, payload);
            log.debug("Informed admin about user disconnected: {}, endpoint: {}", user, adminEventEndpoint);
            log.debug("payload: {}",payload);
        }
        payload.clear();;
    }

    public void sendAnswerToUsers(AnswerRequest ar) {
        Game game;
        if (gameService.getGameById(ar.gameId()) == null) {
            log.debug("Game is null, gameId = {}", ar.gameId());
            return;
        }
        else {
            game = gameService.getGameById(ar.gameId());
        }

        Answer answer;
        if (answerService.getAnswerForQuestionByValueAndGameId(ar.value(), game.getCurrentQuestionId()) == null) {
            log.debug("Answer is null, answer.value {}, gameId {}", ar.value(), game.getCurrentQuestionId());
            return;
        }
        else {
            answer = answerService.getAnswerForQuestionByValueAndGameId(ar.value(), game.getCurrentQuestionId());
        }

        AnswerDto answerDto = answerCnv.converAnswerToAnswerDto(answer);
        List<User> users = userService.getUsersByRoleAndGame(roleService.getRoleByName(RoleService.ROLE_USER), game);
        log.debug("sendAnswerToUsers: {}, {}, {}, {} ", game, answer, answerDto, users);
        for (User user : users) {
            messagingTemplate.convertAndSendToUser(user.getName(), clientAnswerEndpoint, answerDto);
            log.debug("Sent {} to {}, ws: {}", answerDto, user, clientAnswerEndpoint);
        }
    }
}
