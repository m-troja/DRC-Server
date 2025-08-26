package com.drc.server.event;

import com.drc.server.dto.QuestionDto;
import com.drc.server.dto.cnv.QuestionCnv;
import com.drc.server.entity.Game;
import com.drc.server.entity.Question;
import com.drc.server.service.QuestionService;
import com.drc.server.service.notification.AdminNotificationService;
import com.drc.server.service.notification.CheaterNotificationService;
import com.drc.server.service.notification.UserNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class GameEventListener {

    private final UserNotificationService userNotificationService;
    private final AdminNotificationService adminNotificationService;
    private final CheaterNotificationService cheaterNotificationService;
    private final QuestionCnv questionCnv;
    private final QuestionService questionService;

    @EventListener
    public void onGameStarted(NewGameStartedEvent event) {
        log.debug("OnGameStarted {}", event);
        Game game = event.getGame();
        Question question = questionService.getQuestion(game.getCurrentQuestionId());
        QuestionDto questionDto = questionCnv.convertQuestionToQuestionDto(question);

        userNotificationService.sendQuestionToAllClients(questionDto);
        adminNotificationService.sendAllAnswersForAdmin(game);
        cheaterNotificationService.sendAllAnswersForCheater(game);
    }

    @EventListener
    public void onNextQuestion(NextQuestionEvent event) {
        Game game = event.getGame();
        log.debug("onNextQuestion {}", event);
        Question question = questionService.getQuestion(game.getCurrentQuestionId());
        QuestionDto questionDto = questionCnv.convertQuestionToQuestionDto(question);

        userNotificationService.sendQuestionToAllClients(questionDto);
        adminNotificationService.sendAllAnswersForAdmin(game);
        cheaterNotificationService.sendAllAnswersForCheater(game);    }
}
