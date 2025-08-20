package com.drc.server.event;

import com.drc.server.entity.Game;
import com.drc.server.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GameEventListener {

    private final NotificationService notificationService;

    @EventListener
    public void onGameStarted(NewGameStartedEvent event) {
        Game game = event.getGame();
        notificationService.sendQuestionToAllClients(game);
    }

    @EventListener
    public void onNextQuestion(NextQuestionEvent event) {
        Game game = event.getGame();
        notificationService.sendQuestionToAllClients(game);
        notificationService.sendAllAnswersForAdminAndCheater(game);
    }
}
