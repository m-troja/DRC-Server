package com.drc.server.service.notification;

import com.drc.server.dto.CorrectAnswerResponseDto;
import com.drc.server.dto.UserDto;
import com.drc.server.entity.Game;
import com.drc.server.entity.User;

import java.util.List;

public interface AdminNotificationService {

    void notifyAdminThatNewUserConnected(User user);
    void notifyAdminThatUserDisconnected(Integer userId);
    void notifyAdminAboutShootPlayer(UserDto userDto, List<User> users);
    void sendAllAnswersForAdmin(Game game);
    void sendCorrectAnswerResponseToAdmins(CorrectAnswerResponseDto answerDto, List<User> users);
}
