package com.drc.server.service.notification;

import com.drc.server.dto.CorrectAnswerResponseDto;
import com.drc.server.dto.UserDto;
import com.drc.server.entity.Game;
import com.drc.server.entity.Response;
import com.drc.server.entity.ResponseType;
import com.drc.server.entity.User;

import java.util.List;

public interface CheaterNotificationService {

    void sendAllAnswersForCheater(Game game);
    void sendCorrectAnswerResponseToCheaters(CorrectAnswerResponseDto answerDto, List<User> users);
    void updateUsersObjects(List<UserDto> userDtos, List<User> users);
    void tellPlayerIfHeIsCheater(Response response, List<User> users);
}
