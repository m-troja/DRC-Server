package com.drc.server.event;

import com.drc.server.entity.Game;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class GameEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void publishNewGameStartedEvent(Game game) {
        applicationEventPublisher.publishEvent(new NewGameStartedEvent(this, game));
    }

    public void publishNextQuestionEvent(Game game) {
        applicationEventPublisher.publishEvent(new NextQuestionEvent(this, game));
    }
}
