package com.drc.server.event;

import com.drc.server.entity.Game;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationEvent;

@Getter
public class NewGameStartedEvent extends ApplicationEvent {

    private final Game game;

    public NewGameStartedEvent(Object source, Game game) {
        super(source);
        this.game = game;
    }
}
