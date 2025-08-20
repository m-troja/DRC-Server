package com.drc.server.event;

import com.drc.server.entity.Game;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class NextQuestionEvent extends ApplicationEvent {

    private final Game game;

    public NextQuestionEvent(Object source, Game game) {
        super(source);
        this.game = game;
    }
}
