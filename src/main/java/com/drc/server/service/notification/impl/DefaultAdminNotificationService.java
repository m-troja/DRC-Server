package com.drc.server.service.notification.impl;

import com.drc.server.dto.AnswerDto;
import com.drc.server.dto.UserDto;
import com.drc.server.dto.cnv.AnswerCnv;
import com.drc.server.dto.cnv.QuestionCnv;
import com.drc.server.dto.cnv.UserCnv;
import com.drc.server.entity.*;
import com.drc.server.service.*;
import com.drc.server.service.notification.AdminNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultAdminNotificationService implements AdminNotificationService {

    private final AnswerService answerService;
    private final SimpMessagingTemplate messagingTemplate;
    private final AnswerCnv answerCnv;
    private final UserCnv userCnv;
    private final UserService userService;
    private final RoleService roleService;

    private static final String clientAllAnswersEndpoint = "/queue/all-answers"; // Sends message for specific user
    private static final String adminEventEndpoint = "/queue/admin-event";

    public void sendAllAnswersForAdmin(Game game) {
        List<User> admins = userService.getUsersByRoleAndGame(roleService.getRoleByName(RoleService.ROLE_ADMIN), game);
        log.debug("Admins found: {}", admins);

        List<Answer> answers = answerService.getAnswersForQuestionId(game.getCurrentQuestionId());
        List<AnswerDto> answerDtos = answerCnv.converAnswersToAnswerDtos(answers);
        log.debug("answers found: {}", answers);
        log.debug("answerDtos found: {}", answerDtos);

        for (User admin : admins) {
            messagingTemplate.convertAndSendToUser(admin.getName(), clientAllAnswersEndpoint, answerDtos );
            log.debug("Sent answers to {} : {}", admin, answerDtos);
        }
    }

    public void notifyAdminThatNewUserConnected(User user) {
        List<User> adminsWithNoGame = userService.getUsersByRoleAndGame(roleService.getRoleByName(RoleService.ROLE_ADMIN), null);

        if (adminsWithNoGame.isEmpty()) {
            log.debug("Skip informing admin about user connected, since no admin is connected");
        }

        for ( User admin : adminsWithNoGame) {
            UserDto userDto = userCnv.convertUserToUserDto(user);
            messagingTemplate.convertAndSendToUser(admin.getName(), adminEventEndpoint, new NotificationToAdminAboutUser(ResponseType.USER_CONNECTED, userDto));
            log.debug("Informed admin about new user connected: {}, endpoint: {}", user, adminEventEndpoint);
        }
    }

    public void notifyAdminThatUserDisconnected(Integer userId) {
        User userDisconnected = userService.getUserById(userId);
        List<User> admins = userService.getUsersByRoleAndGame(roleService.getRoleByName(RoleService.ROLE_ADMIN), userDisconnected.getGame());

        if (admins.isEmpty()) {
            log.debug("Skip informing admin about user disconnected, since no admin is connected");
        }

        for ( User admin : admins) {
            UserDto userDto = userCnv.convertUserToUserDto(userDisconnected);
            messagingTemplate.convertAndSendToUser(admin.getName(), adminEventEndpoint, new NotificationToAdminAboutUser(ResponseType.USER_DISCONNECTED, userDto));
            log.debug("Informed admin about user disconnected: {}, endpoint: {}", userDisconnected, adminEventEndpoint);
        }
    }
}
