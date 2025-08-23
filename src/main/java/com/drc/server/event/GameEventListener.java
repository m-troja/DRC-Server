package com.drc.server.event;

import com.drc.server.entity.Game;
import com.drc.server.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class GameEventListener {

    private final NotificationService notificationService;

    @EventListener
    public void onGameStarted(NewGameStartedEvent event) {
        log.debug("OnGameStarted {}", event);
        Game game = event.getGame();
        notificationService.sendQuestionToAllClients(game);
        notificationService.sendAllAnswersForAdminAndCheater(game);
    }

    @EventListener
    public void onNextQuestion(NextQuestionEvent event) {
        Game game = event.getGame();
        log.debug("onNextQuestion {}", event);
        notificationService.sendQuestionToAllClients(game);
        notificationService.sendAllAnswersForAdminAndCheater(game);
    }
}
